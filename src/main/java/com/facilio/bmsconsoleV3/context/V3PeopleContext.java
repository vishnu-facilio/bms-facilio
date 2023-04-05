package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.RoleApp;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class V3PeopleContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String phone;
    private String language;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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
        TENANT_CONTACT ("Tenant Contact"),
        VENDOR_CONTACT ("Vendor Contact"),
        EMPLOYEE ("Employee"),
        CLIENT_CONTACT ("Client Contact"),
        OCCUPANT ("Occupant"),
        DEFAULT ("Default"),
        OTHERS ("Others");

        String name;

        PeopleType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return this.name;
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
	@Getter@Setter
    private Boolean isEmployeePortalAccess,user,labour;
    public Boolean getEmployeePortalAccess() {
        return isEmployeePortalAccess;
    }

    public void setEmployeePortalAccess(Boolean employeePortalAccess) {
        isEmployeePortalAccess = employeePortalAccess;
    }


    public boolean isEmployeePortalAccess() {
        if (isEmployeePortalAccess != null) {
            return isEmployeePortalAccess.booleanValue();
        }
        return false;
    }

    private Map<String, Long> rolesMap;

    public Map<String, Long> getRolesMap() {
        return rolesMap;
    }

    public Map<String, Long> getScopingsMap() {
        return scopingsMap;
    }

    public void setScopingsMap(Map<String, Long> scopingsMap) {
        this.scopingsMap = scopingsMap;
    }

    private Map<String, Long> scopingsMap;

    public void setRolesMap(Map<String, Long> rolesMap) {
        this.rolesMap = rolesMap;
    }

	@Setter@Getter
	private LabourContextV3 labourContext; // for client purpose only
	@Setter@Getter
	private User userContext; // for client purpose only

	public Boolean isLabour() {
		if (labour != null) {
			return labour.booleanValue();
		}
		return false;
	}

	public Boolean isUser() {
		if (user != null) {
			return user.booleanValue();
		}
		return false;
	}

    @Setter @Getter
    private Long scopingId;
    @Getter @Setter
    private List<Long> permissionSets;
}
