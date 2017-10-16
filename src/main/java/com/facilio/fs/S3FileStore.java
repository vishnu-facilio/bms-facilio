package com.facilio.fs;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.facilio.aws.util.AwsUtil;

public class S3FileStore extends FileStore {
	
	public S3FileStore(long orgId, long userId) {
		super(orgId, userId);
	}
	
	private String bucketName;
	private String rootPath;

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
		
		AwsUtil.getAmazonS3Client().deleteObject(getBucketName(), fileInfo.getFilePath());
		return deleteFileEntry(fileId);
	}

	@Override
	public boolean deleteFiles(List<Long> fileId) throws Exception {
		
		List<String> filePathList = getFilePathList(fileId);
		
		try {
			DeleteObjectsRequest dor = new DeleteObjectsRequest(getBucketName())
					.withKeys(filePathList.toArray(new String[filePathList.size()]));
			AwsUtil.getAmazonS3Client().deleteObjects(dor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deleteFileEntries(fileId);
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
	
	@Override
	public String getPrivateUrl(long fileId) throws Exception {
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo == null) {
			return null;
		}
		
		java.util.Date expiration = new java.util.Date();
		long msec = expiration.getTime();
		msec += 24 * 60 * 60 * 1000; // 24 hour.
		expiration.setTime(msec);
		             
		GeneratePresignedUrlRequest generatePresignedUrlRequest = 
		              new GeneratePresignedUrlRequest(getBucketName(), fileInfo.getFilePath());
		generatePresignedUrlRequest.setMethod(HttpMethod.GET);
		generatePresignedUrlRequest.setExpiration(expiration);
		             
		URL url = AwsUtil.getAmazonS3Client().generatePresignedUrl(generatePresignedUrlRequest);
		return url.toString();
	}
}
