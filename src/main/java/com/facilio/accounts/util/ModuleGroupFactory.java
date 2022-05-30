package com.facilio.accounts.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleGroupFactory {
	
	private static final String HOME = "Home";
	private static final String MAINTENANCE = "Maintenance";
	private static final String ASSETS = "Assets";

	public enum ModuleGroupPermissionFactory {
	
		HOME (1,ModuleGroupFactory.HOME,"Dashboard, Portfolio, Reservation"),
		ASSETS(2,ModuleGroupFactory.ASSETS,"Assets, Inventory, Purchase Orders, Contracts, Reports"),
		MAINTENANCE(3,ModuleGroupFactory.MAINTENANCE,"WorkOrders, Approvals"),
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
		case HOME:
			return getHomeGroupModules();
		case ASSETS:
			return getAssetGroupModules(); 
		case MAINTENANCE:
			return getMaintanceGroupModules(); 
		default :
			break;
			
		}
		return null;
	}

	private static List<ModulePermissionWrapper> getHomeGroupModules() {
		
		List<ModulePermissionWrapper> modulePermissionWrappers = new ArrayList<ModulePermissionWrapper>();
		
		modulePermissionWrappers.add(new ModulePermissionWrapper(1,PermissionFactory.Dashboard_Permission.getModuleDisplayName(),PermissionFactory.Dashboard_Permission.getAllPermissions()));
		modulePermissionWrappers.add(new ModulePermissionWrapper(2,PermissionFactory.Portfolio_Permission.getModuleDisplayName(),PermissionFactory.Portfolio_Permission.getAllPermissions()));
		modulePermissionWrappers.add(new ModulePermissionWrapper(2,PermissionFactory.Reservation_Permission.getModuleDisplayName(),PermissionFactory.Reservation_Permission.getAllPermissions()));
		
		return modulePermissionWrappers;
	}
	
	private static List<ModulePermissionWrapper> getAssetGroupModules() {
		
		List<ModulePermissionWrapper> modulePermissionWrappers = new ArrayList<ModulePermissionWrapper>();
		
		modulePermissionWrappers.add(new ModulePermissionWrapper(1,PermissionFactory.Asset_Permission.getModuleDisplayName(),PermissionFactory.Asset_Permission.getAllPermissions()));
		modulePermissionWrappers.add(new ModulePermissionWrapper(2,PermissionFactory.Inventory_Permission.getModuleDisplayName(),PermissionFactory.Inventory_Permission.getAllPermissions()));
		modulePermissionWrappers.add(new ModulePermissionWrapper(2,PermissionFactory.Purchase_Order_Permission.getModuleDisplayName(),PermissionFactory.Purchase_Order_Permission.getAllPermissions()));
		modulePermissionWrappers.add(new ModulePermissionWrapper(2,PermissionFactory.Contract_Permission.getModuleDisplayName(),PermissionFactory.Contract_Permission.getAllPermissions()));
		modulePermissionWrappers.add(new ModulePermissionWrapper(2,PermissionFactory.Asset_Reports_Permission.getModuleDisplayName(),PermissionFactory.Asset_Reports_Permission.getAllPermissions()));
		
		return modulePermissionWrappers;
	}
	private static List<ModulePermissionWrapper> getMaintanceGroupModules() {
		
		List<ModulePermissionWrapper> modulePermissionWrappers = new ArrayList<ModulePermissionWrapper>();
		
		modulePermissionWrappers.add(new ModulePermissionWrapper(1,PermissionFactory.WorkOrder_Permission.getModuleDisplayName(),PermissionFactory.WorkOrder_Permission.getAllPermissions()));
		modulePermissionWrappers.add(new ModulePermissionWrapper(1,PermissionFactory.Approval_Permission.getModuleDisplayName(),PermissionFactory.Approval_Permission.getAllPermissions()));
		modulePermissionWrappers.add(new ModulePermissionWrapper(1,PermissionFactory.IndoorFloorPlan_Permission.getModuleDisplayName(),PermissionFactory.IndoorFloorPlan_Permission.getAllPermissions()));

		return modulePermissionWrappers;
	}
}
