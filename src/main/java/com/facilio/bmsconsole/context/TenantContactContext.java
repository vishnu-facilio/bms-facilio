package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.tenant.TenantContext;

public class TenantContactContext extends PeopleContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean tenantPortalAccess;

	public Boolean getTenantPortalAccess() {
		return tenantPortalAccess;
	}

	public void setTenantPortalAccess(Boolean tenantPortalAccess) {
		this.tenantPortalAccess = tenantPortalAccess;
	}

	public boolean isTenantPortalAccess() {
		if (tenantPortalAccess != null) {
			return tenantPortalAccess.booleanValue();
		}
		return false;
	}
	
	private TenantContext tenant;

	public TenantContext getTenant() {
		return tenant;
	}

	public void setTenant(TenantContext tenant) {
		this.tenant = tenant;
	}

	private Boolean isPrimaryContact;

	public Boolean getIsPrimaryContact() {
		return isPrimaryContact;
	}

	public void setIsPrimaryContact(Boolean isPrimaryContact) {
		this.isPrimaryContact = isPrimaryContact;
	}

	public boolean isPrimaryContact() {
		if (isPrimaryContact != null) {
			return isPrimaryContact.booleanValue();
		}
		return false;
	}
	
	private Boolean occupantPortalAccess;

	public Boolean getOccupantPortalAccess() {
		return occupantPortalAccess;
	}

	public void setOccupantPortalAccess(Boolean occupantPortalAccess) {
		this.occupantPortalAccess = occupantPortalAccess;
	}

	public boolean isOccupantPortalAccess() {
		if (occupantPortalAccess != null) {
			return occupantPortalAccess.booleanValue();
		}
		return false;
	}
}
