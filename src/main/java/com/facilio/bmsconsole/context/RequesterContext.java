package com.facilio.bmsconsole.context;

public class RequesterContext {
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long requesterId = 0;
	public long getRequesterId() {
		return requesterId;
	}
	public void setRequesterId(long requesterId) {
		this.requesterId = requesterId;
	}
	
	private String name;
	public String getName() {
		if (this.name == null && this.email != null) {
			this.name = this.email.substring(0, email.indexOf("@"));
			this.name = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	private boolean portalAccess;
	public boolean getPortalAccess() {
		return portalAccess;
	}
	public void setPortalAccess(boolean portalAccess) {
		this.portalAccess = portalAccess;
	}
}
