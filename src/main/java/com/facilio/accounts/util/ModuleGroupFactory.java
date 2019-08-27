package com.facilio.accounts.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleGroupFactory {
	
	private static final String SPACE = "Space";
	private static final String MAINTENACE = "Maintenance";

	public enum ModuleGroupPermissionFactory {
	
		SPACE (1,"Space","Dashboard, Portfolio"),
		CREATE(2,"",""),
		MAINTENACE(3,ModuleGroupFactory.MAINTENACE,"maintance"),
		;
	
		int intValue;
		String groupName;
		String description;
		
		List<ModulePermissionWrapper> modulePermissions;
	
		ModuleGroupPermissionFactory(int intValue,String groupName,String description) {
			this.intValue = intValue;
			this.groupName = groupName;
			this.description = description;
			this.modulePermissions = getModulePermissionsWithGroupName(this.groupName);
		}
	
		public long getIntValue() {
			return this.intValue;
		}
		public String getGroupName() {
			return this.groupName;
		}
		public String getDescription() {
			return this.description;
		}
		public List<ModulePermissionWrapper> getModulePermissions() {
			return modulePermissions;
		}
		
		
		private static final List<ModuleGroupPermissionFactory> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<ModuleGroupPermissionFactory> initTypeMap() {
			List<ModuleGroupPermissionFactory> typeMap = new ArrayList<ModuleGroupPermissionFactory>();

			for (ModuleGroupPermissionFactory type : values()) {
					typeMap.add(type);
			}
			return typeMap;
		}
		
		public static List<ModuleGroupPermissionFactory> getModuleGroupPermissions() {
			return permissionList;
		}
	}
	
	private static List<ModulePermissionWrapper> getModulePermissionsWithGroupName(String moduleGroup) {
		
		switch(moduleGroup) {
		case SPACE:
			
			break;
		case MAINTENACE:
			
			return getMaintanceGroupModules(); 
		default :
			break;
			
		}
		return null;
	}

	private static List<ModulePermissionWrapper> getMaintanceGroupModules() {
		
		List<ModulePermissionWrapper> modulePermissionWrappers = new ArrayList<ModulePermissionWrapper>();
		
		modulePermissionWrappers.add(new ModulePermissionWrapper(1,PermissionFactory.WorkOrder_Permission.getModuleDisplayName(),PermissionFactory.WorkOrder_Permission.getAllPermissions()));
//		modulePermissionWrappers.add(new ModulePermissionWrapper(2,PermissionFactory.PM_Permission.getModuleDisplayName(),PermissionFactory.PM_Permission.getAllPermissions()));
		
		return modulePermissionWrappers;
	}
}
