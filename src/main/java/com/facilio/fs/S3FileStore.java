package com.facilio.fs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.HttpMethod;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.util.ImageScaleUtil;

public class S3FileStore extends FileStore {

	private static final String PRIVATE_KEY_FILE_PATH = "/home/ubuntu/pk/pk-APKAJUH5UCWNSYC4DOSQ.pem";
	private static final long EXPIRATION = 24 * 60* 60 * 1000;

	public S3FileStore(long orgId, long userId) {
		super(orgId, userId);
	}
	
	private String bucketName;
	private String rootPath;
	private static Logger log = LogManager.getLogger(S3FileStore.class.getName());

	@Override
	protected String getRootPath() {
		this.rootPath = getOrgId() + File.separator + "files";
		return rootPath;
	}

	private String getBucketName() {
		if (this.bucketName == null) {
			this.bucketName = AwsUtil.getConfig("s3.bucket.name");
		}
		return this.bucketName;
	}
	
	@Override
	public long addFile(String fileName, File file, String contentType) throws Exception {
		long fileId = addDummyFileEntry(fileName);
		String filePath = getRootPath() + File.separator + fileId+"-"+fileName;
		long fileSize = file.length();
		
	    try {
	    	PutObjectResult rs = AwsUtil.getAmazonS3Client().putObject(getBucketName(), filePath, file);
	    	if (rs != null) {
	    		updateFileEntry(fileId, fileName, filePath, fileSize, contentType);
	    		return fileId;
	    	}
	    	else {
	    		deleteFileEntry(fileId);
	    		return -1;
	    	}
	    } catch (Exception e) {
	    	deleteFileEntry(fileId);
	    	throw e;
	    }
	}
	
	public static String getURL( String bucket, String filePath, File file) throws Exception {
		AmazonS3Client s3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).withCredentials(AwsUtil.getAWSCredentialsProvider()).build();
    	s3Client.putObject(bucket, filePath, file);
		return s3Client.getResourceUrl(bucket, filePath);
	}

	@Override
	public long addFile(String fileName, File file, String contentType, int[] resize) throws Exception {
		long fileId = this.addFile(fileName, file, contentType);
		
		for (int resizeVal : resize) {
			try {
				if (contentType.contains("image/")) {
					// Image resizing...
					
					FileInputStream fis = new FileInputStream(file);
					BufferedImage imBuff = ImageIO.read(fis);
					BufferedImage out = ImageScaleUtil.resizeImage(imBuff, resizeVal, resizeVal);
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(out, "png", baos);
					baos.flush();
					byte[] imageInByte = baos.toByteArray();
					baos.close();
					ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
					
					String resizedFilePath = getRootPath() + File.separator + fileId+"-resized-"+resizeVal+"x"+resizeVal;
					
			    	PutObjectResult rs = AwsUtil.getAmazonS3Client().putObject(getBucketName(), resizedFilePath, bis, null);
			    	if (rs != null) {
			    		addResizedFileEntry(fileId, resizeVal, resizeVal, resizedFilePath, imageInByte.length, "image/png");
			    	}
				}
		    } catch (Exception e) {
		    	log.info("Exception occurred ", e);
		    }
		}
		return fileId;
	}
	
	@Override
	public long addFile(String fileName, String content, String contentType) throws Exception {
		long fileId = addDummyFileEntry(fileName);
		String filePath = getRootPath() + File.separator + fileId+"-"+fileName;
		long fileSize = content.length();
		
	    try {
	    	PutObjectResult rs = AwsUtil.getAmazonS3Client().putObject(getBucketName(), filePath, content);
	    	if (rs != null) {
	    		updateFileEntry(fileId, fileName, filePath, fileSize, contentType);
	    		return fileId;
	    	}
	    	else {
	    		deleteFileEntry(fileId);
	    		return -1;
	    	}
	    } catch (Exception e) {
	    	deleteFileEntry(fileId);
	    	throw e;
	    }
	}

	@Override
	public InputStream readFile(long fileId) throws Exception {
		FileInfo fileInfo = getFileInfo(fileId);
		return readFile(fileInfo);
	}
	@Override
	public InputStream readFile(FileInfo fileInfo) throws Exception {
				if (fileInfo == null) {
			return null;
		}
		S3Object so = AwsUtil.getAmazonS3Client().getObject(getBucketName(), fileInfo.getFilePath());
		return so.getObjectContent();
	} 
	@Override
	public InputStream readFile(long fileId, int height, int width) throws Exception {
		
		FileInfo fileInfo = getResizedFileInfo(fileId, width, height);
		if (fileInfo == null) {
			return null;
		}
		S3Object so = AwsUtil.getAmazonS3Client().getObject(getBucketName(), fileInfo.getFilePath());
		return so.getObjectContent();
	}

	@Override
	public boolean deleteFile(long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo == null) {
			return false;
		}
		
//		AwsUtil.getAmazonS3Client().deleteObject(getBucketName(), fileInfo.getFilePath());
		return  deleteFiles(Collections.singletonList(fileId));
	}

	@Override
	public boolean deleteFiles(List<Long> fileId) throws Exception {
		
		/*List<String> filePathList = getFilePathList(fileId);
		
		try {
			DeleteObjectsRequest dor = new DeleteObjectsRequest(getBucketName())
					.withKeys(filePathList.toArray(new String[filePathList.size()]));
			AwsUtil.getAmazonS3Client().deleteObjects(dor);
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}*/
		return markAsDeleted(fileId) > 0;
	}

	@Override
	public boolean renameFile(long fileId, String newName) throws Exception {
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo == null) {
			return false;
		}

		String oldFilePath = fileInfo.getFilePath();
		String newFilePath = getRootPath() + File.separator + fileId + "-" + newName;
		
		AwsUtil.getAmazonS3Client().copyObject(getBucketName(), oldFilePath, getBucketName(), newFilePath);
		// update db entry
		updateFileEntry(fileId, newName, newFilePath, fileInfo.getFileSize(), fileInfo.getContentType());
		
		// deleting old file from s3
		AwsUtil.getAmazonS3Client().deleteObject(getBucketName(), oldFilePath);
		return true;
	}
	
//	@Override
//	public String getPrivateUrl(long fileId) throws Exception {
//		return AwsUtil.getConfig("app.url")+"/files/preview/" + fileId;
//	}
//	
//	@Override
//	public String getPrivateUrl(long fileId, int width) throws Exception {
//		return AwsUtil.getConfig("app.url")+"/files/preview/" + fileId +"?width=" + width;
		
//		FileInfo fileInfo = getResizedFileInfo(fileId, width, width);
//		long currentTime=System.currentTimeMillis();
//		long bufferTime=180000;
//		boolean insertEntry=false;
//		
//		if (fileInfo == null) {
//			 fileInfo = getFileInfo(fileId);
//			 if (fileInfo == null) {//invalid fileid scenario
//				 return null;
//			 }
//			 insertEntry=true;
//		}
//		else {
//			ResizedFileInfo rfi= (ResizedFileInfo)fileInfo;
//			String url= rfi.getUrl();
//			long expiryTime= rfi.getExpiryTime();
//			
//			long thresholdTime=currentTime+bufferTime; //adding 3 minutes buffer time to avoid timing issues
//			if(thresholdTime<expiryTime && url!=null) {
//				return url;
//			}
//			//if here means... need to fetch url & update entry..
//		}
//		
//		String url= fetchUrl(fileInfo, getExpiration(), false);
//		if(url==null) {
//			return url;
//		}
//
//		ResizedFileInfo rfi=(ResizedFileInfo)fileInfo;
//		rfi.setUrl(url);
//		rfi.setExpiryTime(currentTime+getExpiration());
//		rfi.setWidth(width);
//		rfi.setHeight(width);
//		
//		if(insertEntry) {
//			addResizedFileEntry(rfi);
//		}
//		else {
//			updateResizedFileEntry(rfi);
//		}
//		return url;
//	}
	
	@Override
	public String getDownloadUrl(long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo != null) {
			return fetchDownloadUrl(fileInfo, getExpiration());
		}
		return null;
	}

	private String fetchUrl(FileInfo fileInfo, long expiration, boolean isDownloadable) {

		if (AwsUtil.isDevelopment()) {

			GeneratePresignedUrlRequest generatePresignedUrlRequest =
					new GeneratePresignedUrlRequest(getBucketName(), fileInfo.getFilePath());
			generatePresignedUrlRequest.setMethod(HttpMethod.GET);

			Date expiry = new Date();
			expiry.setTime(expiry.getTime() + expiration);
			generatePresignedUrlRequest.setExpiration(expiry);

			if (fileInfo.getContentType() != null) {
				ResponseHeaderOverrides resHeaders = new ResponseHeaderOverrides();
				resHeaders.setContentType(fileInfo.getContentType());
				generatePresignedUrlRequest.setResponseHeaders(resHeaders);
			}

			URL url = AwsUtil.getAmazonS3Client().generatePresignedUrl(generatePresignedUrlRequest);
			return url.toString();
		} else {
			try {
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
				String encodedUrl = URLEncoder.encode(fileInfo.getFilePath(), "UTF-8");
				String s3ObjectKey = encodedUrl+"?response-content-type="+URLEncoder.encode(fileInfo.getContentType(), "UTF-8");
				if(isDownloadable) {
					s3ObjectKey = s3ObjectKey +"&response-content-disposition="+URLEncoder.encode("attachment; filename="+fileInfo.getFileName(),"UTF-8");
				}
				String keyPairId = AwsUtil.getConfig("key.pair.id");
				return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(SignerUtils.Protocol.https, AwsUtil.getConfig("files.url"), new File(PRIVATE_KEY_FILE_PATH), s3ObjectKey, keyPairId, new Date(System.currentTimeMillis()+getExpiration()));
			} catch (IOException | InvalidKeySpecException e) {
				log.info("Exception while creating signed Url", e);
			}
		}
		return null;
	}
	
	private String fetchDownloadUrl(FileInfo fileInfo, long expiration) {
		return fetchUrl(fileInfo, expiration, true);
	}
	
	
	private long getExpiration() {
		return EXPIRATION;
	}

	public static void main(String[] args) {

	}
}
