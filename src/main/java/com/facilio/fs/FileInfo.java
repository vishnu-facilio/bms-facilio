package com.facilio.fs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileInfo {

	private long orgId;
	private long fileId;
	private String fileName;
	private String filePath;
	private long fileSize;
	private String contentType;
	private long uploadedBy;
	private long uploadedTime;
	private Boolean isDeleted;
	
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public long getUploadedBy() {
		return uploadedBy;
	}
	public void setUploadedBy(long uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	public long getUploadedTime() {
		return uploadedTime;
	}
	public void setUploadedTime(long uploadedTime) {
		this.uploadedTime = uploadedTime;
	}
	
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public boolean isDeleted() {
		if (isDeleted != null) {
			return isDeleted.booleanValue();
		}
		return false;
	}

	public enum FileFormat {
		CSV(1, "CSV", ".csv", "application/csv"),
		XLS(2, "Excel", ".xls", "application/xls"),
		PDF(3, "PDF", ".pdf", "application/pdf"),
		IMAGE(4, "Image", ".jpeg", "image/jpeg"),
		HTML(5, "Html", ".html", "text/html"),
		SIGNATURE(6, "Signature", ".jpeg", "image/jpeg")
		;
		
		private int intVal;
		private String strVal;
		private String extention;
		private String contentType;
		
		private FileFormat(int intVal, String strVal, String extention, String contentType) {
			this.intVal = intVal;
			this.strVal = strVal;
			this.extention = extention;
			this.contentType = contentType;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		public String getExtention() {
			return extention;
		}
		public String getContentType() {
			return contentType;
		}

		public static FileFormat getFileFormat(int val) {
			return formatMap.get(val);
		}
		
		private static final Map<Integer, FileFormat> formatMap = Collections.unmodifiableMap(initFormatMap());
		private static Map<Integer, FileFormat> initFormatMap() {
			Map<Integer, FileFormat> fomatMap = new HashMap<>();
			
			for(FileFormat type : values()) {
				fomatMap.put(type.getIntVal(), type);
			}
			return fomatMap;
		}
	}
}
