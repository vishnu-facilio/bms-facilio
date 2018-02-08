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
	
	public enum FileFormat {
		CSV(1, "CSV"),
		XLS(2, "Excel"),
		PDF(3, "PDF"),
		IMAGE(4, "Image"),
		HTML(5, "Html")
		;
		
		private int intVal;
		private String strVal;
		
		private FileFormat(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
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
		public Map<Integer, FileFormat> getAllFormats() {
			return formatMap;
		}
	}
}
