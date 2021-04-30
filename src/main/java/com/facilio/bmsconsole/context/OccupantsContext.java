package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class OccupantsContext extends ModuleBaseWithCustomFields{

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

	private OccupantType occupantType;
	public int getOccupantType() {
		if (occupantType != null) {
			return occupantType.getIndex();
		}
		return -1;
	}
	public void setOccupantType(int occupantType) {
		this.occupantType = OccupantType.valueOf(occupantType);
	}
	public OccupantType getOccupantTypeEnum() {
		return occupantType;
	}
	public void setOccupantType(OccupantType occupantType) {
		this.occupantType = occupantType;
	}

	public static enum OccupantType implements FacilioIntEnum {
		PUBLIC, TENANT, EMPLOYEE;

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static OccupantType valueOf(int value) {
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
	
	private ResourceContext locatedSpace;

	public ResourceContext getLocatedSpace() {
		return locatedSpace;
	}
	public void setLocatedSpace(ResourceContext locatedSpace) {
		this.locatedSpace = locatedSpace;
	}
	

}
