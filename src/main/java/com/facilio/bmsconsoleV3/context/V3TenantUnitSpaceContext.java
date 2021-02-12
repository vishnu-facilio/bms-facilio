package com.facilio.bmsconsoleV3.context;

import com.fasterxml.jackson.annotation.JsonInclude;

public class V3TenantUnitSpaceContext  extends V3SpaceContext{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private V3TenantContext tenant;

	public V3TenantContext getTenant() {
		return tenant;
	}

	public void setTenant(V3TenantContext tenant) {
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

	public boolean isOccupied() {
		if (isOccupied != null) {
			return isOccupied.booleanValue();
		}
		return false;
	}	

}
