package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.fsm.context.PeopleSkillLevelContext;
import com.facilio.fsm.context.TerritoryContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;
import java.util.Map;

@Getter@Setter
public class PeopleContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;
	private String name;
	private String email;
	private String phone;
	private String language;
	@Getter @Setter
	private String mobile;
	@Getter @Setter
	private String timezone;
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

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return this.name;
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

	@Getter @Setter
	private Boolean employeePortalAccess,user,labour;

	public boolean employeePortalAccess() {
		if (employeePortalAccess != null) {
			return employeePortalAccess.booleanValue();
		}
		return false;
	}

	public Map<String, Long> getScopingsMap() {
		return scopingsMap;
	}

	public void setScopingsMap(Map<String, Long> scopingsMap) {
		this.scopingsMap = scopingsMap;
	}

	private Map<String, Long> scopingsMap;

	private Map<String, Long> rolesMap;

	public Map<String, Long> getRolesMap() {
		return rolesMap;
	}

	public void setRolesMap(Map<String, Long> rolesMap) {
		this.rolesMap = rolesMap;
	}

	@Getter
	@Setter
	private Map<String, Long> securityPolicyMap;

	public boolean isUser(){

		return user != null && user.booleanValue();
	}

	public boolean isLabour(){

		return labour != null && labour.booleanValue();
	}

	@Setter
	private LabourContextV3 labourContext; // for client purpose only
	@Setter
	private User userContext; // for client purpose only

	@Setter @Getter
	private Long scopingId;
	@Getter @Setter
	private List<Long> permissionSets;
	@Getter@Setter
	private List<Long> accessibleSpace;
	@Getter@Setter
	private List<Long> groups;
	@Getter@Setter
	private Long shiftId;
	private List<TerritoryContext> territories;

	private Status status;
	public Integer getStatus() {
		if (status != null) {
			return status.getIndex();
		}
		return null;
	}
	public void setStatus(Integer status) {
		if(status != null) {
			this.status = Status.valueOf(status);
		}
	}
	public Status getStatusEnum() {
		return status;
	}

	public enum Status implements FacilioIntEnum {
		AVAILABLE("Available"),
		EN_ROUTE("En Route"),
		ON_SITE("On Site"),
		NOT_AVAILABLE("Not Available");

		String name;

		Status(String name) {
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

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	private boolean dispatchable;
	private LocationContextV3 startLocation;
	private LocationContextV3 endLocation;
	private String currentLocation;
	private Long lastSyncTime;
	private Double currentFreeCapacity;
	private boolean trackGeoLocation;
	private Double batteryInfo;
	private Double signalInfo;
	private long avatarId;
	private String avatarUrl;
	private File avatar;
	private String avatarFileName;
	private  String avatarContentType;
	private Double rate;
	private Long lastCheckedInTime;
	private Long lastCheckedOutTime;
	private boolean checkedIn;
	private List<PeopleSkillLevelContext> peopleSkillLevel;

}
