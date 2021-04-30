package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class PeopleContext extends ModuleBaseWithCustomFields{

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

	private PeopleType peopleType;
	public int getPeopleType() {
		if (peopleType != null) {
			return peopleType.getIndex();
		}
		return -1;
	}
	public void setPeopleType(int peopleType) {
		this.peopleType = PeopleType.valueOf(peopleType);
	}
	public PeopleType getPeopleTypeEnum() {
		return peopleType;
	}
	public void setPeopleType(PeopleType occupantType) {
		this.peopleType = occupantType;
	}

	public static enum PeopleType implements FacilioIntEnum {
		TENANT_CONTACT, VENDOR_CONTACT, EMPLOYEE, CLIENT_CONTACT, OCCUPANT, DEFAULT, OTHERS;

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static PeopleType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private BaseSpaceContext locatedSpace;

	public BaseSpaceContext getLocatedSpace() {
		return locatedSpace;
	}
	public void setLocatedSpace(BaseSpaceContext locatedSpace) {
		this.locatedSpace = locatedSpace;
	}
	
	private Boolean active;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		if (active != null) {
			return active.booleanValue();
		}
		return false;
	}

	private long roleId;

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	private Boolean isOccupantPortalAccess;

	public Boolean getIsOccupantPortalAccess() {
		return isOccupantPortalAccess;
	}

	public void setIsOccupantPortalAccess(Boolean occupantPortalAccess) {
		this.isOccupantPortalAccess = occupantPortalAccess;
	}

	public boolean isOccupantPortalAccess() {
		if (isOccupantPortalAccess != null) {
			return isOccupantPortalAccess.booleanValue();
		}
		return false;
	}
}
