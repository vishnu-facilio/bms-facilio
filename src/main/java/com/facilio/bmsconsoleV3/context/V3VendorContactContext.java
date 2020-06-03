package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.VendorContext;

public class V3VendorContactContext extends PeopleContext {
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
