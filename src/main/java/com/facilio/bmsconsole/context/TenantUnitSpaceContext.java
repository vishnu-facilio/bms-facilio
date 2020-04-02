package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.tenant.TenantContext;

public class TenantUnitSpaceContext extends SpaceContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TenantContext tenant;

	public TenantContext getTenant() {
		return tenant;
	}

	public void setTenant(TenantContext tenant) {
		this.tenant = tenant;
	}
	

}
