package com.facilio.bmsconsole.context;

import java.io.Serializable;

import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.util.FacilioUtil;

public class VisitorSettingsContext implements Serializable {

	
	private static final long serialVersionUID = 1L;
	long orgId=-1;
	long visitorTypeId=-1;
	Boolean isHostEnabled;
	public Boolean getIsHostEnabled() {
		return isHostEnabled;
	}
	public void setIsHostEnabled(Boolean isHostEnabled) {
		this.isHostEnabled = isHostEnabled;
	}
	
	private JSONObject hostSetttings;
	public JSONObject getHostSettings() {
		return hostSetttings;
	}
	public void setHostSettings(JSONObject hostSetttings) {
		this.hostSetttings = hostSetttings;
	}
	
	
	@JSON(serialize = false)
	public String getHostSettingsJson() {
		if (this.hostSetttings!=null)
		return hostSetttings.toJSONString();
		else {
			return null;
		}
	}
	public void setHostSettingsJson(String hostSettingsJson) {
		if(hostSettingsJson!=null)
		{
		try {
			this.hostSetttings = FacilioUtil.parseJson(hostSettingsJson);
		} catch (ParseException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
		}
		}
	}
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
