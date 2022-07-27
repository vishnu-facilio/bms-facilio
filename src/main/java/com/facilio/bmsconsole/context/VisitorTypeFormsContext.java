package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.forms.FacilioForm;

import java.io.Serializable;

public class VisitorTypeFormsContext implements Serializable {

	
	private static final long serialVersionUID = 1L;
	long visitorTypeId=-1;
	long visitorLogFormId=-1;
	long visitorInviteFormId=-1;
	long appId=-1;

	public boolean isVisitorLogEnabled() {
		return visitorLogEnabled;
	}

	public void setVisitorLogEnabled(boolean visitorLogEnabled) {
		this.visitorLogEnabled = visitorLogEnabled;
	}

	public boolean isInviteEnabled() {
		return inviteEnabled;
	}

	public void setInviteEnabled(boolean inviteEnabled) {
		this.inviteEnabled = inviteEnabled;
	}

	boolean visitorLogEnabled;
	boolean inviteEnabled;

	public long getVisitorTypeId() {
		return visitorTypeId;
	}
	public void setVisitorTypeId(long visitorTypeId) {
		this.visitorTypeId = visitorTypeId;
	}
	
	public long getVisitorLogFormId() {
		return visitorLogFormId;
	}
	public void setVisitorLogFormId(long visitorLogFormId) {
		this.visitorLogFormId = visitorLogFormId;
	}
	public long getVisitorInviteFormId() {
		return visitorInviteFormId;
	}
	public void setVisitorInviteFormId(long visitorInviteFormId) {
		this.visitorInviteFormId = visitorInviteFormId;
	}
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	
	
}
