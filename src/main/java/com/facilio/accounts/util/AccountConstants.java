package com.facilio.accounts.util;

import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class AccountConstants {

	public static enum GroupMemberRole {
		ADMIN(1), MANAGER(2), MEMBER(3);

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
			for (GroupMemberRole mrole : values()) {
				roleMap.put(mrole.getMemberRole(), mrole);
			}
			return roleMap;
		}
	}

	public static enum UserType {

		USER(1), REQUESTER(2);

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

			for (UserType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}

		public Map<Integer, UserType> getAllTypes() {
			return typeMap;
		}
	}

	public static enum SessionType {
		USER_LOGIN_SESSION(1), PERMALINK_SESSION(2);

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

			for (SessionType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}

		public Map<Integer, SessionType> getAllTypes() {
			return typeMap;
		}
	}

	public static class DefaultSuperAdmin {
		public static final String SUPER_ADMIN = "Super Administrator";
		public static final String ADMINISTRATOR = "Administrator";
	}

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

		READ(512),

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

		SHARE_DASHBOARD(34359738368L), // for New layout

		VIEW_APPROVAL(68719476736L),

		EXPORT(137438953472L),

		VIEW_ASSIGNMENT(274877906944L),

		VIEW_ASSIGNMENT_DEPARTMENT(549755813888L),

		VIEW_ASSIGNMENT_OWN(1099511627776L),

		VIEW(2199023255552L),
		CONTROL(4398046511104L),

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

		SETUP(Permission.ORG_ACCESS_ADMINISTER, Permission.ORG_ACCESS_DELETE, Permission.USER_ACCESS_ADMINISTER,
				Permission.USER_ACCESS_DELETE, Permission.GROUP_ACCESS_ADMINISTER, Permission.GROUP_ACCESS_DELETE),

		WORKORDER_CREATE(Permission.WORKORDER_ACCESS_CREATE_ACCESSIBLE_SPACES, Permission.WORKORDER_ACCESS_CREATE_ANY),

		WORKORDER_UPDATE(Permission.WORKORDER_ACCESS_UPDATE_ACCESSIBLE_SPACES, Permission.WORKORDER_ACCESS_UPDATE_OWN,
				Permission.WORKORDER_ACCESS_UPDATE_ANY),

		WORKORDER_READ(Permission.WORKORDER_ACCESS_READ_ACCESSIBLE_SPACES, Permission.WORKORDER_ACCESS_READ_OWN,
				Permission.WORKORDER_ACCESS_READ_ANY),

		WORKORDER_DELETE(Permission.WORKORDER_ACCESS_DELETE_ACCESSIBLE_SPACES, Permission.WORKORDER_ACCESS_DELETE_OWN,
				Permission.WORKORDER_ACCESS_DELETE_ANY),

		WORKORDER_ASSIGN(Permission.WORKORDER_ACCESS_ASSIGN_ACCESSIBLE_SPACES, Permission.WORKORDER_ACCESS_ASSIGN_OWN,
				Permission.WORKORDER_ACCESS_ASSIGN_ANY),

		TASK_CREATE(Permission.TASK_ACCESS_CREATE_ANY),

		TASK_UPDATE(Permission.TASK_ACCESS_UPDATE_OWN, Permission.TASK_ACCESS_UPDATE_ANY),

		TASK_READ(Permission.TASK_ACCESS_READ_OWN, Permission.TASK_ACCESS_READ_ANY),

		TASK_DELETE(Permission.TASK_ACCESS_DELETE_OWN, Permission.TASK_ACCESS_DELETE_ANY),

		TASK_ASSIGN(Permission.TASK_ACCESS_ASSIGN_ACCESSIBLE_GRPUPS, Permission.TASK_ACCESS_ASSIGN_OWN,
				Permission.TASK_ACCESS_ASSIGN_ANY);

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

	public static class DefaultRole {
		public static final String SUPER_ADMIN = "Super Administrator";
//		public static final String ADMINISTRATOR 	= "Administrator";
//		public static final String MANAGER 		    = "Manager";
//		public static final String DISPATCHER 		= "Dispatcher";
//		public static final String TECHNICIAN 		= "Technician";

		public static final Map<String, Role> DEFAULT_ROLES = Collections.unmodifiableMap(initRoles());

		private static Map<String, Role> initRoles() {
			Map<String, Role> defaultRoles = new HashMap<>();

			List<Permissions> superAdminPermission = new ArrayList<>();
			superAdminPermission.add(new Permissions("workorder", ModulePermission.getSumOf(ModulePermission.CREATE,
					ModulePermission.DELETE, ModulePermission.READ, ModulePermission.UPDATE)));
			List<Permissions> adminPermission = new ArrayList<>();
			adminPermission.add(new Permissions("workorder", ModulePermission.getSumOf(ModulePermission.CREATE,
					ModulePermission.DELETE, ModulePermission.READ, ModulePermission.UPDATE)));

			Role superAdmin = new Role();
			superAdmin.setName(SUPER_ADMIN);
			superAdmin.setDescription(SUPER_ADMIN);
			superAdmin.setIsPrevileged(true);
			superAdmin.setIsSuperAdmin(true);

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

//	public static FacilioModule getAppUserModule() {
//		FacilioModule userModule = new FacilioModule();
//		userModule.setName("app_user");
//		userModule.setDisplayName("App Users");
//		userModule.setTableName("Users");
//
//		return userModule;
//	}


	public static FacilioModule getAppOrgUserModule() {
		FacilioModule orgModule = new FacilioModule();
		orgModule.setName("users");
		orgModule.setDisplayName("Users");
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
		if(FacilioProperties.isPreProd()) {
			return getPreProdFeatureLicenseModule();
		}
		return license;
	}

	public static FacilioModule getPreProdFeatureLicenseModule() {
		FacilioModule license = new FacilioModule();
		license.setName("preprodfeaturelicense");
		license.setDisplayName("Pre Production Feature License");
		license.setTableName("PreProdFeatureLicense");

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
		groupModule.setName("groups");
		groupModule.setDisplayName("People Group");
		groupModule.setTableName("FacilioGroups");

		return groupModule;
	}

	public static FacilioModule getGroupMemberModule() {
		FacilioModule groupMemberModule = new FacilioModule();
		groupMemberModule.setName("groupmember");
		groupMemberModule.setDisplayName("People Group Members");
		groupMemberModule.setTableName("FacilioGroupMembers");

		return groupMemberModule;
	}

	public static FacilioModule getRoleModule() {
		FacilioModule roleModule = new FacilioModule();
		roleModule.setName("role");
		roleModule.setDisplayName("Roles");
		roleModule.setTableName("Role");
		roleModule.setType(FacilioModule.ModuleType.PICK_LIST);

		return roleModule;
	}

	public static FacilioModule getPermissionModule() {
		FacilioModule permissionModule = new FacilioModule();
		permissionModule.setName("permission");
		permissionModule.setDisplayName("Permission");
		permissionModule.setTableName("Permission");

		return permissionModule;
	}

	public static List<FacilioField> getOrgInfoFields() {
		FacilioModule module = getOrgInfoModule();
		List<FacilioField> fields = new ArrayList<>();

		/* fields.add(FieldFactory.getOrgIdField(module)); */

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

	public static FacilioField getUserIdField(FacilioModule module) {
		FacilioField uid = new FacilioField();
		uid.setName("uid");
		uid.setDataType(FieldType.NUMBER);
		uid.setColumnName("USERID");
		uid.setModule(module);
		return uid;
	}

//	public static FacilioField getUserPasswordField() {
//		FacilioField password = new FacilioField();
//		password.setName("password");
//		password.setDataType(FieldType.STRING);
//		password.setColumnName("PASSWORD");
//		password.setModule(getAppUserModule());
//		return password;
//	}

//	public static List<FacilioField> getAppUserFields() {
//		FacilioModule module = getAppUserModule();
//		List<FacilioField> fields = new ArrayList<>();
//
//		FacilioField uid = new FacilioField();
//		uid.setName("uid");
//		uid.setDataType(FieldType.NUMBER);
//		uid.setColumnName("USERID");
//		uid.setModule(module);
//		fields.add(uid);
//
//		FacilioField name = new FacilioField();
//		name.setName("name");
//		name.setDataType(FieldType.STRING);
//		name.setColumnName("NAME");
//		name.setModule(module);
//		fields.add(name);
//
//		FacilioField domainName = new FacilioField();
//		domainName.setName("domainName");
//		domainName.setDataType(FieldType.STRING);
//		domainName.setColumnName("DOMAIN_NAME");
//		domainName.setModule(module);
//		fields.add(domainName);
//
////		FacilioField city = new FacilioField();
////		city.setName("city");
////		city.setDataType(FieldType.STRING);
////		city.setColumnName("CITY");
////		city.setModule(module);
////		fields.add(city);
//
//		FacilioField userVerified = new FacilioField();
//		userVerified.setName("userVerified");
//		userVerified.setDataType(FieldType.BOOLEAN);
//		userVerified.setColumnName("USER_VERIFIED");
//		userVerified.setModule(module);
//		fields.add(userVerified);
//
//		FacilioField email = new FacilioField();
//		email.setName("email");
//		email.setDataType(FieldType.STRING);
//		email.setColumnName("EMAIL");
//		email.setModule(module);
//		fields.add(email);
//
//		FacilioField photoId = new FacilioField();
//		photoId.setName("photoId");
//		photoId.setDataType(FieldType.NUMBER);
//		photoId.setColumnName("PHOTO_ID");
//		photoId.setModule(module);
//		fields.add(photoId);
//
//		FacilioField timezone = new FacilioField();
//		timezone.setName("timezone");
//		timezone.setDataType(FieldType.STRING);
//		timezone.setColumnName("TIMEZONE");
//		timezone.setModule(module);
//		fields.add(timezone);
//
//		FacilioField language = new FacilioField();
//		language.setName("language");
//		language.setDataType(FieldType.STRING);
//		language.setColumnName("LANGUAGE");
//		language.setModule(module);
//		fields.add(language);
//
//		FacilioField phone = new FacilioField();
//		phone.setName("phone");
//		phone.setDataType(FieldType.STRING);
//		phone.setColumnName("PHONE");
//		phone.setModule(module);
//		fields.add(phone);
//
//		FacilioField mobile = new FacilioField();
//		mobile.setName("mobile");
//		mobile.setDataType(FieldType.STRING);
//		mobile.setColumnName("MOBILE");
//		mobile.setModule(module);
//		fields.add(mobile);
//
//		return fields;
//	}

	public static FacilioModule getPortalInfoModule() {
		FacilioModule portalInfoModule = new FacilioModule();
		portalInfoModule.setName("portalinfo");
		portalInfoModule.setDisplayName("PortalInfo");
		portalInfoModule.setTableName("PortalInfo");
		return portalInfoModule;
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

	
	public static List<FacilioField> getAppOrgUserFields() {
		FacilioModule module = getAppOrgUserModule();
		module.setExtendModule(ModuleFactory.getResourceModule());
		List<FacilioField> fields = new ArrayList<>();

		FacilioField ouid = new FacilioField();
		ouid.setName("ouid");
		ouid.setDisplayName("ID");
		ouid.setDataType(FieldType.NUMBER);
		ouid.setColumnName("ORG_USERID");
		ouid.setModule(module);
		fields.add(ouid);

		fields.add(getUserIdField(module));

		FacilioField license = new FacilioField();
		license.setName("license");
		license.setDataType(FieldType.NUMBER);
		license.setColumnName("LICENSE");
		license.setModule(module);
		fields.add(license);

		FacilioField invitedTime = new FacilioField();
		invitedTime.setName("invitedTime");
		invitedTime.setDataType(FieldType.NUMBER);
		invitedTime.setColumnName("INVITEDTIME");
		invitedTime.setModule(module);
		fields.add(invitedTime);

		FacilioField inviteAcceptStatus = new FacilioField();
		inviteAcceptStatus.setName("inviteAcceptStatus");
		inviteAcceptStatus.setDataType(FieldType.BOOLEAN);
		inviteAcceptStatus.setColumnName("INVITATION_ACCEPT_STATUS");
		inviteAcceptStatus.setModule(module);
		fields.add(inviteAcceptStatus);

//		FacilioField roleId = new FacilioField();
//		roleId.setName("roleId");
//		roleId.setDataType(FieldType.NUMBER);
//		roleId.setColumnName("ROLE_ID");
//		roleId.setModule(module);
//		fields.add(roleId);

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
		
		FacilioField peopleId = new FacilioField();
		peopleId.setName("peopleId");
		peopleId.setDataType(FieldType.NUMBER);
		peopleId.setColumnName("PEOPLE_ID");
		peopleId.setModule(module);
		fields.add(peopleId);
		
		FacilioField iamOrgUserId = new FacilioField();
		iamOrgUserId.setName("iamOrgUserId");
		iamOrgUserId.setDataType(FieldType.NUMBER);
		iamOrgUserId.setColumnName("IAM_ORG_USERID");
		iamOrgUserId.setModule(module);
		fields.add(iamOrgUserId);
	
		FacilioField signatureFileId = new FacilioField();
		signatureFileId.setName("signatureFileId");
		signatureFileId.setDataType(FieldType.NUMBER);
		signatureFileId.setColumnName("SIGNATURE_FILE_ID");
		signatureFileId.setModule(module);
        fields.add(signatureFileId);
		
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

		/*
		 * FacilioField orgId = new FacilioField(); orgId.setName("orgId");
		 * orgId.setDataType(FieldType.NUMBER); orgId.setColumnName("ORGID");
		 * orgId.setModule(module); fields.add(orgId);
		 */

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

		/*
		 * FacilioField orgId = new FacilioField(); orgId.setName("orgId");
		 * orgId.setDataType(FieldType.NUMBER); orgId.setColumnName("ORGID");
		 * orgId.setModule(module); fields.add(orgId);
		 */

		FacilioField license = new FacilioField();
		license.setName(FacilioConstants.LicenseKeys.LICENSE1);
		license.setDataType(FieldType.NUMBER);
		license.setColumnName("MODULE");
		license.setModule(module);
		fields.add(license);
		
		FacilioField license2 = new FacilioField();
		license2.setName(FacilioConstants.LicenseKeys.LICENSE2);
		license2.setDataType(FieldType.NUMBER);
		license2.setColumnName("MODULE2");
		license2.setModule(module);
		fields.add(license2);

		FacilioField license3 = new FacilioField();
		license3.setName(FacilioConstants.LicenseKeys.LICENSE3);
		license3.setDataType(FieldType.NUMBER);
		license3.setColumnName("MODULE3");
		license3.setModule(module);
		fields.add(license3);

		if(FacilioProperties.isPreProd()) {
			return getPreProdFeatureLicenseFields();
		}
		return fields;
	}

	public static List<FacilioField> getPreProdFeatureLicenseFields() {
		FacilioModule module = getPreProdFeatureLicenseModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField license = new FacilioField();
		license.setName(FacilioConstants.LicenseKeys.LICENSE1);
		license.setDataType(FieldType.NUMBER);
		license.setColumnName("MODULE");
		license.setModule(module);
		fields.add(license);

		FacilioField license2 = new FacilioField();
		license2.setName(FacilioConstants.LicenseKeys.LICENSE2);
		license2.setDataType(FieldType.NUMBER);
		license2.setColumnName("MODULE2");
		license2.setModule(module);
		fields.add(license2);

		FacilioField license3 = new FacilioField();
		license3.setName(FacilioConstants.LicenseKeys.LICENSE3);
		license3.setDataType(FieldType.NUMBER);
		license3.setColumnName("MODULE3");
		license3.setModule(module);
		fields.add(license3);

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

		/*
		 * FacilioField orgId = new FacilioField(); orgId.setName("orgId");
		 * orgId.setDataType(FieldType.NUMBER); orgId.setColumnName("ORGID");
		 * orgId.setModule(module); fields.add(orgId);
		 */

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
		deletedTime.setModule(getAppOrgUserModule());
		return deletedTime;
	}

	public static List<FacilioField> getGroupFields() {
		FacilioModule module = getGroupModule();
		List<FacilioField> fields = new ArrayList<>();

		fields.add(FieldFactory.getIdField(module));
		fields.add(FieldFactory.getModuleIdField(module));

		FacilioField groupId = new FacilioField();
		groupId.setName("groupId");
		groupId.setDataType(FieldType.NUMBER);
		groupId.setColumnName("GROUPID");
		groupId.setModule(module);
		fields.add(groupId);

		/*
		 * FacilioField orgId = new FacilioField(); orgId.setName("orgId");
		 * orgId.setDataType(FieldType.NUMBER); orgId.setColumnName("ORGID");
		 * orgId.setModule(module); fields.add(orgId);
		 */

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

		fields.add(FieldFactory.getIdField(module));
		fields.add(FieldFactory.getModuleIdField(module));

		FacilioField memberId = new FacilioField();
		memberId.setName("memberId");
		memberId.setDataType(FieldType.NUMBER);
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

		FacilioField people = new FacilioField();
		people.setName("people");
		people.setDataType(FieldType.LOOKUP);
		people.setColumnName("PEOPLE_ID");
		people.setModule(module);
		fields.add(people);

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
		
		fields.add(FieldFactory.getField("moduleId", "MODULE_ID", FieldType.LOOKUP));
		
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

		FacilioField peopleId = new FacilioField();
		peopleId.setName("peopleId");
		peopleId.setColumnName("PEOPLE_ID");
		peopleId.setDataType(FieldType.NUMBER);
		peopleId.setModule(module);
		fields.add(peopleId);

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

	public static FacilioField getOrgIdField() {
		return getOrgIdField(null);
	}

	public static FacilioField getOrgIdField(FacilioModule module) {
		FacilioField field = new FacilioField();
		field.setName("orgId");
		field.setDisplayName("Org Id");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("ORGID");
		if (module != null) {
			field.setModule(module);
		}
		return field;
	}
	
	public static FacilioField getSiteIdField(FacilioModule module) {
		FacilioField field = new FacilioField();
		field.setName("siteId");
		field.setDisplayName("Site Id");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("SITE_ID");
		if (module != null) {
			field.setModule(module);
		}
		return field;
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

		/*
		 * FacilioField orgId = new FacilioField(); orgId.setName("orgId");
		 * orgId.setDataType(FieldType.NUMBER); orgId.setColumnName("ORGID");
		 * orgId.setModule(module); fields.add(orgId);
		 */

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

		FacilioField previlegedRole = new FacilioField();
		previlegedRole.setName("isPrevileged");
		previlegedRole.setDataType(FieldType.BOOLEAN);
		previlegedRole.setColumnName("IS_PREVILEGED_ROLE");
		previlegedRole.setModule(module);
		fields.add(previlegedRole);

		FacilioField isSuperAdminRole = new FacilioField();
		isSuperAdminRole.setName("isSuperAdmin");
		isSuperAdminRole.setDataType(FieldType.BOOLEAN);
		isSuperAdminRole.setColumnName("IS_SUPER_ADMIN");
		isSuperAdminRole.setModule(module);
		fields.add(isSuperAdminRole);

		return fields;
	}
	
	public static FacilioModule getOrgUserAppsModule() {
		FacilioModule orgUserAppModule = new FacilioModule();
		orgUserAppModule.setName("orgUserApps");
		orgUserAppModule.setDisplayName("ORG User Apps");
		orgUserAppModule.setTableName("ORG_User_Apps");

		return orgUserAppModule;
	}

	public static FacilioModule getRolesAppsModule() {
		FacilioModule rolesAppsModule = new FacilioModule();
		rolesAppsModule.setName("roleApp");
		rolesAppsModule.setDisplayName("Roles Vs Apps");
		rolesAppsModule.setTableName("Roles_Apps");

		return rolesAppsModule;
	}
	
	public static FacilioField getApplicationIdField() {
		FacilioModule module = getOrgUserAppsModule();
		
		FacilioField domain = new FacilioField();
		domain.setName("applicationId");
		domain.setDataType(FieldType.NUMBER);
		domain.setColumnName("APPLICATION_ID");
		domain.setModule(module);
		
		return domain;
	}

	public static FacilioField getRoleIdField() {
		FacilioModule module = getOrgUserAppsModule();

		FacilioField roleId = new FacilioField();
		roleId.setName("roleId");
		roleId.setDataType(FieldType.NUMBER);
		roleId.setColumnName("ROLE_ID");
		roleId.setModule(module);

		return roleId;
	}
	
	public static List<FacilioField> getOrgUserAppsFields() {
		FacilioModule module = getOrgUserAppsModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField id = new FacilioField();
		id.setName("id");
		id.setDataType(FieldType.ID);
		id.setColumnName("ID");
		id.setModule(module);
		fields.add(id);

		FacilioField ouId = new FacilioField();
		ouId.setName("ouid");
		ouId.setDataType(FieldType.NUMBER);
		ouId.setColumnName("ORG_USERID");
		ouId.setModule(module);
		fields.add(ouId);
		
		FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);
		
		FacilioField applicationId = new FacilioField();
		applicationId.setName("applicationId");
		applicationId.setDataType(FieldType.NUMBER);
		applicationId.setColumnName("APPLICATION_ID");
		applicationId.setModule(module);
		fields.add(applicationId);

		FacilioField roleId = new FacilioField();
		roleId.setName("roleId");
		roleId.setDataType(FieldType.NUMBER);
		roleId.setColumnName("ROLE_ID");
		roleId.setModule(module);
		fields.add(roleId);

		FacilioField scopingId = new FacilioField();
		scopingId.setName("scopingId");
		scopingId.setDataType(FieldType.NUMBER);
		scopingId.setColumnName("SCOPING_ID");
		scopingId.setModule(module);
		fields.add(scopingId);

		FacilioField isDefaultApp = new FacilioField();
		isDefaultApp.setName("isDefaultApp");
		isDefaultApp.setDataType(FieldType.BOOLEAN);
		isDefaultApp.setColumnName("IS_DEFAULT_APP");
		isDefaultApp.setModule(module);
		fields.add(isDefaultApp);

		return fields;
	}


	public static List<FacilioField> getRolesAppsFields() {
		FacilioModule module = getRolesAppsModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField id = new FacilioField();
		id.setName("id");
		id.setDataType(FieldType.ID);
		id.setColumnName("ID");
		id.setModule(module);
		fields.add(id);

		FacilioField applicationId = new FacilioField();
		applicationId.setName("applicationId");
		applicationId.setDataType(FieldType.NUMBER);
		applicationId.setColumnName("APPLICATION_ID");
		applicationId.setModule(module);
		fields.add(applicationId);

		FacilioField roleId = new FacilioField();
		roleId.setName("roleId");
		roleId.setDataType(FieldType.NUMBER);
		roleId.setColumnName("ROLE_ID");
		roleId.setModule(module);
		fields.add(roleId);



		return fields;
	}

	public static FacilioField getFailureCodeField() {
		FacilioField failure_code_id = new FacilioField();
		failure_code_id.setName("code_id");
		failure_code_id.setColumnName("ID");
		failure_code_id.setDataType(FieldType.NUMBER);
		failure_code_id.setModule(ModuleFactory.getFailureCodeModule());
		return failure_code_id;
	}

	public static FacilioField getFailureCodeProblemField() {
		FacilioField failure_problem_id = new FacilioField();
		failure_problem_id.setName("id");
		failure_problem_id.setColumnName("ID");
		failure_problem_id.setDataType(FieldType.NUMBER);
		failure_problem_id.setModule(ModuleFactory.getFailureCodeProblemModule());
		return failure_problem_id;
	}

	public static FacilioField getFailureCodeCausesField() {
		FacilioField failure_cause_id = new FacilioField();
		failure_cause_id.setName("id");
		failure_cause_id.setColumnName("ID");
		failure_cause_id.setDataType(FieldType.NUMBER);
		failure_cause_id.setModule(ModuleFactory.getFailureCodeCausesModule());
		return failure_cause_id;
	}

	public static FacilioField getFailureCodeRemediesField() {
		FacilioField failure_remedies_id = new FacilioField();
		failure_remedies_id.setName("id");
		failure_remedies_id.setColumnName("ID");
		failure_remedies_id.setDataType(FieldType.NUMBER);
		failure_remedies_id.setModule(ModuleFactory.getFailureCodeRemediesModule());
		return failure_remedies_id;
	}

	public static FacilioField getWorkOrderFailureClassRelationField() {
		FacilioField woFailureClass = new FacilioField();
		woFailureClass.setName("id");
		woFailureClass.setColumnName("ID");
		woFailureClass.setDataType(FieldType.NUMBER);
		woFailureClass.setModule(ModuleFactory.getWorkOrderFailureClassRelModule());
		return woFailureClass;
	}

	public static List<FacilioField> getFailureCodeProblemFields() {
		FacilioModule module = ModuleFactory.getFailureCodeProblemModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getFailureCodeProblemField());

		FacilioField code_id = new FacilioField();
		code_id.setName("code_id");
		code_id.setColumnName("FAILURE_CODE_ID");
		code_id.setDataType(FieldType.NUMBER);
		code_id.setModule(module);
		fields.add(code_id);

		FacilioField class_id = new FacilioField();
		class_id.setName("class_id");
		class_id.setColumnName("FAILURE_CLASS_ID");
		class_id.setDataType(FieldType.NUMBER);
		class_id.setModule(module);
		fields.add(class_id);

		return fields;
	}

	public static List<FacilioField> getFailureCodeCausesFields() {
		FacilioModule module = ModuleFactory.getFailureCodeCausesModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getFailureCodeCausesField());

		FacilioField code_id = new FacilioField();
		code_id.setName("code_id");
		code_id.setColumnName("FAILURE_CODE_ID");
		code_id.setDataType(FieldType.NUMBER);
		code_id.setModule(module);
		fields.add(code_id);

		FacilioField problem_id = new FacilioField();
		problem_id.setName("problem_id");
		problem_id.setColumnName("FAILURE_CODE_PROBLEMS_ID");
		problem_id.setDataType(FieldType.NUMBER);
		problem_id.setModule(module);
		fields.add(problem_id);

		return fields;
	}

	public static List<FacilioField> getFailureCodeRemediesFields() {
		FacilioModule module = ModuleFactory.getFailureCodeRemediesModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getFailureCodeRemediesField());

		FacilioField code_id = new FacilioField();
		code_id.setName("code_id");
		code_id.setColumnName("FAILURE_CODE_ID");
		code_id.setDataType(FieldType.NUMBER);
		code_id.setModule(module);
		fields.add(code_id);

		FacilioField causes_id = new FacilioField();
		causes_id.setName("causes_id");
		causes_id.setColumnName("FAILURE_CODE_CAUSES_ID");
		causes_id.setDataType(FieldType.NUMBER);
		causes_id.setModule(module);
		fields.add(causes_id);

		return fields;
	}


	public static List<FacilioField> getLicensingInfoFields() {
		FacilioModule module = ModuleFactory.getLicensingInfoModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField id = new FacilioField();
		id.setName("id");
		id.setColumnName("ID");
		id.setDataType(FieldType.NUMBER);
		id.setModule(module);
		fields.add(id);

		FacilioField licenseType = new FacilioField();
		licenseType.setName("licensingType");
		licenseType.setColumnName("LICENSING_TYPE");
		licenseType.setDataType(FieldType.NUMBER);
		licenseType.setModule(module);
		fields.add(licenseType);

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setColumnName("NAME");
		name.setDataType(FieldType.STRING);
		name.setModule(module);
		fields.add(name);

		FacilioField allowedLicense = new FacilioField();
		allowedLicense.setName("allowedLicense");
		allowedLicense.setColumnName("LICENSE_ALLOWED");
		allowedLicense.setDataType(FieldType.NUMBER);
		allowedLicense.setModule(module);
		fields.add(allowedLicense);

		FacilioField usedLicense = new FacilioField();
		usedLicense.setName("usedLicense");
		usedLicense.setColumnName("LICENSE_USED");
		usedLicense.setDataType(FieldType.NUMBER);
		usedLicense.setModule(module);
		fields.add(usedLicense);

		FacilioField aggregatingPeriod = new FacilioField();
		aggregatingPeriod.setName("aggregatingPeriod");
		aggregatingPeriod.setColumnName("AGGREGATING_PERIOD");
		aggregatingPeriod.setDataType(FieldType.NUMBER);
		aggregatingPeriod.setModule(module);
		fields.add(aggregatingPeriod);

		FacilioField validFrom = new FacilioField();
		validFrom.setName("validFrom");
		validFrom.setColumnName("VALID_FROM");
		validFrom.setDataType(FieldType.NUMBER);
		validFrom.setModule(module);
		fields.add(validFrom);

		FacilioField validTill = new FacilioField();
		validTill.setName("validTill");
		validTill.setColumnName("VALID_TILL");
		validTill.setDataType(FieldType.NUMBER);
		validTill.setModule(module);
		fields.add(validTill);

		return fields;
	}

}
