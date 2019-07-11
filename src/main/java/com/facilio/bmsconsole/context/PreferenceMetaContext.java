package com.facilio.bmsconsole.context;

public class PreferenceMetaContext {

	private long id;
	private long orgId;
	private long moduleId;
	private long recordId;
	private String preferenceName;
	private String formData;
	private Boolean isActive;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	public String getPreferenceName() {
		return preferenceName;
	}
	public void setPreferenceName(String preferenceName) {
		this.preferenceName = preferenceName;
	}
	public String getFormData() {
		return formData;
	}
	public void setFormData(String formData) {
		this.formData = formData;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isActive() {
		if (isActive != null) {
			return isActive.booleanValue();
		}
		return false;
	}
	
}
