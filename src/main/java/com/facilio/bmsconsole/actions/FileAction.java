package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.FileContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import org.apache.commons.chain.Chain;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	private int width;
	private int height;
	public int getHeigth() {
		return height;
	}

	public void setHeigth(int height) {
		this.height = height;
	}

	public String previewFile() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if (request.getHeader("If-Modified-Since") == null) {
			if (fileID > 0) {
				FileStore fs = FileStoreFactory.getInstance().getFileStore();
				FileInfo fileInfo;
				if (width > 0 || height > 0) {
					if (height < 0) {
						fileInfo = fs.getResizedFileInfo(fileID, width, width);
					}
					else {
						fileInfo = fs.getResizedFileInfo(fileID, width, height);
					}
				}
				else {
					fileInfo = fs.getFileInfo(fileID);
				}
				if (fileInfo != null) {
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
				else {
					response.setStatus(404);
				}
			}
		} else {
			response.setStatus(304);
			return NONE;
		}
		throw new IllegalArgumentException("Cannot fetch file");
	}
	
	public String addFile() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
 		context.put(FacilioConstants.ContextNames.FILE, this.fileContent);
 		context.put(FacilioConstants.ContextNames.FILE_NAME, this.fileContentFileName);
 		context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, this.fileContentContentType);
 		
		Chain addFileChain = FacilioChainFactory.getAddFileChain();
		addFileChain.execute(context);
		
		FileContext newFile = (FileContext) context.get(FacilioConstants.ContextNames.FILE_CONTEXT_LIST);
		
		setResult("fileInfo", newFile);
		
		return SUCCESS;
	}
	
	private File fileContent;
	private String fileContentFileName;
	private String fileContentContentType;

	public File getFileContent() {
		return fileContent;
	}

	public void setFileContent(File fileContent) {
		this.fileContent = fileContent;
	}

	public String getFileContentFileName() {
		return fileContentFileName;
	}

	public void setFileContentFileName(String fileContentFileName) {
		this.fileContentFileName = fileContentFileName;
	}

	public String getFileContentContentType() {
		return fileContentContentType;
	}

	public void setFileContentContentType(String fileContentContentType) {
		this.fileContentContentType = fileContentContentType;
	}
}
