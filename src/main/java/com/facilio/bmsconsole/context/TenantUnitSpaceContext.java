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
	

	private Boolean isVacant;

	public Boolean getIsVacant() {
		return isVacant;
	}

	public void setIsVacant(Boolean isVacant) {
		this.isVacant = isVacant;
	}

	public boolean isVacant() {
		if (isVacant != null) {
			return isVacant.booleanValue();
		}
		return false;
	}
}
