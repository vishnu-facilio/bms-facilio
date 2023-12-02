package com.facilio.services.filestore;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.ImageScaleUtil;
import com.facilio.fs.FileInfo;
import com.facilio.oci.util.OCIUtil;
import com.facilio.oci.util.OracleStorageClient;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import com.oracle.bmc.objectstorage.responses.RenameObjectResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OracleFileStore extends FileStore {
	private static final Logger LOGGER = LogManager.getLogger(OracleFileStore.class.getName());

	private static final long EXPIRATION = 48 * 60* 60 * 1000;

	OracleFileStore(long orgId, long userId) {
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
			this.bucketName = FacilioProperties.getConfig("oci.bucket.name");
		}
		return this.bucketName;
	}
	
	@Override
	public long addFile(String namespace, String fileName, File file, String contentType) throws Exception {
		return this.addFile(namespace, fileName, file, contentType, false);
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
			PutObjectResponse rs = OCIUtil.getOracleStorageClient().putObject(getBucketName(), filePath, file);
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

	@Override
	public long addOrphanedFile(String namespace, String fileName, File file, String contentType) throws Exception {
		return addFile(namespace, fileName, file, contentType, true);
	}

	private long addFile(String namespace, String fileName, File file, String contentType, int[] resize, boolean isOrphaned) throws Exception {
		long fileId = this.addFile(namespace, fileName, file, contentType, isOrphaned);

		for (int resizeVal : resize) {
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream();FileInputStream fis = new FileInputStream(file)) {
				if (contentType.contains("image/")) {
					// Image resizing...
					BufferedImage imBuff = ImageIO.read(fis);
					BufferedImage out = ImageScaleUtil.resizeImage(imBuff, resizeVal, resizeVal);
					ImageIO.write(out, "png", baos);
					baos.flush();
					byte[] imageInByte = baos.toByteArray();
					String resizedFilePath = getRootPath(namespace) + File.separator + fileId + "-resized-" + resizeVal + "x" + resizeVal;
					PutObjectResponse rs = OCIUtil.getOracleStorageClient().putObject(getBucketName(), resizedFilePath, imageInByte);
					if (rs != null) {
						addResizedFileEntry(namespace, fileId, resizeVal, resizeVal, resizedFilePath, imageInByte.length, "image/png");
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
	    	PutObjectResponse rs = OCIUtil.getOracleStorageClient().putObject(getBucketName(), filePath, content);
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
			PutObjectResponse rs = OCIUtil.getOracleStorageClient().putObject(getBucketName(), filePath, content);
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

		try {
			GetObjectResponse response = OCIUtil.getOracleStorageClient().getObject(getBucketName(), fileInfo.getFilePath());
			return response.getInputStream();
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

		RenameObjectResponse ror = OCIUtil.getOracleStorageClient().renameObject(getBucketName(), oldFilePath, newFilePath);
		if(ror == null) {
			LOGGER.error("Object copy failed.. old : "+oldFilePath+ " :: new : "+newFilePath);
		}

		// update db entry
		updateFileEntry(namespace, fileId, newName, newFilePath, fileInfo.getFileSize(), fileInfo.getContentType());

		// deleting old file from s3
		OCIUtil.getOracleStorageClient().deleteObject(getBucketName(), oldFilePath);
		return true;
	}

	@Override
	public String getOrgiFileUrl(String namespace, long fileId) throws Exception {
		FileInfo fileInfo = getFileInfo(namespace, fileId);
		if (fileInfo != null) {
			return fetchUrl(fileInfo, getExpiration());
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
	public boolean isFileExists(String newVersion) { //TODO: remove this method from interface itself
		return false;
	}

	private String fetchUrl(FileInfo fileInfo, long expiration) {
		return OCIUtil.getOracleStorageClient().generatePresignedUrl(getBucketName(), fileInfo.getFilePath(), expiration);
	}
	
	private String fetchDownloadUrl(FileInfo fileInfo, long expiration) {
		return fetchUrl(fileInfo, expiration);
	}
	
	
	private long getExpiration() {
		return EXPIRATION;
	}

	@Override
	public long addSecretFile(String fileName,File file, String contentType) throws Exception {
		LOGGER.info("add secret file called : "+fileName + ":" + file.getPath() + " : "+ contentType);
		long fileId = addDummySecretFileEntry(fileName);
		String filePath = SECRET_ROOT_PATH + File.separator + fileName;
		long fileSize = file.length();
		try{
			PutObjectResponse rs = OCIUtil.getOracleStorageClient().putObject(getBucketName(), filePath, file);
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
		OCIUtil.getOracleStorageClient().deleteObject(bucketName, SECRET_ROOT_PATH+File.separator+fileInfo.getFileName());
		return false;
	}

	@Override
	public boolean isSecretFileExists(String fileName) { // todo: check whether to remove or not
		return false;
	}

	@Override
	public InputStream getSecretFile(String filename) {
		LOGGER.info("get secret file called");
		GetObjectResponse response = OCIUtil.getOracleStorageClient().getObject(getBucketName(), SECRET_ROOT_PATH +File.separator + filename);
		return response.getInputStream();
	}


	@Override
	public void addCompressedFile(String namespace, long fileId, FileInfo fileInfo) throws Exception {
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
			String resizedFilePath = getRootPath(namespace) + File.separator + fileId + "-compressed";
			byte[] imageInByte = writeCompressedFile(namespace, fileId, fileInfo , baos, resizedFilePath);
			if (imageInByte != null) {
				OCIUtil.getOracleStorageClient().putObject(getBucketName(), resizedFilePath, imageInByte);
			}
		}
	}
	
	@Override
	public boolean deleteFilePermanently(String namespace, long fileId) throws Exception {
		FileInfo fileInfo = getFileInfo(namespace, fileId);
		OCIUtil.getOracleStorageClient().deleteObject(getBucketName(), fileInfo.getFilePath());
		return deleteFileEntry(namespace, fileId);
	}
	
	@Override
	public boolean deleteFilesPermanently(String namespace, List<Long> fileIds) throws Exception {
		for(Long fileId : fileIds) {
			deleteFilePermanently(namespace, fileId);
		}
		LOGGER.info("size of delete rows :"+fileIds.size());
		return deleteFileEntries(namespace, fileIds);
	}

}
