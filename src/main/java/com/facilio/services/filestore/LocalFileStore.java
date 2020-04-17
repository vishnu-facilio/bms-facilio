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
import java.util.Collections;
import java.util.List;

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
	
	private String rootPath;
	
	@Override
	protected String getRootPath() {
		if (rootPath == null) {
			String localFileStorePath = FacilioProperties.getLocalFileStorePath();
			if (StringUtils.isEmpty(localFileStorePath)) {
				ClassLoader classLoader = LocalFileStore.class.getClassLoader();
				URL fcDataFolder = classLoader.getResource("");
				localFileStorePath = fcDataFolder.getFile();
			}
			rootPath = localFileStorePath + File.separator + "facilio-data" + File.separator + getOrgId() + File.separator + "files";

			File rootDir = new File(rootPath);
			if (!(rootDir.exists() && rootDir.isDirectory())) {
				rootDir.mkdirs();
			}
		}
		return rootPath;
	}

	/*

	select t.VIRTUAL_METER_ID as vm_id, @pv:=t.CHILD_METER_ID as c_id from (select * from Virtual_Energy_Meter_Rel order by VIRTUAL_METER_ID desc) t join (select @pv:=890168)tmp where t.VIRTUAL_METER_ID=@pv;

select * from Resources where ORGID=88 and RESOURCE_TYPE=2
 select * from Assets where PARENT_ASSET_ID IS NOT NULL and ORGID=88
select * from Energy_Meter where orgid=88
select * from Virtual_Energy_Meter_Rel where VIRTUAL_METER_ID=ENERGYMETER_ID


	*
	 */
	
	@Override
	public String getOrgiDownloadUrl(long fileId) throws Exception {
		return null;
	}

	@Override
	public boolean isFileExists(String newVersion) {
		File f = new File(getRootPath()+File.separator+"js"+File.separator+"app.js");
		return f.exists();
	}



	@Override
	public long addFile(String fileName, File file, String contentType) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandtory");
		}
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
	        
	        addComppressedFile(fileId, fileName, file, contentType);
	        
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
	public long addFile(String fileName, File file, String contentType, int[] resize) throws Exception {
		return addFile(fileName, file, contentType);
	}
	
	@Override
	public long addFile(String fileName, String content, String contentType) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandtory");
		}
		long fileId = addDummyFileEntry(fileName);
		String filePath = getRootPath() + File.separator + fileId+"-"+fileName;
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
	public String getOrgiFileUrl(long fileId) {
		return null;
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
		return new FileInputStream(new File(fileInfo.getFilePath()));
	}

	@Override
	public boolean deleteFile(long fileId) throws Exception {
		
		FileInfo fileInfo = getFileInfo(fileId);
		if (fileInfo == null) {
			return false;
		}
		
		return deleteFiles(Collections.singletonList(fileId));
		
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
	public void addComppressedFile(long fileId, String fileName, File file,String contentType) throws Exception {
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
			String resizedFilePath = getRootPath() + File.separator + fileId + "-compressed" +".jpg";
			byte[] imageInBytes = writeCompressedFile(fileId, file, contentType, baos, resizedFilePath);
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
	public boolean deleteFilePermenantly(long fileId) throws Exception {
		return deleteFilesPermanently(Collections.singletonList(fileId));
	}
	
	@Override
	public boolean deleteFilesPermanently(List<Long> fileIds) throws Exception {
		List<String> filePathList = getFilePathList(fileIds);
		boolean status = true;
		for(String path: filePathList) {
			status = new File(path).delete();
			if (status == false) {
				break;
			}
		}
		if (status) {
			deleteFileEntries(fileIds);
		}
		return status;
	}
}
