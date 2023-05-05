package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3VendorContext extends V3Context {
    private static final long serialVersionUID = 1L;
    private String name, email, phone, website, description;
    private Long ttime, modifiedTime;
    private Long vendorLogoId;

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

    private V3VendorContext.VendorSource vendorSource;
    public Integer getInviteSource() {
        if (vendorSource != null) {
            return vendorSource.getIndex();
        }
        return null;
    }
    public void setVendorSource(Integer vendorSource) {
        if(vendorSource != null) {
            this.vendorSource = V3VendorContext.VendorSource.valueOf(vendorSource);
        }
    }
    public V3VendorContext.VendorSource getVendorSourceEnum() {
        return vendorSource;
    }

    public static enum VendorSource implements FacilioIntEnum {
        TENANT, SELF;

        @Override
        public Integer getIndex() {
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

    public void setSourceId(Long sourceId) {
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

    private V3TenantContext tenant;

    public V3TenantContext getTenant() {
        return tenant;
    }

    public void setTenant(V3TenantContext tenant) {
        this.tenant = tenant;
    }

    private List<InsuranceContext> insurance;

    public List<InsuranceContext> getInsurance() {
        return insurance;
    }

    public void setInsurance(List<InsuranceContext> insurance) {
        this.insurance = insurance;
    }

    public Long getVendorLogoId() {
        return vendorLogoId;
    }

    public void setVendorLogoId(Long vendorLogoId) {
        this.vendorLogoId = vendorLogoId;
    }
}
