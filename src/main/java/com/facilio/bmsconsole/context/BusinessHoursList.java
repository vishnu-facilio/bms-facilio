package com.facilio.bmsconsole.context;

import java.util.ArrayList;

public class BusinessHoursList extends ArrayList<BusinessHourContext> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
