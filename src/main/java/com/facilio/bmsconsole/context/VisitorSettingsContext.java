package com.facilio.bmsconsole.context;

import java.io.Serializable;

import com.facilio.bmsconsole.forms.FacilioForm;

public class VisitorSettingsContext implements Serializable {

	
	private static final long serialVersionUID = 1L;
	long orgId=-1;
	long visitorTypeId=-1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getVisitorTypeId() {
		return visitorTypeId;
	}
	public void setVisitorTypeId(long visitorTypeId) {
		this.visitorTypeId = visitorTypeId;
	}
	public long getVisitorFormId() {
		return visitorFormId;
	}
	public void setVisitorFormId(long visitorFormId) {
		this.visitorFormId = visitorFormId;
	}
	public FacilioForm getForm() {
		return form;
	}
	public void setForm(FacilioForm form) {
		this.form = form;
	}
	public VisitorTypeContext getVisitorType() {
		return visitorType;
	}
	public void setVisitorType(VisitorTypeContext visitorType) {
		this.visitorType = visitorType;
	}
	public Boolean getIsNdaEnabled() {
		return isNdaEnabled;
	}
	public void setIsNdaEnabled(Boolean isNdaEnabled) {
		this.isNdaEnabled = isNdaEnabled;
	}
	public Boolean getIsBadgeEnabled() {
		return isBadgeEnabled;
	}
	public void setIsBadgeEnabled(Boolean isBadgeEnabled) {
		this.isBadgeEnabled = isBadgeEnabled;
	}
	public Boolean getIsPhotoEnabled() {
		return isPhotoEnabled;
	}
	public void setIsPhotoEnabled(Boolean isPhotoEnabled) {
		this.isPhotoEnabled = isPhotoEnabled;
	}
	public long getAutoSignoutTime() {
		return autoSignoutTime;
	}
	public void setAutoSignoutTime(long autoSignoutTime) {
		this.autoSignoutTime = autoSignoutTime;
	}
	long visitorFormId=-1;
	FacilioForm form;
	VisitorTypeContext visitorType;

	Boolean isNdaEnabled;
	Boolean isBadgeEnabled;
	Boolean isPhotoEnabled;
	long autoSignoutTime;
//	

	
	
}
