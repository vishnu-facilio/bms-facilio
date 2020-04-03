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
	

	private Boolean isOccupied;

	public Boolean getisOccupied() {
		return isOccupied;
	}

	public void setIsOccupied(Boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	public boolean isOccupied() {
		if (isOccupied != null) {
			return isOccupied.booleanValue();
		}
		return false;
	}
}
