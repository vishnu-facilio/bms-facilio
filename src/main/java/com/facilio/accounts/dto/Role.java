package com.facilio.accounts.dto;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.ModulePermission;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.PickListOperators;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Role implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(Role.class.getName());
	private static org.apache.log4j.Logger log = LogManager.getLogger(Role.class.getName());

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
	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public boolean hasPermission(String moduleName, String permissionVal) {
		for (Permissions permission : getPermissions()) {
			if(permission.getModuleName().equals(moduleName)) {
				if(AccountConstants.DefaultRole.SUPER_ADMIN.equals(name) ) {
					return permission.hasPermission(0L);
				}
				try {
					return permission.hasPermission(permissionVal);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.info("Exception occurred ", e);
				}
			}
		}
		return false;
	}
	private List<Permissions> permissions = new ArrayList<>();
	public List<Permissions> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permissions> permissions) {
		if(permissions != null) {
			this.permissions = permissions;
		}
	}
	public void addPermission(Permissions permission) {
		permissions.add(permission);
	}
	
	private String roleMembersEmail;
	public String getRoleMembersEmail() {
		return roleMembersEmail;
	}
	public void setRoleMembersEmail(String roleMembersEmail) {
		this.roleMembersEmail = roleMembersEmail;
	}
	
	private String roleMembersPhone;
	public String getRoleMembersPhone() {
		return roleMembersPhone;
	}
	public void setRoleMembersPhone(String roleMembersPhone) {
		this.roleMembersPhone = roleMembersPhone;
	}
	
	private String roleMembersIds;
	public String getRoleMembersIds() {
		return roleMembersIds;
	}
	public void setRoleMembersIds(String roleMembersIds) {
		this.roleMembersIds = roleMembersIds;
	}
	
	public Criteria permissionCriteria(String moduleName, String action)
	{
		Criteria criteria = null;
		if(getPermissions() == null) {
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

}