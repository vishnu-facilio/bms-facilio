package com.facilio.accounts.util;

import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.bmsconsole.modules.*;

import java.util.*;

public class AccountConstants {

	public static enum GroupMemberRole {
		ADMIN(1),
		MANAGER(2),
		MEMBER(3);

		private int memberRole;
		GroupMemberRole(int memberRole) {
			this.memberRole = memberRole;
		}

		public int getMemberRole() {
			return this.memberRole;
		}

		public static GroupMemberRole getGroupMemberRole(int memberRole) {
			return ROLE_MAP.get(memberRole);
		}

		private static final Map<Integer, GroupMemberRole> ROLE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, GroupMemberRole> initTypeMap() {
			Map<Integer, GroupMemberRole> roleMap = new HashMap<>();
			for(GroupMemberRole mrole : values()) {
				roleMap.put(mrole.getMemberRole(), mrole);
			}
			return roleMap;
		}
	}
	
	public static enum UserType {
	    
		USER(1),
		REQUESTER(2)
		;

	    private int userType;

	    UserType(int userType) {
	        this.userType = userType;
	    }

	    public int getValue() {
	        return userType;
	    }
	    
	    public boolean isUser(int userType) {
	    	return (userType & this.userType) == this.userType;
	    }
	    
	    public int setUser(int userType) {
			return userType | this.userType;
		}
		
		public int unSetUser(int userType) {
			return userType & ~this.userType;
		}
	    
	    public static UserType valueOf(int eventTypeVal) {
	    	return typeMap.get(eventTypeVal);
	    }
	    
	    private static final Map<Integer, UserType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, UserType> initTypeMap() {
			Map<Integer, UserType> typeMap = new HashMap<>();
			
			for(UserType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
		public Map<Integer, UserType> getAllTypes() {
			return typeMap;
		}
	}
	
	public static enum SessionType {
		USER_LOGIN_SESSION(1),
		PERMALINK_SESSION(2)
		;

	    private int sessionType;

	    SessionType(int sessionType) {
	        this.sessionType = sessionType;
	    }

	    public int getValue() {
	        return sessionType;
	    }
	    
	    public static SessionType valueOf(int sessionType) {
	    	return typeMap.get(sessionType);
	    }
	    
	    private static final Map<Integer, SessionType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, SessionType> initTypeMap() {
			Map<Integer, SessionType> typeMap = new HashMap<>();
			
			for(SessionType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
		public Map<Integer, SessionType> getAllTypes() {
			return typeMap;
		}
	}
	
	public static class DefaultSuperAdmin {
		public static final String SUPER_ADMIN 	= "Super Administrator"; }
	public static enum CommonPermission {
		
		ORG_ACCESS_ADMINISTER(1),
		
		ORG_ACCESS_DELETE(2), // permission to close organization

		USER_ACCESS_ADMINISTER(4), // view, create or edit users

		USER_ACCESS_DELETE(8), // delete users

		GROUP_ACCESS_ADMINISTER(16), // view, create or edit groups

		GROUP_ACCESS_DELETE(32); // delete groups
		
		long commonPermission;

		CommonPermission(long commonPermission) {
			this.commonPermission = commonPermission;
		}

		public long getCommonPermission() {
			return this.commonPermission;
		}
		public static long getSumOf(CommonPermission... commonPermission) {
			long sumOf = 0;
			for (CommonPermission perm : commonPermission) {
				sumOf += perm.getCommonPermission();
			}
			return sumOf;
		}

		public static Map<String, Long> toMap() {
			HashMap<String, Long> commonPermissionMap = new HashMap<>();
			for (CommonPermission permission : CommonPermission.values()) {
				commonPermissionMap.put(permission.name(), permission.getCommonPermission());
			}
			return commonPermissionMap;
		}
	}
	
	public static enum ModulePermission {
		
		GENERAL(1),
		
		USER_MANAGEMENT(2),
		
		WORKORDER_SETTINGS(4),
		
		ALARM_SETTINGS(8),
		
		SPACE_SETTINGS(16),
		
		CONTROLLER(32),
		
		ENERGY_ANALYTICS(64),
		
		DATA_ADMINISTRATION(128),
		
		TENANT_BILLING(256),
				
		READ (512),
		
		UPDATE(1024),
		
		CREATE(2048),
		
		DELETE(4096),
		
		READ_TEAM(8192),
		
		READ_OWN(16384),
		
		UPDATE_TEAM(32768),
		
		UPDATE_OWN(65536),
		
		DELETE_TEAM(131072),
		
		DELETE_OWN(262144),
		
		CHANGE_OWNERSHIP(524288),
		
		CLOSE_WORK_ORDER(1048576),
		
		ADD_UPDATE_DELETE_TASK(2097152),
		
		CREATE_EDIT_DASHBOARD(4194304),
		
		VIEW_REPORTS(8388608),
		
		CREATE_EDIT_REPORTS(16777216),
		
		ADD_READING(33554432),
		
		EXPORT_REPORTS(67108864),
		
		CALENDAR(134217728),
		
		APPROVE_REJECT_WORKREQUEST(268435456),
		
		IMPORT(536870912),
		
		PM_PLANNER(1073741824),
		
		ACKNOWLEDGE_ALARM(2147483648L),
		
		CLEAR_ALARM(4294967296L),

		ASSET_SETTINGS(8589934592L),
		
		CREATE_WO(17179869184L),
		
		VIEW_DASHBOARDS(34359738368L),
		
		VIEW_APPROVAL(68719476736L),
		
		INVENTORY_APPROVAL(137438953472L),

		
//		GENERAL(34359738368L),
//		
//		USER_MANAGEMENT(68719476736L),
//		
//		WORKORDER_SETTINGS(137438953472L),
//		
//		ALARM_SETTINGS(274877906944L),
//		
//		SPACE_SETTINGS(549755813888L),
		
//		CONTROLLER(1099511627776L),
		
//		ENERGY_ANALYTICS(2199023255552L),
//		
//		DATA_ADMINISTRATION(4398046511104L),
//		
//		TENANT_BILLING(8796093022208L)
		;
		
		long modulePermission;
		
		ModulePermission(long modulePermission) {
			this.modulePermission = modulePermission;
		}
		
		public long getModulePermission() {
			return this.modulePermission;
		}
		
		public static long getSumOf(ModulePermission... modulePermission) {
			long sumOf = 0;
			for (ModulePermission perm : modulePermission) {
				sumOf += perm.getModulePermission();
			}
			return sumOf;
		}

		public static Map<String, Long> toMap() {
			HashMap<String, Long> modulePermissionMap = new HashMap<>();
			for (ModulePermission permission : ModulePermission.values()) {
				modulePermissionMap.put(permission.name(), permission.getModulePermission());
			}
			return modulePermissionMap;
		}
		
	}
	public static enum Permission {

		ORG_ACCESS_ADMINISTER(1), // full control over the organization

		ORG_ACCESS_DELETE(2), // permission to close organization

		USER_ACCESS_ADMINISTER(4), // view, create or edit users

		USER_ACCESS_DELETE(8), // delete users

		GROUP_ACCESS_ADMINISTER(16), // view, create or edit groups

		GROUP_ACCESS_DELETE(32), // delete groups

		WORKORDER_ACCESS_CREATE_ACCESSIBLE_SPACES(64),

		WORKORDER_ACCESS_CREATE_ANY(128),

		WORKORDER_ACCESS_UPDATE_OWN(256),

		WORKORDER_ACCESS_UPDATE_ACCESSIBLE_SPACES(512),

		WORKORDER_ACCESS_UPDATE_ANY(1024),

		WORKORDER_ACCESS_READ_OWN(2048),

		WORKORDER_ACCESS_READ_ACCESSIBLE_SPACES(4096),

		WORKORDER_ACCESS_READ_ANY(8192),

		WORKORDER_ACCESS_DELETE_OWN(16384),

		WORKORDER_ACCESS_DELETE_ACCESSIBLE_SPACES(32768),

		WORKORDER_ACCESS_DELETE_ANY(65536),

		WORKORDER_ACCESS_ASSIGN_OWN(131072),

		WORKORDER_ACCESS_ASSIGN_ACCESSIBLE_SPACES(262144),

		WORKORDER_ACCESS_ASSIGN_ANY(524288),

		WORKORDER_ACCESS_CAN_BE_ASSIGNED_ACCESSIBLE_SPACES(1048576),

		WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY(2097152),

		TASK_ACCESS_CREATE_ANY(4194304),

		TASK_ACCESS_UPDATE_OWN(8388608),

		TASK_ACCESS_UPDATE_ANY(16777216),

		TASK_ACCESS_READ_OWN(33554432),

		TASK_ACCESS_READ_ANY(67108864),

		TASK_ACCESS_DELETE_OWN(134217728),

		TASK_ACCESS_DELETE_ANY(268435456),

		TASK_ACCESS_ASSIGN_OWN(536870912),

		TASK_ACCESS_ASSIGN_ACCESSIBLE_GRPUPS(1073741824),

		TASK_ACCESS_ASSIGN_ANY(2147483648L),

		TASK_ACCESS_CAN_BE_ASSIGNED_ANY(4294967296L),

		DASHBOARD_ACCESS_ENABLE(8589934592L),

		REPORTS_ACCESS_ENABLE(17179869184L),

		SPACEMANAGEMENT_ACCESS_ENABLE(34359738368L),

		FIREALARM_ACCESS_ENABLE(68719476736L);

		long permission;

		Permission(long permission) {
			this.permission = permission;
		}

		public long getPermission() {
			return this.permission;
		}

		public static long getSumOf(Permission... permissions) {
			long sumOf = 0;
			for (Permission perm : permissions) {
				sumOf += perm.getPermission();
			}
			return sumOf;
		}

		public static Map<String, Long> toMap() {
			HashMap<String, Long> permissionMap = new HashMap<>();
			for (Permission permission : Permission.values()) {
				permissionMap.put(permission.name(), permission.getPermission());
			}
			return permissionMap;
		}
	}

	public static enum PermissionGroup {

		SETUP(
				Permission.ORG_ACCESS_ADMINISTER,
				Permission.ORG_ACCESS_DELETE,
				Permission.USER_ACCESS_ADMINISTER,
				Permission.USER_ACCESS_DELETE,
				Permission.GROUP_ACCESS_ADMINISTER,
				Permission.GROUP_ACCESS_DELETE
				),

		WORKORDER_CREATE(
				Permission.WORKORDER_ACCESS_CREATE_ACCESSIBLE_SPACES,
				Permission.WORKORDER_ACCESS_CREATE_ANY
				),

		WORKORDER_UPDATE(
				Permission.WORKORDER_ACCESS_UPDATE_ACCESSIBLE_SPACES,
				Permission.WORKORDER_ACCESS_UPDATE_OWN,
				Permission.WORKORDER_ACCESS_UPDATE_ANY
				),

		WORKORDER_READ(
				Permission.WORKORDER_ACCESS_READ_ACCESSIBLE_SPACES,
				Permission.WORKORDER_ACCESS_READ_OWN,
				Permission.WORKORDER_ACCESS_READ_ANY
				),

		WORKORDER_DELETE(
				Permission.WORKORDER_ACCESS_DELETE_ACCESSIBLE_SPACES,
				Permission.WORKORDER_ACCESS_DELETE_OWN,
				Permission.WORKORDER_ACCESS_DELETE_ANY
				),

		WORKORDER_ASSIGN(
				Permission.WORKORDER_ACCESS_ASSIGN_ACCESSIBLE_SPACES,
				Permission.WORKORDER_ACCESS_ASSIGN_OWN,
				Permission.WORKORDER_ACCESS_ASSIGN_ANY
				),

		TASK_CREATE(
				Permission.TASK_ACCESS_CREATE_ANY
				),

		TASK_UPDATE(
				Permission.TASK_ACCESS_UPDATE_OWN,
				Permission.TASK_ACCESS_UPDATE_ANY
				),

		TASK_READ(
				Permission.TASK_ACCESS_READ_OWN,
				Permission.TASK_ACCESS_READ_ANY
				),

		TASK_DELETE(
				Permission.TASK_ACCESS_DELETE_OWN,
				Permission.TASK_ACCESS_DELETE_ANY
				),

		TASK_ASSIGN(
				Permission.TASK_ACCESS_ASSIGN_ACCESSIBLE_GRPUPS,
				Permission.TASK_ACCESS_ASSIGN_OWN,
				Permission.TASK_ACCESS_ASSIGN_ANY
				);


		Permission[] permission;

		PermissionGroup(Permission... permission) {
			this.permission = permission;
		}

		public Permission[] getPermission() {
			return this.permission;
		}

		public static long getSumOf(Permission... permissions) {
			long sumOf = 0;
			for (Permission perm : permissions) {
				sumOf += perm.getPermission();
			}
			return sumOf;
		}

		public static Map<String, List<String>> toMap() {
			HashMap<String, List<String>> permissionGroupMap = new HashMap<>();
			for (PermissionGroup permissionGroup : PermissionGroup.values()) {
				List<String> permissions = new ArrayList<>();
				for (Permission permission : permissionGroup.getPermission()) {
					permissions.add(permission.name());
				}
				permissionGroupMap.put(permissionGroup.name(), permissions);
			}
			return permissionGroupMap;
		}
	}
	
//	public static class DefaultPermssion {
//		public static final String WORKORDER_READ = "Workorder Read";
//		public static final String WORKORDER_UPDATE = "Workorder Update";
//		public static final String WORKORDER_CREATE = "Workorder Create";
//		public static final String WORKORDER_DELETE = "Workorder Delete";
//		
//		public static final Map<String, Permissions> DEFAULT_PERMISSIONS = Collections.unmodifiableMap(initPermissions());
//		
//		private static Map<String, Permissions> initPermissions() {
//			Map<String, Permissions> defaultPermissions = new HashMap<>();
//			
//			Permissions workorderCreate = new Permissions();
//			workorderCreate.setRoleId(1);
//			workorderCreate.setModuleName("workorder");
//			workorderCreate.setPermission(ModulePermission.getSumOf(ModulePermission.WORKORDER_CREATE));
//			
//		}
//		
//		
//	}

	public static class DefaultRole 
	{
		public static final String SUPER_ADMIN 	= "Super Administrator";
//		public static final String ADMINISTRATOR 	= "Administrator";
//		public static final String MANAGER 		    = "Manager";
//		public static final String DISPATCHER 		= "Dispatcher";
//		public static final String TECHNICIAN 		= "Technician";

		public static final Map<String, Role> DEFAULT_ROLES = Collections.unmodifiableMap(initRoles());

		private static Map<String, Role> initRoles()
		{
			Map<String, Role> defaultRoles = new HashMap<>();

			List<Permissions> superAdminPermission = new ArrayList<>();
			superAdminPermission.add(new Permissions("workorder", ModulePermission.getSumOf(ModulePermission.CREATE, 
					ModulePermission.DELETE,
					ModulePermission.READ,
					ModulePermission.UPDATE)));
			List<Permissions> adminPermission = new ArrayList<>();
			adminPermission.add(new Permissions("workorder", ModulePermission.getSumOf(ModulePermission.CREATE, 
					ModulePermission.DELETE,
					ModulePermission.READ,
					ModulePermission.UPDATE)));

			Role superAdmin = new Role();
			superAdmin.setName(SUPER_ADMIN);
			superAdmin.setDescription(SUPER_ADMIN);
//			superAdmin.setPermissions(0L);
//			superAdmin.setPermissions(superAdminPermission);
			
//			Role administrator = new Role();
//			administrator.setName(ADMINISTRATOR);
//			administrator.setDescription(ADMINISTRATOR);
//			administrator.setPermissions(0L);
//			administrator.setPermissions(adminPermission);
			
//			Role manager = new Role();
//			manager.setName(MANAGER);
//			manager.setDescription(MANAGER);
//			manager.setPermissions(adminPermission);
//			manager.setPermissions(Permission.getSumOf(
//					Permission.USER_ACCESS_ADMINISTER,
//					Permission.GROUP_ACCESS_ADMINISTER,
//					Permission.WORKORDER_ACCESS_CREATE_ANY,
//					Permission.WORKORDER_ACCESS_UPDATE_ANY, 
//					Permission.WORKORDER_ACCESS_READ_ANY,
//					Permission.WORKORDER_ACCESS_DELETE_ANY,
//					Permission.WORKORDER_ACCESS_ASSIGN_ANY,
//					Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY,
//					Permission.TASK_ACCESS_CREATE_ANY,
//					Permission.TASK_ACCESS_UPDATE_ANY,
//					Permission.TASK_ACCESS_READ_ANY,
//					Permission.TASK_ACCESS_DELETE_ANY,
//					Permission.TASK_ACCESS_ASSIGN_ANY,
//					Permission.TASK_ACCESS_CAN_BE_ASSIGNED_ANY,
//					Permission.DASHBOARD_ACCESS_ENABLE,
//					Permission.REPORTS_ACCESS_ENABLE
//					));
//			
//			Role dispatcher = new Role();
//			dispatcher.setName(DISPATCHER);
//			dispatcher.setDescription(DISPATCHER);
//			dispatcher.setPermissions(adminPermission);
//			dispatcher.setPermissions(Permission.getSumOf(
//					Permission.WORKORDER_ACCESS_CREATE_ANY,
//					Permission.WORKORDER_ACCESS_UPDATE_ANY, 
//					Permission.WORKORDER_ACCESS_READ_ANY,
//					Permission.WORKORDER_ACCESS_DELETE_ANY,
//					Permission.WORKORDER_ACCESS_ASSIGN_ANY,
//					Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY,
//					Permission.TASK_ACCESS_CREATE_ANY,
//					Permission.TASK_ACCESS_UPDATE_ANY,
//					Permission.TASK_ACCESS_READ_ANY,
//					Permission.TASK_ACCESS_DELETE_ANY,
//					Permission.TASK_ACCESS_ASSIGN_ANY,
//					Permission.TASK_ACCESS_CAN_BE_ASSIGNED_ANY
//					));
//			
//			Role technician = new Role();
//			technician.setName(TECHNICIAN);
//			technician.setDescription(TECHNICIAN);
//			technician.setPermissions(adminPermission);
//			technician.setPermissions(Permission.getSumOf(
//					Permission.WORKORDER_ACCESS_UPDATE_OWN, 
//					Permission.WORKORDER_ACCESS_READ_OWN,
//					Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY,
//					Permission.TASK_ACCESS_UPDATE_OWN,
//					Permission.TASK_ACCESS_READ_OWN,
//					Permission.TASK_ACCESS_CAN_BE_ASSIGNED_ANY
//					));
			
			defaultRoles.put(SUPER_ADMIN, superAdmin);
//			defaultRoles.put(ADMINISTRATOR, administrator);
//			defaultRoles.put(MANAGER, manager);
//			defaultRoles.put(DISPATCHER, dispatcher);
//			defaultRoles.put(TECHNICIAN, technician);
			return defaultRoles;
		}
	}

	public static FacilioModule getUserModule() {
		FacilioModule userModule = new FacilioModule();
		userModule.setName("user");
		userModule.setDisplayName("Users");
		userModule.setTableName("Users");

		return userModule;
	}

	public static FacilioModule getFacilioUserModule() {
		FacilioModule userModule = new FacilioModule();
		userModule.setName("faciliousers");
		userModule.setDisplayName("faciliousers");
		userModule.setTableName("faciliousers");

		return userModule;
	}

	public static FacilioModule getPortalUserModule() {
		FacilioModule userModule = new FacilioModule();
		userModule.setName("portaluser");
		userModule.setDisplayName("PortalUsers");
		userModule.setTableName("faciliorequestors");

		return userModule;
	}


	public static FacilioModule getUserMobileSettingModule() {
		FacilioModule userModule = new FacilioModule();
		userModule.setName("userMobileSetting");
		userModule.setDisplayName("User Mobile Setting");
		userModule.setTableName("User_Mobile_Setting");

		return userModule;
	}

	public static FacilioModule getOrgModule() {
		FacilioModule orgModule = new FacilioModule();
		orgModule.setName("org");
		orgModule.setDisplayName("Organizations");
		orgModule.setTableName("Organizations");

		return orgModule;
	}

	public static FacilioModule getOrgUserModule() {
		FacilioModule orgModule = new FacilioModule();
		orgModule.setName("orguser");
		orgModule.setDisplayName("Org Users");
		orgModule.setTableName("ORG_Users");

		return orgModule;
	}
	
	public static FacilioModule getUserSessionModule() {
		FacilioModule userSession = new FacilioModule();
		userSession.setName("usersession");
		userSession.setDisplayName("User Sessions");
		userSession.setTableName("UserSessions");

		return userSession;
	}

	public static FacilioModule getLicenseModule() {
		FacilioModule license = new FacilioModule();
		license.setName("license");
		license.setDisplayName("License");
		license.setTableName("License");

		return license;
	}
	
	public static FacilioModule getFeatureLicenseModule() {
		FacilioModule license = new FacilioModule();
		license.setName("featurelicense");
		license.setDisplayName("Feature License");
		license.setTableName("FeatureLicense");

		return license;
	}
	
	public static FacilioModule getUserLicenseModule() {
		FacilioModule userLicense = new FacilioModule();
		userLicense.setName("userlicense");
		userLicense.setDisplayName("User License");
		userLicense.setTableName("UserLicense");

		return userLicense;
	}
	public static FacilioModule getOrgInfoModule() {
		FacilioModule orgModule = new FacilioModule();
		orgModule.setName("orginfo");
		orgModule.setDisplayName("OrgInfo");
		orgModule.setTableName("OrgInfo");

		return orgModule;
	}

	public static FacilioModule getGroupModule() {
		FacilioModule groupModule = new FacilioModule();
		groupModule.setName("group");
		groupModule.setDisplayName("Groups");
		groupModule.setTableName("Groups");

		return groupModule;
	}

	public static FacilioModule getGroupMemberModule() {
		FacilioModule groupMemberModule = new FacilioModule();
		groupMemberModule.setName("groupmember");
		groupMemberModule.setDisplayName("Group Members");
		groupMemberModule.setTableName("GroupMembers");

		return groupMemberModule;
	}
	
	public static FacilioModule getRoleModule() {
		FacilioModule roleModule = new FacilioModule();
		roleModule.setName("role");
		roleModule.setDisplayName("Roles");
		roleModule.setTableName("Role");

		return roleModule;
	}
	
	public static FacilioModule getPermissionModule() {
		FacilioModule permissionModule = new FacilioModule();
		permissionModule.setName("permission");
		permissionModule.setDisplayName("Permission");
		permissionModule.setTableName("Permission");
		
		return permissionModule;
	}
	
	public static List<FacilioField> getOrgFields() {
		FacilioModule module = getOrgModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.ID);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("ORGNAME");
		name.setModule(module);
		fields.add(name);

		FacilioField domain = new FacilioField();
		domain.setName("domain");
		domain.setDataType(FieldType.STRING);
		domain.setColumnName("FACILIODOMAINNAME");
		domain.setModule(module);
		fields.add(domain);

		FacilioField logoId = new FacilioField();
		logoId.setName("logoId");
		logoId.setDataType(FieldType.NUMBER);
		logoId.setColumnName("LOGO_ID");
		logoId.setModule(module);
		fields.add(logoId);

		FacilioField phone = new FacilioField();
		phone.setName("phone");
		phone.setDataType(FieldType.STRING);
		phone.setColumnName("PHONE");
		phone.setModule(module);
		fields.add(phone);

		FacilioField mobile = new FacilioField();
		mobile.setName("mobile");
		mobile.setDataType(FieldType.STRING);
		mobile.setColumnName("MOBILE");
		mobile.setModule(module);
		fields.add(mobile);

		FacilioField fax = new FacilioField();
		fax.setName("fax");
		fax.setDataType(FieldType.STRING);
		fax.setColumnName("FAX");
		fax.setModule(module);
		fields.add(fax);

		FacilioField street = new FacilioField();
		street.setName("street");
		street.setDataType(FieldType.STRING);
		street.setColumnName("STREET");
		street.setModule(module);
		fields.add(street);

		FacilioField city = new FacilioField();
		city.setName("city");
		city.setDataType(FieldType.STRING);
		city.setColumnName("CITY");
		city.setModule(module);
		fields.add(city);

		FacilioField state = new FacilioField();
		state.setName("state");
		state.setDataType(FieldType.STRING);
		state.setColumnName("STATE");
		state.setModule(module);
		fields.add(state);

		FacilioField zip = new FacilioField();
		zip.setName("zip");
		zip.setDataType(FieldType.STRING);
		zip.setColumnName("ZIP");
		zip.setModule(module);
		fields.add(zip);

		FacilioField country = new FacilioField();
		country.setName("country");
		country.setDataType(FieldType.STRING);
		country.setColumnName("COUNTRY");
		country.setModule(module);
		fields.add(country);

		FacilioField timezone = new FacilioField();
		timezone.setName("timezone");
		timezone.setDataType(FieldType.STRING);
		timezone.setColumnName("TIMEZONE");
		timezone.setModule(module);
		fields.add(timezone);
		
		FacilioField currency = new FacilioField();
		currency.setName("currency");
		currency.setDataType(FieldType.STRING);
		currency.setColumnName("CURRENCY");
		currency.setModule(module);
		fields.add(currency);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(module);
		fields.add(createdTime);

		return fields;
	}

	public static List<FacilioField> getOrgInfoFields() {
		FacilioModule module = getOrgInfoModule();
		List<FacilioField> fields = new ArrayList<>();

		/*fields.add(FieldFactory.getOrgIdField(module));*/

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);

		FacilioField value = new FacilioField();
		value.setName("value");
		value.setDataType(FieldType.STRING);
		value.setColumnName("VALUE");
		value.setModule(module);
		fields.add(value);

		return fields;
	}


	public static List<FacilioField> getFacilioUserFields() {
		FacilioModule module = getFacilioUserModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField uid = new FacilioField();
		uid.setName("uid");
		uid.setDataType(FieldType.ID);
		uid.setColumnName("USERID");
		uid.setModule(module);
		fields.add(uid);

		FacilioField name = new FacilioField();
		name.setName("username");
		name.setDataType(FieldType.STRING);
		name.setColumnName("username");
		name.setModule(module);
		fields.add(name);

		FacilioField password = new FacilioField();
		password.setName("password");
		password.setDataType(FieldType.STRING);
		password.setColumnName("password");
		password.setModule(module);
		fields.add(password);

		FacilioField email = new FacilioField();
		email.setName("email");
		email.setDataType(FieldType.STRING);
		email.setColumnName("email");
		email.setModule(module);
		fields.add(email);

		return fields;
	}

	public static FacilioField getUserIdField(FacilioModule module) {
		FacilioField uid = new FacilioField();
		uid.setName("uid");
		uid.setDataType(FieldType.NUMBER);
		uid.setColumnName("USERID");
		uid.setModule(module);
		return uid;
	}

	public static FacilioField getUserPasswordField() {
		FacilioField password = new FacilioField();
		password.setName("password");
		password.setDataType(FieldType.STRING);
		password.setColumnName("PASSWORD");
		password.setModule(getUserModule());
		return password;
	}

	public static List<FacilioField> getUserFields() {
		FacilioModule module = getUserModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField uid = new FacilioField();
		uid.setName("uid");
		uid.setDataType(FieldType.ID);
		uid.setColumnName("USERID");
		uid.setModule(module);
		fields.add(uid);

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);

	/*	FacilioField cognitoId = new FacilioField();
		cognitoId.setName("cognitoId");
		cognitoId.setDataType(FieldType.STRING);
		cognitoId.setColumnName("COGNITO_ID");
		cognitoId.setModule(module);
		fields.add(cognitoId);*/

		FacilioField userVerified = new FacilioField();
		userVerified.setName("userVerified");
		userVerified.setDataType(FieldType.BOOLEAN);
		userVerified.setColumnName("USER_VERIFIED");
		userVerified.setModule(module);
		fields.add(userVerified);

		FacilioField email = new FacilioField();
		email.setName("email");
		email.setDataType(FieldType.STRING);
		email.setColumnName("EMAIL");
		email.setModule(module);
		fields.add(email);

		FacilioField photoId = new FacilioField();
		photoId.setName("photoId");
		photoId.setDataType(FieldType.NUMBER);
		photoId.setColumnName("PHOTO_ID");
		photoId.setModule(module);
		fields.add(photoId);

		FacilioField timezone = new FacilioField();
		timezone.setName("timezone");
		timezone.setDataType(FieldType.STRING);
		timezone.setColumnName("TIMEZONE");
		timezone.setModule(module);
		fields.add(timezone);

		FacilioField language = new FacilioField();
		language.setName("language");
		language.setDataType(FieldType.STRING);
		language.setColumnName("LANGUAGE");
		language.setModule(module);
		fields.add(language);

		FacilioField phone = new FacilioField();
		phone.setName("phone");
		phone.setDataType(FieldType.STRING);
		phone.setColumnName("PHONE");
		phone.setModule(module);
		fields.add(phone);

		FacilioField mobile = new FacilioField();
		mobile.setName("mobile");
		mobile.setDataType(FieldType.STRING);
		mobile.setColumnName("MOBILE");
		mobile.setModule(module);
		fields.add(mobile);		

		FacilioField street = new FacilioField();
		street.setName("street");
		street.setDataType(FieldType.STRING);
		street.setColumnName("STREET");
		street.setModule(module);
		fields.add(street);

		FacilioField city = new FacilioField();
		city.setName("city");
		city.setDataType(FieldType.STRING);
		city.setColumnName("CITY");
		city.setModule(module);
		fields.add(city);

		FacilioField state = new FacilioField();
		state.setName("state");
		state.setDataType(FieldType.STRING);
		state.setColumnName("STATE");
		state.setModule(module);
		fields.add(state);

		FacilioField zip = new FacilioField();
		zip.setName("zip");
		zip.setDataType(FieldType.STRING);
		zip.setColumnName("ZIP");
		zip.setModule(module);
		fields.add(zip);

		FacilioField country = new FacilioField();
		country.setName("country");
		country.setDataType(FieldType.STRING);
		country.setColumnName("COUNTRY");
		country.setModule(module);
		fields.add(country);

		return fields;
	}

	public static FacilioModule getPortalInfoModule(){
        FacilioModule portalInfoModule = new FacilioModule();
        portalInfoModule.setName("portalinfo");
        portalInfoModule.setDisplayName("PortalInfo");
        portalInfoModule.setTableName("PortalInfo");
        return portalInfoModule;
    }
	
	public static List<FacilioField> getPortalUserFields() {
		FacilioModule module = getPortalUserModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField portalId = new FacilioField();
		portalId.setName("portalId");
		portalId.setDataType(FieldType.NUMBER);
		portalId.setColumnName("PORTALID");
		portalId.setModule(module);
		fields.add(portalId);

		FacilioField name = new FacilioField();
		name.setName("username");
		name.setDataType(FieldType.STRING);
		name.setColumnName("username");
		name.setModule(module);
		fields.add(name);

		FacilioField email = new FacilioField();
		email.setName("email");
		email.setDataType(FieldType.STRING);
		email.setColumnName("email");
		email.setModule(module);
		fields.add(email);

		return fields;
	}
	
	
	public static List<FacilioField> getPortalCustomDomainFields() {
		FacilioModule module = getPortalInfoModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField portalId = new FacilioField();
		portalId.setName("portalId");
		portalId.setDataType(FieldType.NUMBER);
		portalId.setColumnName("PORTALID");
		portalId.setModule(module);
		fields.add(portalId);

		FacilioField name = new FacilioField();
		name.setName("customDomain");
		name.setDataType(FieldType.STRING);
		name.setColumnName("CUSTOM_DOMAIN");
		name.setModule(module);
		fields.add(name);

		return fields;
	}

	public static List<FacilioField> getUserMobileSettingFields() {
		FacilioModule module = getUserMobileSettingModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField uid = new FacilioField();
		uid.setName("userId");
		uid.setDataType(FieldType.NUMBER);
		uid.setColumnName("USERID");
		uid.setModule(module);
		fields.add(uid);

		FacilioField userMobileSettingId = new FacilioField();
		userMobileSettingId.setName("userMobileSettingId");
		userMobileSettingId.setDataType(FieldType.ID);
		userMobileSettingId.setColumnName("USER_MOBILE_SETTING_ID");
		userMobileSettingId.setModule(module);
		fields.add(userMobileSettingId);

		FacilioField mobileInstanceId = new FacilioField();
		mobileInstanceId.setName("mobileInstanceId");
		mobileInstanceId.setDataType(FieldType.STRING);
		mobileInstanceId.setColumnName("MOBILE_INSTANCE_ID");
		mobileInstanceId.setModule(module);
		fields.add(mobileInstanceId);
		
		fields.add(FieldFactory.getField("createdTime", "CREATED_TIME", module, FieldType.DATE_TIME));
		
		fields.add(FieldFactory.getField("fromPortal", "IS_FROM_PORTAL", module, FieldType.BOOLEAN));

		return fields;
	}


	public static List<FacilioField> getOrgUserFields() {
		FacilioModule module = getOrgUserModule();
		module.setExtendModule(ModuleFactory.getResourceModule());
		List<FacilioField> fields = new ArrayList<>();

		FacilioField ouid = new FacilioField();
		ouid.setName("ouid");
		ouid.setDataType(FieldType.ID);
		ouid.setColumnName("ORG_USERID");
		ouid.setModule(module);
		fields.add(ouid);

		fields.add(getUserIdField(module));

		/*FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);*/
		
		FacilioField license = new FacilioField();
		license.setName("license");
		license.setDataType(FieldType.NUMBER);
		license.setColumnName("LICENSE");
		license.setModule(module);
		fields.add(license);
	/*	FacilioField otp = new FacilioField();
		otp.setName("otp");
		otp.setDataType(FieldType.STRING);
		otp.setColumnName("OTP");
		otp.setModule(module);
		fields.add(otp);
		
		FacilioField otp_time = new FacilioField();
		otp_time.setName("otpTime");
		otp_time.setDataType(FieldType.NUMBER);
		otp_time.setColumnName("OTP_TIME");
		otp_time.setModule(module);
		fields.add(otp_time);*/

		FacilioField invitedTime = new FacilioField();
		invitedTime.setName("invitedTime");
		invitedTime.setDataType(FieldType.NUMBER);
		invitedTime.setColumnName("INVITEDTIME");
		invitedTime.setModule(module);
		fields.add(invitedTime);

		FacilioField isDefaultOrg = new FacilioField();
		isDefaultOrg.setName("isDefaultOrg");
		isDefaultOrg.setDataType(FieldType.BOOLEAN);
		isDefaultOrg.setColumnName("ISDEFAULT");
		isDefaultOrg.setModule(module);
		fields.add(isDefaultOrg);

		FacilioField userStatus = new FacilioField();
		userStatus.setName("userStatus");
		userStatus.setDataType(FieldType.BOOLEAN);
		userStatus.setColumnName("USER_STATUS");
		userStatus.setModule(module);
		fields.add(userStatus);

		FacilioField inviteAcceptStatus = new FacilioField();
		inviteAcceptStatus.setName("inviteAcceptStatus");
		inviteAcceptStatus.setDataType(FieldType.BOOLEAN);
		inviteAcceptStatus.setColumnName("INVITATION_ACCEPT_STATUS");
		inviteAcceptStatus.setModule(module);
		fields.add(inviteAcceptStatus);

		FacilioField roleId = new FacilioField();
		roleId.setName("roleId");
		roleId.setDataType(FieldType.NUMBER);
		roleId.setColumnName("ROLE_ID");
		roleId.setModule(module);
		fields.add(roleId);

		FacilioField userType = new FacilioField();
		userType.setName("userType");
		userType.setDataType(FieldType.NUMBER);
		userType.setColumnName("USER_TYPE");
		userType.setModule(module);
		fields.add(userType);
		
		FacilioField portal_verified = new FacilioField();
		portal_verified.setName("portal_verified");
		portal_verified.setDataType(FieldType.BOOLEAN);
		portal_verified.setColumnName("PORTAL_VERIFIED");
		portal_verified.setModule(module);
		fields.add(portal_verified);
	
		return fields;
	}
	
	public static List<FacilioField> getLicenseFields() {
		FacilioModule module = getLicenseModule();
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField id = new FacilioField();
		id.setName("id");
		id.setDataType(FieldType.NUMBER);
		id.setColumnName("ID");
		id.setModule(module);
		fields.add(id);
		
		/*FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);*/
		
		FacilioField license = new FacilioField();
		license.setName("license");
		license.setDataType(FieldType.NUMBER);
		license.setColumnName("LICENSE");
		license.setModule(module);
		fields.add(license);
		
		FacilioField totalLicense = new FacilioField();
		totalLicense.setName("totalLicense");
		totalLicense.setDataType(FieldType.NUMBER);
		totalLicense.setColumnName("TOTAL_LICENSE");
		totalLicense.setModule(module);
		fields.add(totalLicense);
		
		FacilioField usedLicense = new FacilioField();
		usedLicense.setName("usedLicense");
		usedLicense.setDataType(FieldType.NUMBER);
		usedLicense.setColumnName("USED_LICENSE");
		usedLicense.setModule(module);
		fields.add(usedLicense);
		
		FacilioField expiryDate = new FacilioField();
		expiryDate.setName("expiryDate");
		expiryDate.setDataType(FieldType.NUMBER);
		expiryDate.setColumnName("EXPIRY_DATE ");
		expiryDate.setModule(module);
		fields.add(expiryDate);
		
		return fields;
	}
	
	public static List<FacilioField> getFeatureLicenseFields() {
		FacilioModule module = getFeatureLicenseModule();
		List<FacilioField> fields = new ArrayList<>();
		
		/*FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);*/
		
		FacilioField license = new FacilioField();
		license.setName("module");
		license.setDataType(FieldType.NUMBER);
		license.setColumnName("MODULE");
		license.setModule(module);
		fields.add(license);
		
		return fields;
	}
	
	public static List<FacilioField> getUserLicenseFields() {
		FacilioModule module = getUserLicenseModule();
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField id = new FacilioField();
		id.setName("id");
		id.setDataType(FieldType.NUMBER);
		id.setColumnName("ID");
		id.setModule(module);
		fields.add(id);
		
		/*FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);*/
		
		FacilioField roleId = new FacilioField();
		roleId.setName("roleId");
		roleId.setDataType(FieldType.NUMBER);
		roleId.setColumnName("ROLE_ID");
		roleId.setModule(module);
		fields.add(roleId);
		
		FacilioField numberofusers = new FacilioField();
		numberofusers.setName("numberofusers");
		numberofusers.setDataType(FieldType.NUMBER);
		numberofusers.setColumnName("NUMBER_OF_USERS");
		numberofusers.setModule(module);
		fields.add(numberofusers);
		
		return fields;
	}
	
	public static List<FacilioField> getUserSessionFields() {
		FacilioModule module = getUserSessionModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField id = new FacilioField();
		id.setName("id");
		id.setDataType(FieldType.ID);
		id.setColumnName("SESSIONID");
		id.setModule(module);
		fields.add(id);
		
		fields.add(getUserIdField(module));
		
		FacilioField sessionType = new FacilioField();
		sessionType.setName("sessionType");
		sessionType.setDataType(FieldType.NUMBER);
		sessionType.setColumnName("SESSION_TYPE");
		sessionType.setModule(module);
		fields.add(sessionType);
		
		FacilioField token = new FacilioField();
		token.setName("token");
		token.setDataType(FieldType.STRING);
		token.setColumnName("TOKEN");
		token.setModule(module);
		fields.add(token);

		FacilioField startTime = new FacilioField();
		startTime.setName("startTime");
		startTime.setDataType(FieldType.NUMBER);
		startTime.setColumnName("START_TIME");
		startTime.setModule(module);
		fields.add(startTime);
		
		FacilioField endTime = new FacilioField();
		endTime.setName("endTime");
		endTime.setDataType(FieldType.NUMBER);
		endTime.setColumnName("END_TIME");
		endTime.setModule(module);
		fields.add(endTime);
		
		FacilioField isActive = new FacilioField();
		isActive.setName("isActive");
		isActive.setDataType(FieldType.BOOLEAN);
		isActive.setColumnName("IS_ACTIVE");
		isActive.setModule(module);
		fields.add(isActive);

		FacilioField ipAddress = new FacilioField();
		ipAddress.setName("ipAddress");
		ipAddress.setDataType(FieldType.STRING);
		ipAddress.setColumnName("IPADDRESS");
		ipAddress.setModule(module);
		fields.add(ipAddress);
		
		FacilioField userAgent = new FacilioField();
		userAgent.setName("userAgent");
		userAgent.setDataType(FieldType.STRING);
		userAgent.setColumnName("USER_AGENT");
		userAgent.setModule(module);
		fields.add(userAgent);
		
		FacilioField sessionInfo = new FacilioField();
		sessionInfo.setName("sessionInfo");
		sessionInfo.setDataType(FieldType.STRING);
		sessionInfo.setColumnName("SESSION_INFO");
		sessionInfo.setModule(module);
		fields.add(sessionInfo);
		
		FacilioField userType = new FacilioField();
		userType.setName("userType");
		userType.setDataType(FieldType.STRING);
		userType.setColumnName("USER_TYPE");
		userType.setModule(module);
		fields.add(userType);
	
		return fields;
	}
	
	public static FacilioField getOrgUserDeletedTimeField() {
		FacilioField deletedTime = new FacilioField();
		deletedTime.setName("deletedTime");
		deletedTime.setDataType(FieldType.NUMBER);
		deletedTime.setColumnName("DELETED_TIME");
		deletedTime.setModule(getOrgUserModule());
		return deletedTime;
	}
	
	public static List<FacilioField> getGroupFields() {
		FacilioModule module = getGroupModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField groupId = new FacilioField();
		groupId.setName("groupId");
		groupId.setDataType(FieldType.ID);
		groupId.setColumnName("GROUPID");
		groupId.setModule(module);
		fields.add(groupId);

		/*FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);*/

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("GROUP_NAME");
		name.setModule(module);
		fields.add(name);

		FacilioField email = new FacilioField();
		email.setName("email");
		email.setDataType(FieldType.STRING);
		email.setColumnName("GROUP_EMAIL");
		email.setModule(module);
		fields.add(email);

		FacilioField description = new FacilioField();
		description.setName("description");
		description.setDataType(FieldType.STRING);
		description.setColumnName("GROUP_DESC");
		description.setModule(module);
		fields.add(description);

		FacilioField isActive = new FacilioField();
		isActive.setName("isActive");
		isActive.setDataType(FieldType.BOOLEAN);
		isActive.setColumnName("IS_ACTIVE");
		isActive.setModule(module);
		fields.add(isActive);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(module);
		fields.add(createdTime);

		FacilioField createdBy = new FacilioField();
		createdBy.setName("createdBy");
		createdBy.setDataType(FieldType.NUMBER);
		createdBy.setColumnName("CREATED_BY");
		createdBy.setModule(module);
		fields.add(createdBy);

		FacilioField parent = new FacilioField();
		parent.setName("parent");
		parent.setDataType(FieldType.NUMBER);
		parent.setColumnName("PARENT");
		parent.setModule(module);
		fields.add(parent);
		
		FacilioField siteId = FieldFactory.getSiteIdField(module);
		fields.add(siteId);

		return fields;
	}

	public static List<FacilioField> getGroupMemberFields() {
		FacilioModule module = getGroupMemberModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField memberId = new FacilioField();
		memberId.setName("memberId");
		memberId.setDataType(FieldType.ID);
		memberId.setColumnName("MEMBERID");
		memberId.setModule(module);
		fields.add(memberId);

		FacilioField groupId = new FacilioField();
		groupId.setName("groupId");
		groupId.setDataType(FieldType.NUMBER);
		groupId.setColumnName("GROUPID");
		groupId.setModule(module);
		fields.add(groupId);

		FacilioField ouid = new FacilioField();
		ouid.setName("ouid");
		ouid.setDataType(FieldType.NUMBER);
		ouid.setColumnName("ORG_USERID");
		ouid.setModule(module);
		fields.add(ouid);

		FacilioField memberRole = new FacilioField();
		memberRole.setName("memberRole");
		memberRole.setDataType(FieldType.NUMBER);
		memberRole.setColumnName("MEMBER_ROLE");
		memberRole.setModule(module);
		fields.add(memberRole);

		return fields;
	}
	
	public static List<FacilioField> getPermissionFields() {
		FacilioModule module = getPermissionModule();
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField roleId = new FacilioField();
		roleId.setName("roleId");
		roleId.setColumnName("ROLE_ID");
		roleId.setDataType(FieldType.NUMBER);
		roleId.setModule(module);
		fields.add(roleId);
		
		FacilioField moduleName = new FacilioField();
		moduleName.setName("moduleName");
		moduleName.setColumnName("MODULE_NAME");
		moduleName.setDataType(FieldType.STRING);
		moduleName.setModule(module);
		fields.add(moduleName);
		
		FacilioField permission = new FacilioField();
		permission.setName("permission");
		permission.setColumnName("PERMISSION");
		permission.setDataType(FieldType.NUMBER);
		permission.setModule(module);
		fields.add(permission);
		
		return fields;
		
	}
	
	public static List<FacilioField> getAccessbileSpaceFields() {
		FacilioModule module = ModuleFactory.getAccessibleSpaceModule();
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField orgUserId = new FacilioField();
		orgUserId.setName("ouid");
		orgUserId.setColumnName("ORG_USER_ID");
		orgUserId.setDataType(FieldType.NUMBER);
		orgUserId.setModule(module);
		fields.add(orgUserId);
		
		FacilioField bsid = new FacilioField();
		bsid.setName("bsid");
		bsid.setColumnName("BS_ID");
		bsid.setDataType(FieldType.NUMBER);
		bsid.setModule(module);
		fields.add(bsid);
		
		FacilioField siteId = FieldFactory.getSiteIdField(module);
		fields.add(siteId);
		
		return fields;
	}
	
	public static List<FacilioField> getRoleFields() {
		FacilioModule module = getRoleModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField roleId = new FacilioField();
		roleId.setName("roleId");
		roleId.setDataType(FieldType.ID);
		roleId.setColumnName("ROLE_ID");
		roleId.setModule(module);
		fields.add(roleId);

		/*FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);*/
		
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);
		
		FacilioField description = new FacilioField();
		description.setName("description");
		description.setDataType(FieldType.STRING);
		description.setColumnName("DESCRIPTION");
		description.setModule(module);
		fields.add(description);
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(module);
		fields.add(createdTime);

		return fields;
	}
}
