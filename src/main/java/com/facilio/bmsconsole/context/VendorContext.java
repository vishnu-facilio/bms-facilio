package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.VisitorInviteContext.InviteSource;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class VendorContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;
	private String name, email, phone, website, description;
	private long ttime, modifiedTime;

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public long getTtime() {
		return ttime;
	}

	public void setTtime(long ttime) {
		this.ttime = ttime;
	}

	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
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

	private long getLocationId;

	public long getGetLocationId() {
		if (address != null) {
			return address.getId();
		}
		return -1;
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
	
	private VendorSource vendorSource;
	public int getInviteSource() {
		if (vendorSource != null) {
			return vendorSource.getIndex();
		}
		return -1;
	}
	public void setVendorSource(int vendorSource) {
		this.vendorSource = VendorSource.valueOf(vendorSource);
	}
	public VendorSource getVendorSourceEnum() {
		return vendorSource;
	}
	public void setVendorSource(VendorSource vendorSource) {
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

		public static VendorSource valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private long sourceId;


	public long getSourceId() {
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
	
	
	private TenantContext tenant;

	public TenantContext getTenant() {
		return tenant;
	}

	public void setTenant(TenantContext tenant) {
		this.tenant = tenant;
	}
}
