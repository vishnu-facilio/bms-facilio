package com.facilio.accounts.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Role;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;

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

	public static class DefaultRole 
	{
		public static final String SUPER_ADMIN 	= "Super Administrator";
		public static final String ADMINISTRATOR 	= "Administrator";
		public static final String MANAGER 		    = "Manager";
		public static final String DISPATCHER 		= "Dispatcher";
		public static final String TECHNICIAN 		= "Technician";

		public static final Map<String, Role> DEFAULT_ROLES = Collections.unmodifiableMap(initRoles());

		private static Map<String, Role> initRoles()
		{
			Map<String, Role> defaultRoles = new HashMap<>();

			Role superAdmin = new Role();
			superAdmin.setName(SUPER_ADMIN);
			superAdmin.setDescription(SUPER_ADMIN);
			superAdmin.setPermissions(0L);
			
			Role administrator = new Role();
			administrator.setName(ADMINISTRATOR);
			administrator.setDescription(ADMINISTRATOR);
			administrator.setPermissions(0L);
			
			Role manager = new Role();
			manager.setName(MANAGER);
			manager.setDescription(MANAGER);
			manager.setPermissions(Permission.getSumOf(
					Permission.USER_ACCESS_ADMINISTER,
					Permission.GROUP_ACCESS_ADMINISTER,
					Permission.WORKORDER_ACCESS_CREATE_ANY,
					Permission.WORKORDER_ACCESS_UPDATE_ANY, 
					Permission.WORKORDER_ACCESS_READ_ANY,
					Permission.WORKORDER_ACCESS_DELETE_ANY,
					Permission.WORKORDER_ACCESS_ASSIGN_ANY,
					Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY,
					Permission.TASK_ACCESS_CREATE_ANY,
					Permission.TASK_ACCESS_UPDATE_ANY,
					Permission.TASK_ACCESS_READ_ANY,
					Permission.TASK_ACCESS_DELETE_ANY,
					Permission.TASK_ACCESS_ASSIGN_ANY,
					Permission.TASK_ACCESS_CAN_BE_ASSIGNED_ANY,
					Permission.DASHBOARD_ACCESS_ENABLE,
					Permission.REPORTS_ACCESS_ENABLE
					));
			
			Role dispatcher = new Role();
			dispatcher.setName(DISPATCHER);
			dispatcher.setDescription(DISPATCHER);
			dispatcher.setPermissions(Permission.getSumOf(
					Permission.WORKORDER_ACCESS_CREATE_ANY,
					Permission.WORKORDER_ACCESS_UPDATE_ANY, 
					Permission.WORKORDER_ACCESS_READ_ANY,
					Permission.WORKORDER_ACCESS_DELETE_ANY,
					Permission.WORKORDER_ACCESS_ASSIGN_ANY,
					Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY,
					Permission.TASK_ACCESS_CREATE_ANY,
					Permission.TASK_ACCESS_UPDATE_ANY,
					Permission.TASK_ACCESS_READ_ANY,
					Permission.TASK_ACCESS_DELETE_ANY,
					Permission.TASK_ACCESS_ASSIGN_ANY,
					Permission.TASK_ACCESS_CAN_BE_ASSIGNED_ANY
					));
			
			Role technician = new Role();
			technician.setName(TECHNICIAN);
			technician.setDescription(TECHNICIAN);
			technician.setPermissions(Permission.getSumOf(
					Permission.WORKORDER_ACCESS_UPDATE_OWN, 
					Permission.WORKORDER_ACCESS_READ_OWN,
					Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY,
					Permission.TASK_ACCESS_UPDATE_OWN,
					Permission.TASK_ACCESS_READ_OWN,
					Permission.TASK_ACCESS_CAN_BE_ASSIGNED_ANY
					));
			
			defaultRoles.put(SUPER_ADMIN, superAdmin);
			defaultRoles.put(ADMINISTRATOR, administrator);
			defaultRoles.put(MANAGER, manager);
			defaultRoles.put(DISPATCHER, dispatcher);
			defaultRoles.put(TECHNICIAN, technician);
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

	public static List<FacilioField> getOrgFields() {
		FacilioModule module = getOrgModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
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

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(module);
		fields.add(createdTime);

		return fields;
	}

	public static List<FacilioField> getUserFields() {
		FacilioModule module = getUserModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField uid = new FacilioField();
		uid.setName("uid");
		uid.setDataType(FieldType.NUMBER);
		uid.setColumnName("USERID");
		uid.setModule(module);
		fields.add(uid);

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);

		FacilioField cognitoId = new FacilioField();
		cognitoId.setName("cognitoId");
		cognitoId.setDataType(FieldType.STRING);
		cognitoId.setColumnName("COGNITO_ID");
		cognitoId.setModule(module);
		fields.add(cognitoId);

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
		userMobileSettingId.setDataType(FieldType.NUMBER);
		userMobileSettingId.setColumnName("USER_MOBILE_SETTING_ID");
		userMobileSettingId.setModule(module);
		fields.add(userMobileSettingId);

		FacilioField mobileInstanceId = new FacilioField();
		mobileInstanceId.setName("mobileInstanceId");
		mobileInstanceId.setDataType(FieldType.STRING);
		mobileInstanceId.setColumnName("MOBILE_INSTANCE_ID");
		mobileInstanceId.setModule(module);
		fields.add(mobileInstanceId);

		return fields;
	}

	public static List<FacilioField> getOrgUserFields() {
		FacilioModule module = getOrgUserModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField ouid = new FacilioField();
		ouid.setName("ouid");
		ouid.setDataType(FieldType.NUMBER);
		ouid.setColumnName("ORG_USERID");
		ouid.setModule(module);
		fields.add(ouid);

		FacilioField uid = new FacilioField();
		uid.setName("uid");
		uid.setDataType(FieldType.NUMBER);
		uid.setColumnName("USERID");
		uid.setModule(module);
		fields.add(uid);

		FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);

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

		return fields;
	}

	public static List<FacilioField> getGroupFields() {
		FacilioModule module = getGroupModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField groupId = new FacilioField();
		groupId.setName("groupId");
		groupId.setDataType(FieldType.NUMBER);
		groupId.setColumnName("GROUPID");
		groupId.setModule(module);
		fields.add(groupId);

		FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);

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

		return fields;
	}

	public static List<FacilioField> getGroupMemberFields() {
		FacilioModule module = getGroupMemberModule();
		List<FacilioField> fields = new ArrayList<>();

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

		FacilioField memberRole = new FacilioField();
		memberRole.setName("memberRole");
		memberRole.setDataType(FieldType.NUMBER);
		memberRole.setColumnName("MEMBER_ROLE");
		memberRole.setModule(module);
		fields.add(memberRole);

		return fields;
	}
	
	public static List<FacilioField> getRoleFields() {
		FacilioModule module = getRoleModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField roleId = new FacilioField();
		roleId.setName("roleId");
		roleId.setDataType(FieldType.NUMBER);
		roleId.setColumnName("ROLE_ID");
		roleId.setModule(module);
		fields.add(roleId);

		FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(module);
		fields.add(orgId);
		
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

		FacilioField permissions = new FacilioField();
		permissions.setName("permissionStr");
		permissions.setDataType(FieldType.STRING);
		permissions.setColumnName("PERMISSIONS");
		permissions.setModule(module);
		fields.add(permissions);

		return fields;
	}
}
