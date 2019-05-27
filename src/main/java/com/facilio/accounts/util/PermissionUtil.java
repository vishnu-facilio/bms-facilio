package com.facilio.accounts.util;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants.ModulePermission;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {
	private static final Logger log = LogManager.getLogger(PermissionUtil.class.getName());

    public static Criteria getCurrentUserScopeCriteria (String moduleName, FacilioField...fields) {
        return getUserScopeCriteria(AccountUtil.getCurrentUser(), moduleName, fields);
    }

    private static Criteria getUserScopeCriteria(User user, String moduleName, FacilioField...fields) {
		Criteria criteria = null;
        List<Long> accessibleSpace = user.getAccessibleSpace();
		if(accessibleSpace == null) {
			return null;
		}
		if (moduleName.equals("pmjobs")) {
			Condition templateResourceCondition = new Condition();
			templateResourceCondition.setColumnName("Workorder_Template.RESOURCE_ID");
			templateResourceCondition.setFieldName("resourceId");
			templateResourceCondition.setOperator(BuildingOperator.BUILDING_IS);
			templateResourceCondition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria = new Criteria();
			criteria.addAndCondition(templateResourceCondition);

			Condition multiResourceCondition = new Condition();
			multiResourceCondition.setColumnName("PM_Jobs.RESOURCE_ID");
			multiResourceCondition.setFieldName("resourceId");
			multiResourceCondition.setOperator(BuildingOperator.BUILDING_IS);
			multiResourceCondition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria.addOrCondition(multiResourceCondition);
		}
		if(moduleName.equals("workorder") || moduleName.equals("workorderrequest") || moduleName.equals("planned") || moduleName.equals("alarm"))
		{
			Condition condition = new Condition();
			if (moduleName.equals("planned")) {
				condition.setColumnName("Workorder_Template.RESOURCE_ID");
			}
			else {
				condition.setColumnName("RESOURCE_ID");
			}
			condition.setFieldName("resourceId");
			condition.setOperator(BuildingOperator.BUILDING_IS);
			condition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria = new Criteria();
			criteria.addAndCondition(condition);

			String siteColumn = "SITE_ID";
			switch (moduleName) {
			case "workorder": siteColumn = "WorkOrders.SITE_ID";
			break;
			case "workorderrequest": siteColumn = "WorkOrderRequests.SITE_ID";
			break;
			case "planned": siteColumn = "Preventive_Maintenance.SITE_ID";
			break;
			case "alarm": siteColumn = "Alarms.SITE_ID";
			break;
			}

			Condition siteCondition = new Condition();
			siteCondition.setColumnName(siteColumn);
			siteCondition.setFieldName("siteId");
			siteCondition.setOperator(BuildingOperator.BUILDING_IS);
			siteCondition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria.addOrCondition(siteCondition);
		}
		if(moduleName.equals("asset")) {
			Condition condition = new Condition();
			condition.setColumnName("SPACE_ID");
			condition.setFieldName("spaceId");
			condition.setOperator(BuildingOperator.BUILDING_IS);
			condition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria = new Criteria();
			criteria.addAndCondition(condition);
			if (user.getUserType() == 0) {
				criteria.addAndCondition(CriteriaAPI.getCondition("hideToCustomer", "HIDE_TO_CUSTOMER", String.valueOf(false), BooleanOperators.IS));
			}
		}
		if(moduleName.equals("site")) {
			Condition condition = new Condition();
			condition.setColumnName("Resources.ID");
			condition.setFieldName("id");
			condition.setOperator(PickListOperators.IS);
			condition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria = new Criteria();
			criteria.addAndCondition(condition);
		}
		if(moduleName.equals("building") || moduleName.equals("floor") || moduleName.equals("space") || moduleName.equals("zone") || moduleName.equals("basespace")) {
			Condition condition = new Condition();
			condition.setColumnName("Resources.ID");
			condition.setFieldName("id");
			condition.setOperator(BuildingOperator.BUILDING_IS);
			condition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria = new Criteria();
			criteria.addAndCondition(condition);
		}
		if (fields != null && fields.length > 0) {
			criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(fields[0], accessibleSpace, BuildingOperator.BUILDING_IS));
		}
		return criteria;
	}

	public static Criteria getCurrentUserPermissionCriteria(String moduleName, String action) {
    	return getPermissionCriteria(AccountUtil.getCurrentUser().getRole(), moduleName, action);
	}

	private static Criteria getPermissionCriteria(Role role, String moduleName, String action) {
		Criteria criteria = null;
		List<Permissions> permissions = role.getPermissions();
		if(permissions == null) {
			return null;
		}
		if(moduleName.equals("workorder") || moduleName.equals("workorderrequest") || moduleName.equals("planned") || moduleName.equals("inventory"))
		{
			for (Permissions perm : permissions) {
				if (perm.getModuleName().equals(moduleName)) {
					boolean access = false;
					long permissionValue = perm.getPermission();
					for (AccountConstants.ModulePermission perms : AccountConstants.ModulePermission.values()) {

						// Temporary...
						if (action.equals("read") && perms != ModulePermission.READ && perms != ModulePermission.READ_OWN &&  perms != ModulePermission.READ_TEAM) {
							continue;
						}

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

								if (moduleName.equals("inventory")) {
									Condition storeRoomCondition = new Condition();
									storeRoomCondition.setColumnName("Store_room.OWNER_ID");
									storeRoomCondition.setFieldName("owner");
									storeRoomCondition.setOperator(PickListOperators.IS);
									storeRoomCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

									criteria = new Criteria();
									criteria.addAndCondition(storeRoomCondition);
								}
									else {
									Condition userCondition = new Condition();
									if (moduleName.equals("planned")) {
										userCondition.setColumnName("Workorder_Template.ASSIGNED_TO_ID");
									}
									else {
										userCondition.setColumnName("ASSIGNED_TO_ID");
									}
									userCondition.setFieldName("assignedToid");
									userCondition.setOperator(PickListOperators.IS);
									userCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

									criteria = new Criteria();
									criteria.addAndCondition(userCondition);
									}
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
										log.info("Exception occurred ", e);
									}
									List<Long> groupIds = new ArrayList<>();
									for(Group group : groups) {
										groupIds.add(group.getId());
									}
									String groupColName = "ASSIGNMENT_GROUP_ID";
									String colName = "ASSIGNED_TO_ID";
									if (moduleName.equals("planned")) {
										groupColName = "Workorder_Template.ASSIGNMENT_GROUP_ID";
										colName = "Workorder_Template.ASSIGNED_TO_ID";
									}
									Condition groupCondition = CriteriaAPI.getCondition(groupColName, "assignmentGroupId", StringUtils.join(groupIds, ","), PickListOperators.IS);
									Condition userCondition = CriteriaAPI.getCondition(colName, "assignedToid", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);

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

	public static boolean currentUserHasPermission(String moduleName, String permissionVal) {
		Role role = AccountUtil.getCurrentUser().getRole();

		// TODO Have to check separately for delete org later. Admin cannot delete org
		if(role.getName().equals(AccountConstants.DefaultSuperAdmin.SUPER_ADMIN) || role.getName().equals(AccountConstants.DefaultSuperAdmin.ADMINISTRATOR)) {
			return true;
		}
		for (Permissions permission : role.getPermissions()) {
			if(permission.getModuleName().equals(moduleName)) {
//				if(AccountConstants.DefaultRole.SUPER_ADMIN.equals(role.getName()) ) { //No idea why we are checking twice
//					return hasPermission(permission, 0L);
//				}
				try {
					return hasPermission(permission, permissionVal);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.info("Exception occurred ", e);
				}
			}
		}
		return false;
	}

	private static boolean hasPermission(Permissions perm, long permission) {
		if (perm.getPermission() == 0) {
			return true;
		}
		return (perm.getPermission() & permission) == permission;
	}

	private static boolean hasPermission(Permissions perm, AccountConstants.ModulePermission permission) {
		return hasPermission(perm, permission.getModulePermission());
	}

	private static boolean hasPermission(Permissions perm, String permissionValue) throws Exception {

		boolean hasAccess = false;
		String[] permissionArray = permissionValue.split(",");

		for (String permission : permissionArray) {

			permission = permission.trim();

			AccountConstants.ModulePermission permType = null;
			try {
				permType = AccountConstants.ModulePermission.valueOf(permission);
			} catch (Exception e) {
				e.getMessage();
			}
			if (permType != null) {
				hasAccess = hasPermission(perm, permType);
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

}
