package com.facilio.accounts.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.constants.FacilioConstants;

public class Role {

	private long roleId;
	private long orgId;
	private String name;
	private String description;
	private String permissionStr;

	public long getRoleId() {
		return roleId;
	}
	public long getId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPermissionStr() {
		return permissionStr;
	}
//	public long getPermissions() {
//		if (permissionStr != null) {
//			return Long.parseLong(permissionStr);
//		}		
//		return 0;
//	}
//	public void setPermissionStr(String permissionStr) {
//		this.permissionStr = permissionStr;
//	}
//	public void setPermissions(long permissions) {
//		this.permissionStr = permissions + "";
//	}

	public boolean hasPermission(String moduleName, String permissionVal) {
//		if (getPermissions() == 0) {
//			return true;
//		}
//		return (getPermissions() & permission) == permission;
		//check if sA
		for (Permissions permission : getPermissions()) {
			if(permission.getModuleName().equals(moduleName)) {
				if(name == AccountConstants.DefaultRole.SUPER_ADMIN ) {
					return permission.hasPermission(0L);
				}
				try {
					return permission.hasPermission(permissionVal);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
//
//	public boolean hasPermission(AccountConstants.Permission permission) {
//		return hasPermission(permission.getPermission());
//	}
//
//	public boolean hasPermission(String permissionValue) throws Exception {
//
//		boolean hasAccess = false;
//		String[] permissionArray = permissionValue.split(",");
//
//		for (String permission : permissionArray) {
//
//			permission = permission.trim();
//
//			AccountConstants.Permission permType = null;
//			AccountConstants.PermissionGroup permGroupType = null;
//
//			try {
//				permType = AccountConstants.Permission.valueOf(permission);
//			} catch (Exception e) {
//				e.getMessage();
//			}
//			try {
//				permGroupType = AccountConstants.PermissionGroup.valueOf(permission);
//			} catch (Exception e) {
//				e.getMessage();
//			}
//
//			if (permType != null) {
//				hasAccess = hasPermission(permType);
//			}
//			else if (permGroupType != null) {
//				AccountConstants.Permission permissionList[] = permGroupType.getPermission();
//				for (AccountConstants.Permission perm : permissionList) {
//					hasAccess = hasPermission(perm);
//					if (hasAccess) {
//						break;
//					}
//				}
//			}
//			else {
//				throw new Exception("Invalid permission type: "+permission);
//			}
//
//			if (hasAccess) {
//				break;
//			}
//		}
//		return hasAccess;
//	}
//	
	private List<Permissions> permissions;
	public List<Permissions> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permissions> permissions) {
		this.permissions = permissions;
	}
	public void addPermission(Permissions permission) {
		if (this.permissions == null) {
			permissions = new ArrayList<>();
		}
		permissions.add(permission);
	}
	
	public Criteria permissionCriteria(String moduleName)
	{
		Criteria criteria = null;
		if(getPermissions() == null) {
			return null;
		}
		if(moduleName.equals("workorder"))
		{	
			for (Permissions perm : permissions) {
				if (perm.getModuleName().equals(moduleName)) {
					boolean access = false;
					long permissionValue = perm.getPermission();
					for (AccountConstants.ModulePermission perms : AccountConstants.ModulePermission.values()) {
						access = (permissionValue & perms.getModulePermission()) == perms.getModulePermission();
						if(access) {
							switch(perms) {
								case READ:
								case UPDATE:
								case DELETE: {
									return null;
								}
								case READ_OWN:
								case DELETE_OWN:
								case UPDATE_OWN: {
									Condition userCondition = new Condition();
									userCondition.setColumnName("ASSIGNED_TO_ID");
									userCondition.setFieldName("assignedToid");
									userCondition.setOperator(PickListOperators.IS);
									userCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);
									
									criteria = new Criteria();
									criteria.addAndCondition(userCondition);
								}break;
								case READ_TEAM:
								case DELETE_TEAM:
								case UPDATE_TEAM:{
									long ouid = AccountUtil.getCurrentAccount().getUser().getOuid();
									List<Group> groups = new ArrayList<>();
									try {
										groups = AccountUtil.getGroupBean().getMyGroups(ouid);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									List<Long> groupIds = new ArrayList<>();
									for(Group group : groups) {
										groupIds.add(group.getId());
									}
									Condition groupCondition = CriteriaAPI.getCondition("ASSIGNMENT_GROUP_ID", "assignmentGroupId", StringUtils.join(groupIds, ","), PickListOperators.IS);
									
									Condition userCondition = CriteriaAPI.getCondition("ASSIGNED_TO_ID", "assignedToid", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
									
									criteria = new Criteria();
									criteria.addOrCondition(groupCondition);
									criteria.addOrCondition(userCondition);
								}break;
							}
						}
					}
				}
			}
		}
		return criteria;
	}

}