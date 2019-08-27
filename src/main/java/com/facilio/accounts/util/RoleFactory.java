package com.facilio.accounts.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.constants.FacilioConstants;


public class RoleFactory {
	
	private static final String SUPER_ADMIN = "Super Administrator";
	private static final String ADMIN = "Administrator";
	private static final String MANAGER = "Manager";
	private static final String TECHNICIAN = "Technician";

	public static enum Role {

		SUPER_ADMIN(1,RoleFactory.SUPER_ADMIN),
		ADMIN(2,RoleFactory.ADMIN),
		MANAGER(3,RoleFactory.MANAGER),
		TECHNICIAN(4,RoleFactory.TECHNICIAN),
		;

		int roleType;
		String name;
		Map<String,List<PermissionInterface>> permissionMap;

		Role(int roleType,String name) {
			this.roleType = roleType;
			this.name = name;
			permissionMap = RoleFactory.getPermissionMapForRole(name);
		}

		public int getRoleType() {
			return this.roleType;
		}
		public Map<String,List<PermissionInterface>> getPermissionMap() {
			return this.permissionMap;
		}
		public String getName() {
			return this.name;
		}

	}
	
	private static Map<String, List<PermissionInterface>> getManagerPermissions() {

		Map<String, List<PermissionInterface>> map = new HashMap<String, List<PermissionInterface>>();
		
		List<PermissionInterface> workOrderPermissions = new ArrayList<PermissionInterface>();
		
		workOrderPermissions.add(PermissionFactory.WorkOrder_Permission.READ);
//		workOrderPermissions.add(PermissionFactory.WorkOrder_Permission.ASSIGN);
		workOrderPermissions.add(PermissionFactory.WorkOrder_Permission.CREATE);
		
		map.put(FacilioConstants.ContextNames.WORK_ORDER, workOrderPermissions);
		
		return map;
	}
	private static Map<String, List<PermissionInterface>> getTechnicianPermissions() {

		Map<String, List<PermissionInterface>> map = new HashMap<String, List<PermissionInterface>>();
		
		List<PermissionInterface> workOrderPermissions = new ArrayList<PermissionInterface>();
		
		workOrderPermissions.add(PermissionFactory.WorkOrder_Permission.READ);
		workOrderPermissions.add(PermissionFactory.WorkOrder_Permission.CREATE);
		
		map.put(FacilioConstants.ContextNames.WORK_ORDER, workOrderPermissions);
		
		return map;
	}
	
	private static Map<String, List<PermissionInterface>> getPermissionMapForRole(String roleName) {
		
		if (roleName.equals(RoleFactory.MANAGER)) {
			return getManagerPermissions();
		}
		else if (roleName.equals(RoleFactory.TECHNICIAN)) {
			return getTechnicianPermissions();
		}
		return null;
	}
	
}
