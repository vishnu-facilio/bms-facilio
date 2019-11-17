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

	public long getAutoSignoutTime() {
		return autoSignoutTime;
	}
	public void setAutoSignoutTime(long autoSignoutTime) {
		this.autoSignoutTime = autoSignoutTime;
	}
	long visitorFormId=-1;
	FacilioForm form;
	VisitorTypeContext visitorType;
	
	public Boolean getHostEnabled() {
		return hostEnabled;
	}
	public void setHostEnabled(Boolean hostEnabled) {
		this.hostEnabled = hostEnabled;
	}
	public Boolean getNdaEnabled() {
		return ndaEnabled;
	}
	public void setNdaEnabled(Boolean ndaEnabled) {
		this.ndaEnabled = ndaEnabled;
	}
	public Boolean getBadgeEnabled() {
		return badgeEnabled;
	}
	public void setBadgeEnabled(Boolean badgeEnabled) {
		this.badgeEnabled = badgeEnabled;
	}
	public Boolean getPhotoEnabled() {
		return photoEnabled;
	}
	public void setPhotoEnabled(Boolean photoEnabled) {
		this.photoEnabled = photoEnabled;
	}
	Boolean hostEnabled;
	Boolean ndaEnabled;
	Boolean badgeEnabled;
	Boolean photoEnabled;
	long autoSignoutTime;
//	

	
	
}
