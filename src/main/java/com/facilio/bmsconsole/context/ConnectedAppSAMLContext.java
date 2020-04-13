package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ConnectedAppSAMLContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private long connectedAppId;
	private String spEntityId;
	private String spAcsUrl;
	private String spLogoutUrl;
	private String subjectType;
	private String nameIdFormat;

	public long getConnectedAppId() {
		return connectedAppId;
	}

	public void setConnectedAppId(long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}

	public String getSpEntityId() {
		return spEntityId;
	}

	public void setSpEntityId(String spEntityId) {
		this.spEntityId = spEntityId;
	}

	public String getSpAcsUrl() {
		return spAcsUrl;
	}

	public void setSpAcsUrl(String spAcsUrl) {
		this.spAcsUrl = spAcsUrl;
	}

	public String getSpLogoutUrl() {
		return spLogoutUrl;
	}

	public void setSpLogoutUrl(String spLogoutUrl) {
		this.spLogoutUrl = spLogoutUrl;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public String getNameIdFormat() {
		return nameIdFormat;
	}

	public void setNameIdFormat(String nameIdFormat) {
		this.nameIdFormat = nameIdFormat;
	}

}