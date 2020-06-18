package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3VendorContext extends V3Context {
    private static final long serialVersionUID = 1L;
    private String name, email, phone, website, description;
    private Long ttime, modifiedTime;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Long getTtime() {
        return ttime;
    }

    public void setTtime(Long ttime) {
        this.ttime = ttime;
    }

    public Long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private LocationContext address;

    public LocationContext getAddress() {
        return address;
    }

    public void setAddress(LocationContext address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private Long getLocationId;

    public Long getGetLocationId() {
        if (address != null) {
            return address.getId();
        }
        return null;
    }

    private List<ContactsContext> vendorContacts;

    public List<ContactsContext> getVendorContacts() {
        return vendorContacts;
    }

    public void setVendorContacts(List<ContactsContext> vendorContacts) {
        this.vendorContacts = vendorContacts;
    }
    private User registeredBy;

    public User getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(User registeredBy) {
        this.registeredBy = registeredBy;
    }


    private Boolean hasInsurance;

    public Boolean getHasInsurance() {
        if (hasInsurance != null) {
            return hasInsurance.booleanValue();
        }
        return false;
    }

    public void setHasInsurance(Boolean hasInsurance) {
        this.hasInsurance = hasInsurance;
    }

    private VendorContext.VendorSource vendorSource;
    public int getInviteSource() {
        if (vendorSource != null) {
            return vendorSource.getIndex();
        }
        return -1;
    }
    public void setVendorSource(int vendorSource) {
        this.vendorSource = VendorContext.VendorSource.valueOf(vendorSource);
    }
    public VendorContext.VendorSource getVendorSourceEnum() {
        return vendorSource;
    }
    public void setVendorSource(VendorContext.VendorSource vendorSource) {
        this.vendorSource = vendorSource;
    }

    public static enum VendorSource implements FacilioEnum {
        TENANT, SELF;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static V3VendorContext.VendorSource valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private Long sourceId;


    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    private String primaryContactName;

    public String getPrimaryContactName() {
        return primaryContactName;
    }

    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }

    private String primaryContactEmail;
    private String primaryContactPhone;

    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }

    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }

    public String getPrimaryContactPhone() {
        return primaryContactPhone;
    }

    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }



}
