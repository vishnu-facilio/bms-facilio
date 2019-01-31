package com.facilio.bmsconsole.actions;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class FileAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long fileID;

	public long getFileID() {
		return fileID;
	}

	public void setFileID(long fileID) {
		this.fileID = fileID;
	}
	
	private File file;
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	private FileInfo fileInfo;
	private InputStream downloadStream;
	
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	public InputStream getDownloadStream() {
		return downloadStream;
	}

	public void setDownloadStream(InputStream downloadStream) {
		this.downloadStream = downloadStream;
	}
	
	private Boolean isDownload;
	public Boolean getIsDownload() {
		if (isDownload == null) {
			isDownload = false;
		}
		return isDownload;
	}

	public void setIsDownload(Boolean isDownload) {
		this.isDownload = isDownload;
	}
	
	public String previewFile() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if (request.getHeader("If-Modified-Since") == null) {
			if (fileID > 0) {
				FileStore fs = FileStoreFactory.getInstance().getFileStore();
				FileInfo fileInfo = fs.getFileInfo(fileID);
				if (fileInfo != null && fileInfo.getFileId() > 0) {
					downloadStream = fs.readFile(fileInfo);
					if (downloadStream != null) {
						String dateStamp = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z").format(new Date());
						response.setHeader("Last-Modified", dateStamp);
						if (getIsDownload()) {
							setContentType("application/x-download");
							setFilename(fileInfo.getFileName());
						}
						else {
							setContentType(fileInfo.getContentType());
						}
						return SUCCESS;
					} 
					else {
						throw new Exception("File not Found");
					}
				}
			}
		} else {
			response.setStatus(304);
			return SUCCESS;
		}
		throw new IllegalArgumentException("Cannot fetch file");
	}
	
}
