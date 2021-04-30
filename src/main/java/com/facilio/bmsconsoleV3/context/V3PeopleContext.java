package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

public class V3PeopleContext extends V3Context {
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

    private V3PeopleContext.PeopleType peopleType;
    public Integer getPeopleType() {
        if (peopleType != null) {
            return peopleType.getIndex();
        }
        return null;
    }
    public void setPeopleType(Integer peopleType) {
        if(peopleType != null) {
            this.peopleType = V3PeopleContext.PeopleType.valueOf(peopleType);
        }
    }
    public V3PeopleContext.PeopleType getPeopleTypeEnum() {
        return peopleType;
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

        public static V3PeopleContext.PeopleType valueOf(int value) {
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

    public Boolean isActive() {
        if (active != null) {
            return active.booleanValue();
        }
        return false;
    }

    private Long roleId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    private Boolean isOccupantPortalAccess;

    public Boolean getIsOccupantPortalAccess() {
        return isOccupantPortalAccess;
    }

    public void setIsOccupantPortalAccess(Boolean occupantPortalAccess) {
        this.isOccupantPortalAccess = occupantPortalAccess;
    }

    public Boolean isOccupantPortalAccess() {
        if (isOccupantPortalAccess != null) {
            return isOccupantPortalAccess.booleanValue();
        }
        return false;
    }
}
