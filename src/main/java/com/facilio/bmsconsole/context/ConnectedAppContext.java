package com.facilio.bmsconsole.context;

public class ConnectedAppContext {
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String linkName;
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private String baseurl;
	public String getBaseurl() {
		return baseurl;
	}
	public void setWidgetName(String baseurl) {
		this.baseurl = baseurl;
	}
	
	private Long connectedAppId;
	public Long getConnectedAppId() {
		return connectedAppId;
	}
	public void setConnectedAppId(Long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}
}
