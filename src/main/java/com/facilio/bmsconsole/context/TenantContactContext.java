package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.tenant.TenantContext;

public class TenantContactContext extends PeopleContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean isTenantPortalAccess;

	public Boolean getIsTenantPortalAccess() {
		return isTenantPortalAccess;
	}

	public void setIsTenantPortalAccess(Boolean tenantPortalAccess) {
		this.isTenantPortalAccess = tenantPortalAccess;
	}

	public boolean isTenantPortalAccess() {
		if (isTenantPortalAccess != null) {
			return isTenantPortalAccess.booleanValue();
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
	
}
