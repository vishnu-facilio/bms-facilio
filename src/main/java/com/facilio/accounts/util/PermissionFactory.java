package com.facilio.accounts.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.mv.context.MVProjectContext.EMC_Options;

public class PermissionFactory {
	
	public static enum WorkOrder_Permission implements PermissionInterface {

		CREATE(1,"Create"),
		UPDATE_OWN(2,"Update Own"),
		UPDATE_ANY(4,"Update Any"),
		READ_OWN(8,"Read Own"),
		READ_ANY(16,"Read Any"),
		DELETE_OWN(32,"Delete Own"),
		DELETE_ANY(64,"Delete Any"),
		ASSIGN_OWN(128,"Assign Own"),
		ASSIGN_ANY(256,"Assign Any"),
		WORKORDER_ACCESS_CREATE_ACCESSIBLE_SPACES(512,"WORKORDER_ACCESS_CREATE_ACCESSIBLE_SPACES"),
		
		// more to add
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
		
		public static final Map<Long, WorkOrder_Permission> permissionValueMap = Collections.unmodifiableMap(toValueMap());
		public static final Map<String, WorkOrder_Permission> permissionNameMap = Collections.unmodifiableMap(toNameMap());

		private static Map<String, WorkOrder_Permission> toNameMap() {
			HashMap<String, WorkOrder_Permission> permissionMap = new HashMap<>();
			for (WorkOrder_Permission permission : WorkOrder_Permission.values()) {
				permissionMap.put(permission.getPermissionName(), permission);
			}
			return permissionMap;
		}
		
		private static Map<Long, WorkOrder_Permission> toValueMap() {
			HashMap<Long, WorkOrder_Permission> permissionMap = new HashMap<>();
			for (WorkOrder_Permission permission : WorkOrder_Permission.values()) {
				permissionMap.put(permission.getPermission(), permission);
			}
			return permissionMap;
		}
	}
}
