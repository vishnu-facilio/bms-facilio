package com.facilio.services.filestore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class LocalFileStore extends FileStore {

	LocalFileStore(long orgId, long userId) {
		super(orgId, userId);
	}
	
	private static final Map<String, String> ROOT_PATHS = new ConcurrentHashMap<>();
	@Override
	protected String getRootPath(String namespace) {
		NamespaceConfig namespaceConfig = FileStore.getNamespace(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace in getting root path");
		String key = namespaceConfig.getName(), dateVal = null, rootPath = null;
		if (namespaceConfig.isDailyDirectoryNeeded()) {
			dateVal = DATE_FORMAT.format(new Date());
			key += "-" + dateVal;
		}
		rootPath = ROOT_PATHS.get(key);
		if (rootPath == null) {
			String localFileStorePath = FacilioProperties.getLocalFileStorePath();
			if (StringUtils.isEmpty(localFileStorePath)) {
				ClassLoader classLoader = LocalFileStore.class.getClassLoader();
				URL fcDataFolder = classLoader.getResource("");
				localFileStorePath = fcDataFolder.getFile();
			}
			StringBuilder path = new StringBuilder()
									.append(localFileStorePath)
									.append(File.separator)
									.append("facilio-data")
									.append(File.separator)
									.append(getOrgId())
									.append(File.separator);
			path.append("files")
				.append(File.separator)
				.append(namespace)
				.append(File.separator);
			if (namespaceConfig.isDailyDirectoryNeeded()) {
				path.append(dateVal);
			}
			rootPath = path.toString();
			File rootDir = new File(rootPath);
			if (!(rootDir.exists() && rootDir.isDirectory())) {
				rootDir.mkdirs();
			}
			ROOT_PATHS.put(key, rootPath);
		}
		return rootPath;
	}
	
	@Override
	public String getOrgiDownloadUrl(String namespace, long fileId) throws Exception {
		return null;
	}

	@Override
	public boolean isFileExists(String newVersion) {
		File f = new File(getRootPath(DEFAULT_NAMESPACE)+File.separator+"js"+File.separator+"app.js");
		return f.exists();
	}

	private long addFile(String namespace, String fileName, File file, String contentType, boolean isOrphan) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandtory");
		}
		long fileId = addDummyFileEntry(namespace, fileName, isOrphan);
		String filePath = getRootPath(namespace) + File.separator + fileId+"-"+fileName;
		long fileSize = file.length();

		InputStream is = null;
		OutputStream os = null;
		try {
			File createFile = new File(filePath);
			createFile.createNewFile();

			is = new FileInputStream(file);
			os = new FileOutputStream(createFile);
			byte[] buffer = new byte[4096];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();

			updateFileEntry(namespace, fileId, fileName, filePath, fileSize, contentType);

			addComppressedFile(namespace, fileId, fileName, file, contentType);

		} catch (IOException ioe) {
			deleteFileEntry(namespace, fileId);
			throw ioe;
		} finally {
			is.close();
			os.close();
		}
		return fileId;
	}


	@Override
	public long addFile(String namespace, String fileName, File file, String contentType) throws Exception {
		return this.addFile(namespace, fileName, file, contentType, false);
	}
	
	@Override
	public long addFile(String namespace, String fileName, File file, String contentType, int[] resize) throws Exception {
		return addFile(namespace, fileName, file, contentType);
	}

	@Override
	public long addOrphanedFile(String namespace, String fileName, File file, String contentType, int[] resize) throws Exception {
		return addFile(namespace, fileName, file, contentType, true);
	}

	@Override
	public long addFile(String namespace, String fileName, String content, String contentType) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandtory");
		}
		long fileId = addDummyFileEntry(namespace, fileName, false);
		String filePath = getRootPath(namespace) + File.separator + fileId+"-"+fileName;
		long fileSize = content.length();
		
		InputStream is = null;
	    OutputStream os = null;
	    try {
	    	File createFile = new File(filePath);
			createFile.createNewFile();
			
	        is = IOUtils.toInputStream(content);
	        os = new FileOutputStream(createFile);
	        byte[] buffer = new byte[4096];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	        os.flush();
	        
	        updateFileEntry(namespace, fileId, fileName, filePath, fileSize, contentType);
	        
	    } catch (IOException ioe) {
	    	deleteFileEntry(namespace, fileId);
	    	throw ioe;
	    } finally {
	        is.close();
	        os.close();
	    }
		return fileId;
	}
	@Override
	public String getOrgiFileUrl(String namespace, long fileId) {
		return null;
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
		return new FileInputStream(new File(fileInfo.getFilePath()));
	}

	@Override
	public boolean deleteFile(String namespace, long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(namespace, fileId);
		if (fileInfo == null) {
			return false;
		}
		
		return deleteFiles(namespace, Collections.singletonList(fileId));
		
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

		String newFilePath = getRootPath(namespace) + File.separator + fileId + "-" + newName;
		
		File currentFile = new File(fileInfo.getFilePath());
		File newFile = new File(newFilePath);
		
		boolean status = currentFile.renameTo(newFile);
		if (status) {
			// update db entry
			updateFileEntry(namespace, fileId, newName, newFilePath, fileInfo.getFileSize(), fileInfo.getContentType());
			return true;
		}
		else {
			return false;
		}
	}
	@Override
	public long addSecretFile(String fileName, File file, String contentType) throws Exception {
		long fileId = addDummySecretFileEntry(fileName);
		String filePath = "secrets" + File.separator +fileName;
		long fileSize = file.length();
		InputStream is = null;
		OutputStream os = null;
		try {
			File createFile = new File(filePath);
			File secretsDir = new File("secrets");
			if (secretsDir.exists() && secretsDir.isDirectory()) {
				createFile.createNewFile();

			}else{
				Files.createDirectory(Paths.get("secrets"));
				createFile.createNewFile();
			}
			is = new FileInputStream(file);
			os = new FileOutputStream(createFile);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();

			updateSecretFileEntry(fileId, fileName, filePath, fileSize, contentType);
		} catch (IOException ioe) {
			deleteSecretFileEntry(fileId);
			throw ioe;
		} finally {
			is.close();
			os.close();
		}
		return fileId;
	}

	@Override
	public InputStream getSecretFile(String fileName) throws Exception {
		FileInfo fileInfo = SecretFileUtils.getSecretFileInfo(fileName);
		return readFile(fileInfo);
	}



	@Override
	public boolean removeSecretFile(String tag) throws Exception {
		FileInfo fileInfo =SecretFileUtils.getSecretFileInfo(tag);
		if (fileInfo==null) return false;
		Files.delete(Paths.get(fileInfo.getFilePath()));
		new GenericDeleteRecordBuilder().table(ModuleFactory.getSecretFileModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getSecretFileIdField(), Collections.singleton(fileInfo.getFileId()), NumberOperators.EQUALS))
				.delete();
		return SecretFileUtils.getSecretFileInfo(tag)==null;
	}

	@Override
	public boolean isSecretFileExists(String fileName) {
		return false;
	}

	@Override
	public void addComppressedFile(String namespace, long fileId, String fileName, File file,String contentType) throws Exception {
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
			String resizedFilePath = getRootPath(namespace) + File.separator + fileId + "-compressed" +".jpg";
			byte[] imageInBytes = writeCompressedFile(namespace, fileId, file, contentType, baos, resizedFilePath);
			if (imageInBytes != null) {
				File createFile = new File(resizedFilePath);
				createFile.createNewFile();
				try(OutputStream outputStream = new FileOutputStream(createFile)) {
					baos.writeTo(outputStream);
				}
			}
		}
	}
	
	@Override
	public boolean deleteFilePermanently(String namespace, long fileId) throws Exception {
		return deleteFilesPermanently(namespace, Collections.singletonList(fileId));
	}
	
	@Override
	public boolean deleteFilesPermanently(String namespace, List<Long> fileIds) throws Exception {
		List<String> filePathList = getFilePathList(namespace, fileIds);
		boolean status = true;
		for(String path: filePathList) {
			status = new File(path).delete();
			if (status == false) {
				break;
			}
		}
		if (status) {
			deleteFileEntries(namespace, fileIds);
		}
		return status;
	}
}
