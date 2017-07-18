package com.facilio.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

public class LocalFileStore extends FileStore {

	public LocalFileStore(long orgId, long userId) {
		super(orgId, userId);
	}
	
	private String rootPath;
	
	@Override
	protected String getRootPath() {
		if (rootPath == null) {
			ClassLoader classLoader = LocalFileStore.class.getClassLoader();
			URL fcDataFolder = classLoader.getResource("");
			rootPath = fcDataFolder.getFile() + File.separator + "facilio-data" + File.separator + getOrgId() + File.separator + "files";
			
			File rootDir = new File(rootPath);
			if (!rootDir.exists() || !!rootDir.isDirectory()) {
				rootDir.mkdirs();
			}
		}
		return rootPath;
	}

	@Override
	public long addFile(String fileName, File file, String contentType) throws Exception {
		long fileId = addDummyFileEntry(fileName);
		String filePath = getRootPath() + File.separator + fileId+"-"+fileName;
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
	        
	        updateFileEntry(fileId, fileName, filePath, fileSize, contentType);
	    } catch (IOException ioe) {
	    	deleteFileEntry(fileId);
	    	throw ioe;
	    } finally {
	        is.close();
	        os.close();
	    }
		return fileId;
	}

	@Override
	public InputStream readFile(long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo == null) {
			return null;
		}
		
		return new FileInputStream(new File(fileInfo.getFilePath()));
	}

	@Override
	public boolean deleteFile(long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo == null) {
			return false;
		}
		
		boolean status = new File(fileInfo.getFilePath()).delete();
		if (status) {
			// deleting db entry
			return deleteFileEntry(fileId);
		}
		else {
			return status;
		}
	}

	@Override
	public boolean deleteFiles(List<Long> fileId) throws Exception {
		
		for (long id : fileId) {
			deleteFile(id);
		}
		return true;
	}

	@Override
	public boolean renameFile(long fileId, String newName) throws Exception {
		
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo == null) {
			return false;
		}

		String newFilePath = getRootPath() + File.separator + fileId + "-" + newName;
		
		File currentFile = new File(fileInfo.getFilePath());
		File newFile = new File(newFilePath);
		
		boolean status = currentFile.renameTo(newFile);
		if (status) {
			// update db entry
			updateFileEntry(fileId, newName, newFilePath, fileInfo.getFileSize(), fileInfo.getContentType());
			return true;
		}
		else {
			return false;
		}
	}	
}
