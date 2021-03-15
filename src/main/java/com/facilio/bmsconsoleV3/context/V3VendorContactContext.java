package com.facilio.bmsconsoleV3.context;


public class V3VendorContactContext extends V3PeopleContext {
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

    public Boolean isLabour() {
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

    public Boolean isVendorPortalAccess() {
        if (isVendorPortalAccess != null) {
            return isVendorPortalAccess.booleanValue();
        }
        return false;
    }

    private V3VendorContext vendor;

    public V3VendorContext getVendor() {
        return vendor;
    }

    public void setVendor(V3VendorContext vendor) {
        this.vendor = vendor;
    }

    private Long getVendorId() {
        if (this.vendor != null) {
            return this.vendor.getId();
        }
        return null;
    }

    private void setVendorId(Long vendorId) {
        if (vendorId != null) {
            if (vendor == null) {
                this.vendor = new V3VendorContext();
            }
            this.vendor.setId(vendorId);
        }
    }


    private Boolean isPrimaryContact;

    public Boolean getIsPrimaryContact() {
        return isPrimaryContact;
    }

    public void setIsPrimaryContact(Boolean isPrimaryContact) {
        this.isPrimaryContact = isPrimaryContact;
    }

    public Boolean isPrimaryContact() {
        if (isPrimaryContact != null) {
            return isPrimaryContact.booleanValue();
        }
        return false;
    }

}
