package com.facilio.bmsconsole.actions;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.FileContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

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
	
	private int width;
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	private int height;
	
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	String publicFileKey;
	
	public String getPublicFileKey() {
		return publicFileKey;
	}

	public void setPublicFileKey(String publicFileKey) {
		this.publicFileKey = publicFileKey;
	}

	public String previewPublicFile() throws Exception {
		
//		HttpServletResponse response = ServletActionContext.getResponse();
//		
//		if(publicFileKey != null) {
//			
//			PublicFileContext publicFileContext = PublicFileUtil.getPublicFileFromKey(publicFileKey);
//			
//			if(publicFileContext.getExpiresOn() <= DateTimeUtil.getCurrenTime()) {
//				response.setStatus(404);
//				return ERROR;
//			}
//			
//			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", publicFileContext.getOrgId());
//			
//			fileInfo = bean.getFile(publicFileContext.getFileId());
//			
//			FileStore fs = FacilioFactory.getFileStore();
//			
//			if (fileInfo != null) {
//				
//				downloadStream = fs.readFile(fileInfo);
//				 if (downloadStream != null) {
//					String dateStamp = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z").format(new Date());
//					response.setHeader("Last-Modified", dateStamp);
//					if (getIsDownload()) {
//						setContentType("application/x-download");
//						setFilename(fileInfo.getFileName());
//					}
//					else {
//						setContentType(fileInfo.getContentType());
//					}
//					return SUCCESS;
//				} 
//				else {
//					throw new Exception("File not Found");
//				}
//			}
//			else {
//				response.setStatus(404);
//			}
//		}
//		
		return SUCCESS;
	}

	public String previewFile() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if (request.getHeader("If-Modified-Since") == null) {
			if (fileID > 0) {
				FileStore fs = FacilioFactory.getFileStore();
				FileInfo fileInfo = null;
				if (width > 0 || height > 0) {
					if (height <= 0) {
						fileInfo = fs.getResizedFileInfo(fileID, width, width);
					}
					else {
						fileInfo = fs.getResizedFileInfo(fileID, width, height);
					}
				}
				if (fileInfo == null) {
					fileInfo = fs.getFileInfo(fileID);
				}
				if (fileInfo != null) {
					
					downloadStream = fs.readFile(fileInfo);
					 if (downloadStream != null) {
						String dateStamp = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z").format(new Date());
						setLastModified(dateStamp);
						setDayExpiry();
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
					response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
				}
			}
		} else {
			response.setStatus(HttpURLConnection.HTTP_NOT_MODIFIED);
			return NONE;
		}
		throw new IllegalArgumentException("Cannot fetch file");
	}
	
	
	public String addFile() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
 		context.put(FacilioConstants.ContextNames.FILE, this.fileContent);
 		context.put(FacilioConstants.ContextNames.FILE_NAME, this.fileContentFileName);
 		context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, this.fileContentContentType);
 		
		FacilioChain addFileChain = FacilioChainFactory.getAddFileChain();
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
