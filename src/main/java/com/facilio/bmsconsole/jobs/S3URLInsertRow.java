package com.facilio.bmsconsole.jobs;

public class S3URLInsertRow{
	private long meterId = -1;
	private long orgId = -1;
	private String createdDate;
	private long createdTime = -1;
	private long fileType = -1;
	private String s3URL;
	private int isValid = -1;

	public S3URLInsertRow(long meterId, long orgId, String createdDate, long createdTime, long fileType, 
			String s3URL, int isValid) {
		this.meterId = meterId;
		this.orgId = orgId;
		this.createdDate = createdDate;
		this.createdTime = createdTime;
	
		this.s3URL = s3URL;
		this.isValid = isValid;
		this.fileType = fileType;
	}

	public S3URLInsertRow() {
		// TODO Auto-generated constructor stub
	}

	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getMeterId() {
		return meterId;
	}
	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public long getFileType() {
		return fileType;
	}
	public void setFileType(long fileType) {
		this.fileType = fileType;
	}
	public String getS3URL() {
		return s3URL;
	}
	public void setS3URL(String s3URL) {
		this.s3URL = s3URL;
	}
	public int getIsValid() {
		return isValid;
	}
	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}
	
}
