package com.facilio.bmsconsole.context;

public class VendorContactContext extends PeopleContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean isLabour;

	public Boolean getIsLabour() {
		return isLabour;
	}

	public void setIsLabour(Boolean isLabour) {
		this.isLabour = isLabour;
	}

	public boolean isLabour() {
		if (isLabour != null) {
			return isLabour.booleanValue();
		}
		return false;
	}
	
	private Boolean isVendorPortalAccess;

	public Boolean getIsVendorPortalAccess() {
		return isVendorPortalAccess;
	}

	public void setIsVendorPortalAccess(Boolean vendorPortalAccess) {
		this.isVendorPortalAccess = vendorPortalAccess;
	}

	public boolean isVendorPortalAccess() {
		if (isVendorPortalAccess != null) {
			return isVendorPortalAccess.booleanValue();
		}
		return false;
	}
	
	private VendorContext vendor;

	public VendorContext getVendor() {
		return vendor;
	}

	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
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
