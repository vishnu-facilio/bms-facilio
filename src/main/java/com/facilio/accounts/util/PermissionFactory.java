package com.facilio.accounts.util;

public class PermissionFactory {
	
	public static enum WorkOrder_Permission implements PermissionInterface {
		
		READ (1,""),
		READ_TEAM(2,""),
		READ_OWN(4,""),
		READ_IN_ACCESSIBLE_SPACES(8,""),
		
		CREATE(16,""),
		CREATE_IN_ACCESSIBLE_SPACES(32,""),
		
		UPDATE(64,""),
		UPDATE_TEAM(128,""),
		UPDATE_OWN(256,""),
		UPDATE_IN_ACCESSIBLE_SPACES(512,""),
		
		DELETE(1024,""),
		DELETE_TEAM(2048,""),
		DELETE_OWN(4096,""),
		DELETE_IN_ACCESSIBLE_SPACES(8192,""),
		
		ASSIGN(16384,""),
		ASSIGN_TEAM(32768,""),
		ASSIGN_OWN(65536,""),
		ASSIGN_IN_ACCESSIBLE_SPACES(131072,""),
		
		//check and remove list
		
		CHANGE_OWNERSHIP(524288,""),
		CLOSE_WORK_ORDER(1048576,""),
		ADD_UPDATE_DELETE_TASK(2097152,""),


		TASK_ACCESS_CREATE_ANY(4194304,""),

		TASK_ACCESS_UPDATE_OWN(8388608,""),
		TASK_ACCESS_UPDATE_ANY(16777216,""),

		TASK_ACCESS_READ_OWN(33554432,""),
		TASK_ACCESS_READ_ANY(67108864,""),

		TASK_ACCESS_DELETE_OWN(134217728,""),
		TASK_ACCESS_DELETE_ANY(268435456,""),

		TASK_ACCESS_ASSIGN_OWN(536870912,""),
		TASK_ACCESS_ASSIGN_ACCESSIBLE_GRPUPS(1073741824,""),
		TASK_ACCESS_ASSIGN_ANY(2147483648L,""),
		TASK_ACCESS_CAN_BE_ASSIGNED_ANY(4294967296L,""),
		
		;

		long permission;
		String permissionName;
		String moduleName="workorder";

		WorkOrder_Permission(long permission,String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		public long getPermission() {
			return this.permission;
		}
		public String getPermissionName() {
			return this.permissionName;
		}
		public String getModuleName() {
			return this.moduleName;
		}
	}
	
	public static enum Dashboard_Permission implements PermissionInterface {
		
		READ (1,""),
		CREATE(2,""),
		UPDATE(4,""),
		DELETE(8,""),
		SHARE(16,""),
		;

		long permission;
		String permissionName;
		String moduleName="dashboard";

		Dashboard_Permission(long permission,String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		public long getPermission() {
			return this.permission;
		}
		public String getPermissionName() {
			return this.permissionName;
		}
		public String getModuleName() {
			return this.moduleName;
		}
	}
	
	public static enum PM_Permission implements PermissionInterface {
		
		READ (1,""),
		CREATE(2,""),
		UPDATE(4,""),
		DELETE(8,""),
		CALENDER(16,""),
		PLANNER(32,""),
		;

		long permission;
		String permissionName;
		String moduleName="planned";

		PM_Permission(long permission,String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		public long getPermission() {
			return this.permission;
		}
		public String getPermissionName() {
			return this.permissionName;
		}
		public String getModuleName() {
			return this.moduleName;
		}
	}
	
	
	public static enum Alarm_Permission implements PermissionInterface {
		
		READ (1,""),
		DELETE(2,""),
		GENDRAL(4,""),
		ACKNOWLEDGE(8,""),
		CLEAR(16,""),
		;

		long permission;
		String permissionName;
		String moduleName="alarm";

		Alarm_Permission(long permission,String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		public long getPermission() {
			return this.permission;
		}
		public String getPermissionName() {
			return this.permissionName;
		}
		public String getModuleName() {
			return this.moduleName;
		}
	}
	
	public static enum Asset_Permission implements PermissionInterface {
		
		READ (1,""),
		CREATE(2,""),
		UPDATE(4,""),
		DELETE(8,""),
		IMPORT(16,""),
		ADD_READING(32,""),
		;

		long permission;
		String permissionName;
		String moduleName="asset";

		Asset_Permission(long permission,String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		public long getPermission() {
			return this.permission;
		}
		public String getPermissionName() {
			return this.permissionName;
		}
		public String getModuleName() {
			return this.moduleName;
		}
	}
	
}
