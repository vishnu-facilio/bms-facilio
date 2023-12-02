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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.amazonaws.AbortedException;
import com.amazonaws.services.s3.model.*;
import com.facilio.util.FacilioUtil;
import com.google.common.io.ByteSource;
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
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.ImageScaleUtil;
import com.facilio.fs.FileInfo;

public class S3FileStore extends FileStore {
	private static final Logger LOGGER = LogManager.getLogger(S3FileStore.class.getName());
	private static AmazonS3 AWS_S3_CLIENT = null;
	private static final String PRIVATE_KEY_FILE_PATH = System.getProperty("user.home")+ FacilioUtil.normalizePath("/pk/pk-APKAJUH5UCWNSYC4DOSQ.pem");
	private static final long EXPIRATION = 48 * 60* 60 * 1000;


	S3FileStore(long orgId, long userId) {
		super(orgId, userId);
	}
	private String bucketName;
	private static final String SECRET_ROOT_PATH="secrets";
	private static final String FILES_DIR = "files";

	@Override
	protected String getRootPath(String namespace) {
		NamespaceConfig namespaceConfig = FileStore.getNamespace(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace in getting root path");
		StringBuilder rootPath = new StringBuilder();
		rootPath.append(getOrgId())
				.append(File.separator)
				.append(FILES_DIR)
				.append(File.separator)
				.append(namespace);
		if(namespaceConfig.isDailyDirectoryNeeded()) {
			rootPath.append(File.separator).append(DATE_FORMAT.format(new Date()));
		}
		return rootPath.toString();
	}

	private String getBucketName() {
		if (this.bucketName == null) {
			this.bucketName = FacilioProperties.getConfig("s3.bucket.name");
		}
		return this.bucketName;
	}
	
	@Override
	public long addFile(String namespace, String fileName, File file, String contentType) throws Exception {
		return this.addFile(namespace, fileName, file, contentType, false);
	}
	
	public static String getURL( String bucket, String filePath, File file) throws Exception {
		AmazonS3Client s3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).withCredentials(AwsUtil.getAWSCredentialsProvider()).build();
    	s3Client.putObject(bucket, filePath, file);
		return s3Client.getResourceUrl(bucket, filePath);
	}

	private long addFile(String namespace, String fileName, File file, String contentType, boolean isOrphan) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandtory");
		}
		long fileId = addDummyFileEntry(namespace, fileName, false);
		String filePath = getRootPath(namespace) + File.separator + fileId+"-"+fileName;
		long fileSize = file.length();
		LOGGER.debug("addFile: filePath: " + filePath);
		LOGGER.debug("addFile: fileName: " + fileName);

		try {
			PutObjectResult rs = getPutObjectResult(file, filePath,5);
			if (rs != null) {
				updateFileEntry(namespace, fileId, fileName, filePath, fileSize, contentType);
				scheduleCompressJob(namespace, fileId, contentType);
				return fileId;
			}
			else {
				deleteFileEntry(namespace, fileId);
				return -1;
			}
		} catch (Exception e) {
			deleteFileEntry(namespace, fileId);
			throw e;
		}
	}

	private PutObjectResult getPutObjectResult(File file, String filePath, int count) {
		try {
			PutObjectResult rs = AwsUtil.getAmazonS3Client().putObject(getBucketName(), filePath, file);
			return rs;
		}
		catch (AbortedException e) {
			if(count !=0) {
				count--;
				int retryCount = 5 - count;
				LOGGER.info("Retrying count : " + retryCount + filePath);
				return getPutObjectResult(file, filePath, count);
			}else{
				throw e;
			}
		}
	}

	@Override
	public long addOrphanedFile(String namespace, String fileName, File file, String contentType) throws Exception {
		return addFile(namespace, fileName, file, contentType, true);
	}

	private long addFile(String namespace, String fileName, File file, String contentType, int[] resize, boolean isOrphaned) throws Exception {
		long fileId = this.addFile(namespace, fileName, file, contentType, isOrphaned);

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
						String resizedFilePath = getRootPath(namespace) + File.separator + fileId + "-resized-" + resizeVal + "x" + resizeVal;
						PutObjectResult rs = writeStreamToS3(imageInByte, resizedFilePath);
						if (rs != null) {
							addResizedFileEntry(namespace, fileId, resizeVal, resizeVal, resizedFilePath, imageInByte.length, "image/png");
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Exception occurred ", e);
			}
		}
		return fileId;
	}

	@Override
	public long addFile(String namespace, String fileName, File file, String contentType, int[] resize) throws Exception {
		return addFile(namespace, fileName, file, contentType, resize, false);
	}
	
	@Override
	public long addFile(String namespace, String fileName, String content, String contentType) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandtory");
		}
		long fileId = addDummyFileEntry(namespace, fileName, false);
		String filePath = getRootPath(namespace) + File.separator + fileId + "-" + fileName;
		long fileSize = content.length();
		
	    try {
	    	PutObjectResult rs = AwsUtil.getAmazonS3Client().putObject(getBucketName(), filePath, content);
	    	if (rs != null) {
	    		updateFileEntry(namespace, fileId, fileName, filePath, fileSize, contentType);
	    		return fileId;
	    	}
	    	else {
	    		deleteFileEntry(namespace, fileId);
	    		return -1;
	    	}
	    } catch (Exception e) {
	    	deleteFileEntry(namespace, fileId);
	    	throw e;
	    }
	}
	@Override
	public long addFile(String namespace, String fileName, byte[] content, String contentType, boolean isOrphan) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandatory");
		}

		long fileId = addDummyFileEntry(namespace, fileName, isOrphan);
		String filePath = getRootPath(namespace) + File.separator + fileId + "-" + fileName;
		long fileSize = content.length;

		try {

			InputStream inputStream = new ByteArrayInputStream(content);

			ObjectMetadata metaData = new ObjectMetadata();
			metaData.setContentLength(content.length);
			PutObjectResult rs = AwsUtil.getAmazonS3Client().putObject(getBucketName(), filePath,inputStream,metaData);
			if (rs != null) {
				updateFileEntry(namespace, fileId, fileName, filePath, fileSize, contentType);
				return fileId;
			}
			else {
				deleteFileEntry(namespace, fileId);
				return -1;
			}
		} catch (IOException ioe) {
			deleteFileEntry(namespace, fileId);
			throw ioe;
		}
	}

	@Override
	public String getDownloadUrl(long fileId) throws Exception {
		return super.getDownloadUrl(fileId);
	}
	
	@Override
	public InputStream readFile(String namespace, long fileId) throws Exception {
		return readFile(namespace, fileId, false);
	}
	
	@Override
	public InputStream readFile(String namespace, long fileId, boolean fetchOriginal) throws Exception {
		FileInfo fileInfo = getFileInfo(namespace, fileId, fetchOriginal);
		return readFile(fileInfo);
	}
	
	@Override
	public InputStream readFile(FileInfo fileInfo) throws Exception {
		if (fileInfo == null) {
			return null;
		}
		LOGGER.debug("filePath: " + getBucketName() + "" + fileInfo.getFilePath());
		LOGGER.debug("fileUrl: " + AwsUtil.getAmazonS3Client().getUrl(getBucketName(), fileInfo.getFilePath()));

		return getS3ObjectInputStream(fileInfo,5);
	}

	private S3ObjectInputStream getS3ObjectInputStream(FileInfo fileInfo,int count) {
		try {
			try {
				S3Object so = AwsUtil.getAmazonS3Client().getObject(getBucketName(), fileInfo.getFilePath());
				return so.getObjectContent();
			}
			 catch(AbortedException e ){
				if(count !=0) {
					count--;
					int retryCount = 5 - count;
					LOGGER.info("Retrying count : " + retryCount + fileInfo.getFilePath());
					return getS3ObjectInputStream(fileInfo,count);
				}else{
					throw e;
				}
			 }
		}
		catch (Exception e) {
			LOGGER.error("Error occurred while getting file : "+String.valueOf(fileInfo.getFilePath()), e);
			throw e;
		}
	}

	@Override
	public boolean deleteFile(String namespace, long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(namespace, fileId);
		if (fileInfo == null) {
			return false;
		}
		
		return  deleteFiles(namespace, Collections.singletonList(fileId));
	}

	@Override
	public boolean deleteFiles(String namespace, List<Long> fileId) throws Exception {
		return markAsDeleted(namespace, fileId) > 0;
	}

	@Override
	public boolean renameFile(String namespace, long fileId, String newName) throws Exception {
		FileInfo fileInfo = getFileInfo(namespace, fileId);
		if (fileInfo == null) {
			return false;
		}

		String oldFilePath = fileInfo.getFilePath();
		String newFilePath = getRootPath(namespace) + File.separator + fileId + "-" + newName;
		
		AwsUtil.getAmazonS3Client().copyObject(getBucketName(), oldFilePath, getBucketName(), newFilePath);
		// update db entry
		updateFileEntry(namespace, fileId, newName, newFilePath, fileInfo.getFileSize(), fileInfo.getContentType());
		
		// deleting old file from s3
		AwsUtil.getAmazonS3Client().deleteObject(getBucketName(), oldFilePath);
		return true;
	}

	@Override
	public String getOrgiFileUrl(String namespace, long fileId) throws Exception {
		FileInfo fileInfo = getFileInfo(namespace, fileId);
		if (fileInfo != null) {
			return fetchUrl(fileInfo, getExpiration(), false);
		}
		return null;
	}
	
	@Override
	public String getOrgiDownloadUrl(String namespace, long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(namespace, fileId, true);
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
			objectExists = s3Client.doesObjectExist(staticBucket, newVersion+FacilioUtil.normalizePath("/js/app.js"));
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
				
				String encodedFilename = URLEncoder.encode(fileInfo.getFileName(), "UTF-8").replace("+", " ");
				if(isDownloadable) {
					s3ObjectKey = s3ObjectKey +"&response-content-disposition="+URLEncoder.encode("attachment; filename="+encodedFilename,"UTF-8");
				}
				else {
					s3ObjectKey = s3ObjectKey +"&response-content-disposition="+URLEncoder.encode("inline; filename="+encodedFilename,"UTF-8");
				}
				String keyPairId = FacilioProperties.getConfig("key.pair.id");
				return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(SignerUtils.Protocol.https, FacilioProperties.getConfig("files.url"), new File(PRIVATE_KEY_FILE_PATH), s3ObjectKey, keyPairId, new Date(System.currentTimeMillis()+getExpiration()));
			} catch (IOException | InvalidKeySpecException e) {
				LOGGER.info("Exception while creating signed Url", e);
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
			objectExists = s3Client.doesObjectExist(staticBucket, newVersion+FacilioUtil.normalizePath("/js/app.js"));
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
		LOGGER.info("add secret file called : "+fileName + ":" + file.getPath() + " : "+ contentType);
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
		LOGGER.info("get secret file called");
		S3Object so = AwsUtil.getAmazonS3Client().getObject(getBucketName(),SECRET_ROOT_PATH +File.separator + filename);
		return so.getObjectContent();
	}
	
	@Override
	public void addCompressedFile(String namespace, long fileId, FileInfo fileInfo) throws Exception {
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
			String resizedFilePath = getRootPath(namespace) + File.separator + fileId + "-compressed";
			byte[] imageInByte = writeCompressedFile(namespace, fileId, fileInfo , baos, resizedFilePath);
			if (imageInByte != null) {
				writeStreamToS3(imageInByte, resizedFilePath);
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
	public boolean deleteFilePermanently(String namespace, long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(namespace, fileId);
		AwsUtil.getAmazonS3Client().deleteObject(getBucketName(), fileInfo.getFilePath());
		
		return deleteFileEntry(namespace, fileId);
	}
	
	@Override
	public boolean deleteFilesPermanently(String namespace, List<Long> fileIds) throws Exception {

		List<String> filePathList = getFilePathList(namespace, fileIds);
		List<List<String>> partitionList = ListUtils.partition(filePathList, 1000);
		try {
			partitionList.forEach((List<String> chunckObjects) -> {
				DeleteObjectsRequest dor = new DeleteObjectsRequest(getBucketName())
						.withKeys(chunckObjects.toArray(new String[chunckObjects.size()])).withQuiet(false);

				DeleteObjectsResult delObjRes = AwsUtil.getAmazonS3Client().deleteObjects(dor);
//				 log.info("s3 object deleted size : "+delObjRes.getDeletedObjects().size());
			});
		}catch(MultiObjectDeleteException e) {
			List<DeleteError> error = e.getErrors();
			error.forEach(del -> LOGGER.info("Object Key is : "+del.getKey() + " ErrorMessage is  : "+ del.getMessage()+"  error code : "+del.getCode()));
		}
		LOGGER.info("size of delete rows :"+fileIds.size());
		return deleteFileEntries(namespace, fileIds);
	}

}
