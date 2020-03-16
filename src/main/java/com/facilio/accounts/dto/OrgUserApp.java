package com.facilio.accounts.dto;

import java.io.Serializable;

public class OrgUserApp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long ouid;
	private long appDomainId;
	private long deletedTime;
	
	private Boolean userStatus;
	public Boolean getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(Boolean userStatus) {
		this.userStatus = userStatus;
	}
	public boolean isActive() {
		if(userStatus != null) {
			return userStatus.booleanValue();
		}
		return false;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOuid() {
		return ouid;
	}
	public void setOuid(long ouid) {
		this.ouid = ouid;
	}
	public long getAppDomainId() {
		return appDomainId;
	}
	public void setAppDomainId(long appDomainId) {
		this.appDomainId = appDomainId;
	}
	public long getDeletedTime() {
		return deletedTime;
	}
	public void setDeletedTime(long deletedTime) {
		this.deletedTime = deletedTime;
	}

}
