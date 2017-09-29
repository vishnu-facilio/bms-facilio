package com.facilio.bmsconsole.context;

import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;

public class RoleContext {
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long roleId = 0;
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long permissions;
	public long getPermissions() {
		return permissions;
	}
	public void setPermissions(long permissions) {
		this.permissions = permissions;
	}
	
	public boolean hasPermission(long permission)
	{
		if(name.equals(FacilioConstants.Role.ADMINISTRATOR))
		{
			return true;
		}
		return (permissions & permission) == permission;
	}
	
	public boolean hasPermission(String permissionValue) throws Exception {
		
		boolean hasAccess = false;
		
		String[] permissionArray = permissionValue.split(",");
		
		for (String permission : permissionArray) {
			
			permission = permission.trim();

			FacilioConstants.Permission permType = null;
			FacilioConstants.PermissionGroup permGroupType = null;
			
			try {
				permType = FacilioConstants.Permission.valueOf(permission);
			} catch (Exception e) {
				e.getMessage();
			}
			try {
				permGroupType = FacilioConstants.PermissionGroup.valueOf(permission);
			} catch (Exception e) {
				e.getMessage();
			}
			
			if (permType != null) {
				hasAccess = hasPermission(permType);
			}
			else if (permGroupType != null) {
				FacilioConstants.Permission permissionList[] = permGroupType.getPermission();
				for (FacilioConstants.Permission perm : permissionList) {
					hasAccess = hasPermission(perm);
					if (hasAccess) {
						break;
					}
				}
			}
			else {
				throw new Exception("Invalid permission type: "+permission);
			}
			
			if (hasAccess) {
				break;
			}
		}
		return hasAccess;
	}
	
	public boolean hasPermission(FacilioConstants.Permission permission)
	{
		if(name.equals(FacilioConstants.Role.ADMINISTRATOR))
		{
			return true;
		}
		return (permissions & permission.getPermission()) == permission.getPermission();
	}
	
	public JSONObject getAccessControl() throws Exception {
		
		JSONObject orgPermission = new JSONObject();
		orgPermission.put("administer", (permissions & FacilioConstants.Permission.ORG_ACCESS_ADMINISTER.getPermission()) == FacilioConstants.Permission.ORG_ACCESS_ADMINISTER.getPermission());
		orgPermission.put("delete", (permissions & FacilioConstants.Permission.ORG_ACCESS_DELETE.getPermission()) == FacilioConstants.Permission.ORG_ACCESS_DELETE.getPermission());
		
		JSONObject userPermission = new JSONObject();
		userPermission.put("administer", (permissions & FacilioConstants.Permission.USER_ACCESS_ADMINISTER.getPermission()) == FacilioConstants.Permission.USER_ACCESS_ADMINISTER.getPermission());
		userPermission.put("delete", (permissions & FacilioConstants.Permission.USER_ACCESS_DELETE.getPermission()) == FacilioConstants.Permission.USER_ACCESS_DELETE.getPermission());
		
		JSONObject groupPermission = new JSONObject();
		groupPermission.put("administer", (permissions & FacilioConstants.Permission.GROUP_ACCESS_ADMINISTER.getPermission()) == FacilioConstants.Permission.GROUP_ACCESS_ADMINISTER.getPermission());
		groupPermission.put("delete", (permissions & FacilioConstants.Permission.GROUP_ACCESS_DELETE.getPermission()) == FacilioConstants.Permission.GROUP_ACCESS_DELETE.getPermission());
		
		JSONObject workorderCreatePermission = new JSONObject();
		workorderCreatePermission.put("any", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_CREATE_ANY.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_CREATE_ANY.getPermission());
		workorderCreatePermission.put("accessible_spaces", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_CREATE_ACCESSIBLE_SPACES.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_CREATE_ACCESSIBLE_SPACES.getPermission());
		
		JSONObject workorderUpdatePermission = new JSONObject();
		workorderUpdatePermission.put("any", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_UPDATE_ANY.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_UPDATE_ANY.getPermission());
		workorderUpdatePermission.put("accessible_spaces", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_UPDATE_ACCESSIBLE_SPACES.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_UPDATE_ACCESSIBLE_SPACES.getPermission());
		workorderUpdatePermission.put("own", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_UPDATE_OWN.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_UPDATE_OWN.getPermission());
		
		JSONObject workorderDeletePermission = new JSONObject();
		workorderDeletePermission.put("any", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_DELETE_ANY.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_DELETE_ANY.getPermission());
		workorderDeletePermission.put("accessible_spaces", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_DELETE_ACCESSIBLE_SPACES.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_DELETE_ACCESSIBLE_SPACES.getPermission());
		workorderDeletePermission.put("own", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_DELETE_OWN.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_DELETE_OWN.getPermission());
		
		JSONObject workorderCanAssignPermission = new JSONObject();
		workorderCanAssignPermission.put("any", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_ASSIGN_ANY.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_ASSIGN_ANY.getPermission());
		workorderCanAssignPermission.put("accessible_spaces", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_ASSIGN_ACCESSIBLE_SPACES.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_ASSIGN_ACCESSIBLE_SPACES.getPermission());
		workorderCanAssignPermission.put("own", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_ASSIGN_OWN.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_ASSIGN_OWN.getPermission());
		
		JSONObject workorderCanBeAssignedPermission = new JSONObject();
		workorderCanBeAssignedPermission.put("any", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY.getPermission());
		workorderCanBeAssignedPermission.put("accessible_spaces", (permissions & FacilioConstants.Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ACCESSIBLE_SPACES.getPermission()) == FacilioConstants.Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ACCESSIBLE_SPACES.getPermission());
		
		JSONObject workorderPermission = new JSONObject();
		workorderPermission.put("create", workorderCreatePermission);
		workorderPermission.put("update", workorderUpdatePermission);
		workorderPermission.put("delete", workorderDeletePermission);
		workorderPermission.put("can_assign", workorderCanAssignPermission);
		workorderPermission.put("can_be_assigned", workorderCanBeAssignedPermission);
		
		JSONObject spaceManagementPermission = new JSONObject();
		spaceManagementPermission.put("access", (permissions & FacilioConstants.Permission.SPACEMANAGEMENT_ACCESS_ENABLE.getPermission()) == FacilioConstants.Permission.SPACEMANAGEMENT_ACCESS_ENABLE.getPermission());
		
		JSONObject fireAlarmsPermission = new JSONObject();
		fireAlarmsPermission.put("access", (permissions & FacilioConstants.Permission.FIREALARM_ACCESS_ENABLE.getPermission()) == FacilioConstants.Permission.FIREALARM_ACCESS_ENABLE.getPermission());
		
		JSONObject dashboardPermission = new JSONObject();
		dashboardPermission.put("access", (permissions & FacilioConstants.Permission.DASHBOARD_ACCESS_ENABLE.getPermission()) == FacilioConstants.Permission.DASHBOARD_ACCESS_ENABLE.getPermission());
		
		JSONObject reportsPermission = new JSONObject();
		reportsPermission.put("access", (permissions & FacilioConstants.Permission.REPORTS_ACCESS_ENABLE.getPermission()) == FacilioConstants.Permission.REPORTS_ACCESS_ENABLE.getPermission());
		
		JSONObject setupPermission = new JSONObject();
		setupPermission.put("access", hasPermission(FacilioConstants.PermissionGroup.SETUP.name()));
		
		JSONObject accessControl = new JSONObject();
		accessControl.put("org", orgPermission);
		accessControl.put("user", userPermission);
		accessControl.put("group", userPermission);
		accessControl.put("workorder", workorderPermission);
		accessControl.put("space_management", spaceManagementPermission);
		accessControl.put("fire_alarm", spaceManagementPermission);
		accessControl.put("dashboard", dashboardPermission);
		accessControl.put("reports", reportsPermission);
		accessControl.put("setup", setupPermission);
		
		return accessControl;
	}
}
