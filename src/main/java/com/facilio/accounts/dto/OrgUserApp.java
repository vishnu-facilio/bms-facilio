package com.facilio.accounts.dto;

import java.io.Serializable;

public class OrgUserApp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long ouid;
	private long applicationId;
	
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
	public long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(long applicationId) {
		this.applicationId = applicationId;
	}
	
}
