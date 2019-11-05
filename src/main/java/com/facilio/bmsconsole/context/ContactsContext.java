package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ContactsContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;

	private String name;
	private String email;
	private String phone;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	private ContactType contactType;
	public int getContactType() {
		if (contactType != null) {
			return contactType.getIndex();
		}
		return -1;
	}
	public void setContactType(int contactType) {
		this.contactType = ContactType.valueOf(contactType);
	}
	public ContactType getContactTypeEnum() {
		return contactType;
	}
	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}

	public static enum ContactType implements FacilioEnum {
		TENANT;

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static ContactType valueOf(int value) {
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

	private TenantContext tenant;

	public TenantContext getTenant() {
		return tenant;
	}
	public void setTenant(TenantContext tenant) {
		this.tenant = tenant;
	}
	
	public Boolean isPortalAccessNeeded;
	public Boolean getIsPortalAccessNeeded() {
		return isPortalAccessNeeded;
	}
	public void setIsPortalAccessNeeded(Boolean isPortalAccessNeeded) {
		this.isPortalAccessNeeded = isPortalAccessNeeded;
	}
	
	public boolean isPortalAccessNeeded() {
		if(isPortalAccessNeeded != null ) {
			return isPortalAccessNeeded.booleanValue();
		}
		return false;
	}

	
	
}
