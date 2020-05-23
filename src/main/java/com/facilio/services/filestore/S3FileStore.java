package com.facilio.services.filestore;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.facilio.accounts.util.AccountUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.iterators.CollatingIterator;
import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.HttpMethod;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.ImageScaleUtil;
import com.facilio.fs.FileInfo;

public class S3FileStore extends FileStore {
	private static Logger log = LogManager.getLogger(S3FileStore.class.getName());
	private static AmazonS3 AWS_S3_CLIENT = null;
	private static final String PRIVATE_KEY_FILE_PATH = "/home/ubuntu/pk/pk-APKAJUH5UCWNSYC4DOSQ.pem";
	private static final long EXPIRATION = 48 * 60* 60 * 1000;


	S3FileStore(long orgId, long userId) {
		super(orgId, userId);
	}
	private String bucketName;
	private String rootPath;
	private static final String SECRET_ROOT_PATH="secrets";

	@Override
	protected String getRootPath() {
		this.rootPath = getOrgId() + File.separator + "files";
		return rootPath;
	}

	private String getBucketName() {
		if (this.bucketName == null) {
			this.bucketName = FacilioProperties.getConfig("s3.bucket.name");
		}
		return this.bucketName;
	}
	
	@Override
	public long addFile(String fileName, File file, String contentType) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandtory");
		}
		long fileId = addDummyFileEntry(fileName);
		String filePath = getRootPath() + File.separator + fileId+"-"+fileName;
		long fileSize = file.length();
		
	    try {
	    	PutObjectResult rs = AwsUtil.getAmazonS3Client().putObject(getBucketName(), filePath, file);
	    	if (rs != null) {
	    		updateFileEntry(fileId, fileName, filePath, fileSize, contentType);
	    		
	    		addComppressedFile(fileId, fileName, file, contentType);
	    		
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
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream();FileInputStream fis = new FileInputStream(file);) {
				if (contentType.contains("image/")) {
					// Image resizing...
					BufferedImage imBuff = ImageIO.read(fis);
					BufferedImage out = ImageScaleUtil.resizeImage(imBuff, resizeVal, resizeVal);
					ImageIO.write(out, "png", baos);
					baos.flush();
					byte[] imageInByte = baos.toByteArray();
					try (ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);) {
						String resizedFilePath = getRootPath() + File.separator + fileId + "-resized-" + resizeVal + "x" + resizeVal;
						PutObjectResult rs = writeStreamToS3(imageInByte, resizedFilePath);
						if (rs != null) {
							addResizedFileEntry(fileId, resizeVal, resizeVal, resizedFilePath, imageInByte.length, "image/png");
						}
					}
				}
		    } catch (Exception e) {
		    	log.error("Exception occurred ", e);
		    }
		}
		return fileId;
	}
	
	@Override
	public long addFile(String fileName, String content, String contentType) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandtory");
		}
		long fileId = addDummyFileEntry(fileName);
		String filePath;
		if(getOrgId() == -1 || AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == -1) {
			filePath = getRootPath() + File.separator + fileName + "-" + fileId;
		} else {
			filePath = getRootPath() + File.separator + fileId + "-" + fileName;
		}
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
	public String getDownloadUrl(long fileId) throws Exception {
		return super.getDownloadUrl(fileId);
	}
	
	@Override
	public InputStream readFile(long fileId) throws Exception {
		return readFile(fileId, false);
	}
	
	@Override
	public InputStream readFile(long fileId, boolean fetchOriginal) throws Exception {
		FileInfo fileInfo = getFileInfo(fileId, fetchOriginal);
		return readFile(fileInfo);
	}
	
	@Override
	public InputStream readFile(FileInfo fileInfo) throws Exception {
				if (fileInfo == null) {
			return null;
		}
		log.debug("filePath: " + getBucketName() + "" + fileInfo.getFilePath());
		log.debug("fileUrl: " + AwsUtil.getAmazonS3Client().getUrl(getBucketName(), fileInfo.getFilePath()));
		S3Object so = AwsUtil.getAmazonS3Client().getObject(getBucketName(), fileInfo.getFilePath());
		return so.getObjectContent();
	}

	@Override
	public boolean deleteFile(long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo == null) {
			return false;
		}
		
		return  deleteFiles(Collections.singletonList(fileId));
	}

	@Override
	public boolean deleteFiles(List<Long> fileId) throws Exception {
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
	public String getOrgiFileUrl(long fileId) throws Exception {
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo != null) {
			return fetchUrl(fileInfo, getExpiration(), false);
		}
		return null;
	}
	
	@Override
	public String getOrgiDownloadUrl(long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo != null) {
			return fetchDownloadUrl(fileInfo, getExpiration());
		}
		return null;
	}

	@Override
	public boolean isFileExists(String newVersion) {
		if (FacilioProperties.isDevelopment()) {
			return true;
		}
		boolean objectExists = false;
		String staticBucket = FacilioProperties.getConfig("static.bucket");
		if( ! (FacilioProperties.isDevelopment() && FacilioProperties.isProduction())) {
			newVersion = "facilio-client/"+newVersion;
		}
		if(staticBucket != null) {
			AmazonS3 s3Client = getClient();
			objectExists = s3Client.doesObjectExist(staticBucket, newVersion+"/js/app.js");
		}
		return objectExists;
	}

	private String fetchUrl(FileInfo fileInfo, long expiration, boolean isDownloadable) {

		if (FacilioProperties.isDevelopment()) {

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
				else {
					s3ObjectKey = s3ObjectKey +"&response-content-disposition="+URLEncoder.encode("inline; filename="+fileInfo.getFileName(),"UTF-8");
				}
				String keyPairId = FacilioProperties.getConfig("key.pair.id");
				return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(SignerUtils.Protocol.https, FacilioProperties.getConfig("files.url"), new File(PRIVATE_KEY_FILE_PATH), s3ObjectKey, keyPairId, new Date(System.currentTimeMillis()+getExpiration()));
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

	private static boolean checkIfVersionExistsInS3(String newVersion) {
		if (FacilioProperties.isDevelopment()) {
			return true;
		}
		boolean objectExists = false;
		String staticBucket = FacilioProperties.getConfig("static.bucket");
		if(staticBucket != null) {
			AmazonS3 s3Client = getClient();
			objectExists = s3Client.doesObjectExist(staticBucket, newVersion+"/js/app.js");
		}
		return objectExists;

	}
	public static AmazonS3 getClient() {
		if (AWS_S3_CLIENT == null) {
			AWS_S3_CLIENT = AmazonS3ClientBuilder.standard().withRegion(com.facilio.services.impls.aws.AwsUtil.getRegion()).withCredentials(com.facilio.services.impls.aws.AwsUtil.getAWSCredentialsProvider()).build();
		}
		return AWS_S3_CLIENT;
	}

	@Override
	public long addSecretFile(String fileName,File file, String contentType) throws Exception {
		log.info("add secret file called : "+fileName + ":" + file.getPath() + " : "+ contentType);
		long fileId = addDummySecretFileEntry(fileName);
		String filePath = SECRET_ROOT_PATH + File.separator + fileName;
		long fileSize = file.length();
		try{
			PutObjectResult rs = AwsUtil.getAmazonS3Client().putObject(getBucketName(),filePath,file);
			if (rs != null ){
				updateSecretFileEntry(fileId,fileName,filePath,fileSize,contentType);
				return fileId;
			}
			else{
				deleteSecretFileEntry(fileId);
				return -1;
			}

		}catch (Exception ex){
			deleteSecretFileEntry(fileId);
			throw ex;

		}

	}


	@Override
	public boolean removeSecretFile(String fileName) throws Exception {
		FileInfo fileInfo = SecretFileUtils.getSecretFileInfo(fileName);
		AwsUtil.getAmazonS3Client().deleteObject(bucketName,SECRET_ROOT_PATH+File.separator+fileInfo.getFileName());
		return false;
	}

	@Override
	public boolean isSecretFileExists(String fileName) {
		return false;
	}

	@Override
	public InputStream getSecretFile(String filename) {
		log.info("get secret file called");
		S3Object so = AwsUtil.getAmazonS3Client().getObject(getBucketName(),SECRET_ROOT_PATH +File.separator + filename);
		return so.getObjectContent();
	}

	@Override
	public void addComppressedFile(long fileId,  String fileName, File file, String contentType) throws Exception {
		if (contentType.contains("image/")) {
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
				String resizedFilePath = getRootPath() + File.separator + fileId + "-compressed";
				byte[] imageInByte = writeCompressedFile(fileId, file, contentType, baos, resizedFilePath);
				if (imageInByte != null) {
					writeStreamToS3(imageInByte, resizedFilePath);
				}
			} 
		}
	}
	
	private PutObjectResult writeStreamToS3(byte[] imageInByte, String filePath) throws IOException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);) {
			ObjectMetadata metaData = new ObjectMetadata();
			metaData.setContentLength(imageInByte.length);
			return AwsUtil.getAmazonS3Client().putObject(getBucketName(), filePath, bis, metaData);
		}
	}
	
	@Override
	public boolean deleteFilePermenantly(long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(fileId);
		AwsUtil.getAmazonS3Client().deleteObject(getBucketName(), fileInfo.getFilePath());
		
		return deleteFileEntry(fileId);
	}
	
	@Override
	public boolean deleteFilesPermanently(List<Long> fileIds) throws Exception {

		List<String> filePathList = getFilePathList(fileIds);
		List<List<String>> partitionList = ListUtils.partition(filePathList, 1000);

		partitionList.forEach((List<String> chunckObjects) -> {
			DeleteObjectsRequest dor = new DeleteObjectsRequest(getBucketName())
					.withKeys(chunckObjects.toArray(new String[chunckObjects.size()])).withQuiet(false);

			DeleteObjectsResult delObjRes = AwsUtil.getAmazonS3Client().deleteObjects(dor);
			List<DeletedObject> resultObj = delObjRes.getDeletedObjects();
			if(CollectionUtils.isNotEmpty(resultObj)) {
//				int successfulDeletes = delObjRes.getDeletedObjects().size();
//				log.info(successfulDeletes + " objects successfully marked for deletion without versions." + " isDeleteMarker : "+delObjRes.getDeletedObjects().get(0).isDeleteMarker());
				List<KeyVersion> keyList = new ArrayList<KeyVersion>();
				resultObj.forEach((DeletedObject deletedObject) -> {
					keyList.add(new KeyVersion(deletedObject.getKey(), deletedObject.getDeleteMarkerVersionId()));
				});
				multiObjectVersionedDeleteAndRemoveDeleteMarkers(keyList);
			}
		});

		return deleteFileEntries(fileIds);
	}
	
	private void multiObjectVersionedDeleteAndRemoveDeleteMarkers(List<KeyVersion> keyList) {
		DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(getBucketName()).withKeys(keyList)
				.withQuiet(false);
		DeleteObjectsResult delObjRes = AwsUtil.getAmazonS3Client().deleteObjects(deleteRequest);
		int successfulDeletes = delObjRes.getDeletedObjects().size();
		log.info(successfulDeletes + " delete markers successfully deleted");
	}
}
