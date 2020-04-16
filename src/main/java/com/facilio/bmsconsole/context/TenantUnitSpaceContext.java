package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.fasterxml.jackson.annotation.JsonInclude;

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
	

	@JsonInclude
	private Boolean isOccupied = false;

	public Boolean getIsOccupied() {
		return isOccupied;
	}

	public void setIsOccupied(Boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	@JsonInclude
	public boolean isOccupied() {
		if (isOccupied != null) {
			return isOccupied.booleanValue();
		}
		return false;
	}
}
