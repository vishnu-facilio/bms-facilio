package com.facilio.bmsconsole.context;

public class PreventiveMaintenance {
	
	private long preventiveMaintenanceId = -1;
	public long getPreventiveMaintenanceId() {
		return preventiveMaintenanceId;
	}
	public void setPreventiveMaintenanceId(long preventiveMaintenanceId) {
		this.preventiveMaintenanceId = preventiveMaintenanceId;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	private int pmType;
	public Integer getPmType() {
		return pmType;
	}
	public void setPmType(Integer pmType) {
		this.pmType = pmType;
	}
	
	private int status;
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	private long createdBy;
	public long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}
	
	private long modifiedBy;
	public long getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	private long createdTime;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private long lastModifiedTime;
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	
	private long templateId;
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
}
