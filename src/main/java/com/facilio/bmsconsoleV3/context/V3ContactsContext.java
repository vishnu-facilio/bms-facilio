package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

public class V3ContactsContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String phone;

    public String getName() {
//		if(requester != null && requester.getOuid() > 0) {
//			return requester.getName();
//		}
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
//		if(requester != null && requester.getOuid() > 0) {
//			return requester.getEmail();
//		}
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
//		if(requester != null && requester.getOuid() > 0) {
//			return requester.getMobile();
//		}
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    private V3ContactsContext.ContactType contactType;
    public Integer getContactType() {
        if (contactType != null) {
            return contactType.getIndex();
        }
        return null;
    }
    public void setContactType(Integer contactType) {
        if(contactType != null) {
            this.contactType = V3ContactsContext.ContactType.valueOf(contactType);
        }
    }
    public V3ContactsContext.ContactType getContactTypeEnum() {
        return contactType;
    }
    public static enum ContactType implements FacilioIntEnum {
        TENANT, VENDOR, EMPLOYEE, CLIENT;

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static V3ContactsContext.ContactType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private User requester;

    public User getRequester() {
        return requester;
    }
    public void setRequester(User requester) {
        this.requester = requester;
    }

    private V3TenantContext tenant;

    public V3TenantContext getTenant() {
        return tenant;
    }
    public void setTenant(V3TenantContext tenant) {
        this.tenant = tenant;
    }

    private V3VendorContext vendor;

    public V3VendorContext getVendor() {
        return vendor;
    }
    public void setVendor(V3VendorContext vendor) {
        this.vendor = vendor;
    }

    private V3ClientContext client;

    public V3ClientContext getClient() {
		return client;
	}

	public void setClient(V3ClientContext client) {
		this.client = client;
	}

    public Boolean isPortalAccessNeeded;
    public Boolean getIsPortalAccessNeeded() {
        return isPortalAccessNeeded;
    }
    public void setIsPortalAccessNeeded(Boolean isPortalAccessNeeded) {
        this.isPortalAccessNeeded = isPortalAccessNeeded;
    }

    public Boolean isPortalAccessNeeded() {
        if(isPortalAccessNeeded != null ) {
            return isPortalAccessNeeded.booleanValue();
        }
        return false;
    }

    public Boolean isPrimaryContact;
    public Boolean getIsPrimaryContact() {
        return isPrimaryContact;
    }
    public void setIsPrimaryContact(Boolean isPrimaryContact) {
        this.isPrimaryContact = isPrimaryContact;
    }

    public Boolean isPrimaryContact() {
        if(isPrimaryContact != null ) {
            return isPrimaryContact.booleanValue();
        }
        return false;
    }

}
