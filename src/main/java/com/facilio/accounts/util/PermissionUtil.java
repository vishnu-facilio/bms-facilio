package com.facilio.accounts.util;

import com.facilio.accounts.dto.*;
import com.facilio.accounts.util.AccountConstants.ModulePermission;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.PermissionGroup;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabContext.Type;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.bmsconsoleV3.util.AppModulePermissionUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.delegate.context.DelegationType;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class PermissionUtil {
	private static final Logger log = LogManager.getLogger(PermissionUtil.class.getName());

	private static final List<String> SPECIAL_MODULES = new ArrayList<>();

	static {
//		SPECIAL_MODULES.add("planned");
//		SPECIAL_MODULES.add("setup");
		// more to be added
	}

    public static Criteria getCurrentUserScopeCriteria (String moduleName, FacilioField...fields) {
		try {
			if (AccountUtil.isFeatureEnabled(FeatureLicense.SCOPE_VARIABLE)) {
				return null;
			}
		} catch (Exception e){
			log.info(e);
		}
        return getUserScopeCriteria(AccountUtil.getCurrentUser(), moduleName, fields);
    }

    private static Criteria getUserScopeCriteria(User user, String moduleName, FacilioField...fields) {
		Criteria criteria = null;
        List<Long> accessibleSpace = user.getAccessibleSpace();

		if(accessibleSpace == null) {
			return null;
		}

		//handling user delegation
		try {
			List<User> delegatedUsers = DelegationUtil.getUsers(AccountUtil.getCurrentAccount().getUser(), System.currentTimeMillis(), DelegationType.USER_SCOPING);
			if(CollectionUtils.isNotEmpty(delegatedUsers)) {
				for(User delegatedUser : delegatedUsers) {
					if (delegatedUser.getId() != AccountUtil.getCurrentAccount().getUser().getId()) {
						accessibleSpace.addAll(AccountUtil.getUserBean().getAccessibleSpaceList(delegatedUser.getOuid()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		if(moduleName.equals("workorder") || moduleName.equals("workorderrequest") || moduleName.equals("planned") || moduleName.equals("alarm")
		|| moduleName.equals(FacilioConstants.ContextNames.NEW_READING_ALARM) || moduleName.equals(FacilioConstants.ContextNames.BMS_ALARM) ||
				moduleName.equals(FacilioConstants.ContextNames.ML_ALARM) || moduleName.equals(FacilioConstants.ContextNames.VIOLATION_ALARM) || moduleName.equals(FacilioConstants.ContextNames.AGENT_ALARM))
		{
			criteria = new Criteria();
			if (!moduleName.equals(FacilioConstants.ContextNames.AGENT_ALARM)) {
				Condition condition = new Condition();
				if (moduleName.equals("planned")) {
					condition.setColumnName("Workorder_Template.RESOURCE_ID");
				}
				else if (moduleName.equals("workorder")) {
					condition.setColumnName("Tickets.RESOURCE_ID");
				} else {
					condition.setColumnName("RESOURCE_ID");
				}
				condition.setFieldName("resourceId");
				condition.setOperator(BuildingOperator.BUILDING_IS);
				condition.setValue(StringUtils.join(accessibleSpace, ","));
				
				
				criteria.addAndCondition(condition);
			}

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
			case FacilioConstants.ContextNames.NEW_READING_ALARM : siteColumn = "ReadingAlarm.SITE_ID";
			break;
			case FacilioConstants.ContextNames.BMS_ALARM : siteColumn = "BMSAlarm.SITE_ID";
			break;
			case  FacilioConstants.ContextNames.ML_ALARM : siteColumn = "ML_Alarms.SITE_ID";
			break;
			case  FacilioConstants.ContextNames.VIOLATION_ALARM : siteColumn = "ReadingViolationAlarm.SITE_ID";
			break;
			case  FacilioConstants.ContextNames.AGENT_ALARM : siteColumn = "Agent_Alarm.SITE_ID";
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
		if (moduleName.equals(ContextNames.MV_PROJECT_MODULE)) {
			criteria = criteria == null ? new Criteria() : criteria;
			criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(), accessibleSpace, BuildingOperator.BUILDING_IS));
		}
		if (fields != null && fields.length > 0) {
			criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(fields[0], accessibleSpace, BuildingOperator.BUILDING_IS));
		}
		return criteria;
	}

	public static Criteria getCurrentUserPermissionCriteria(String moduleName, String action) {
			try {
				if (AccountUtil.isFeatureEnabled(FeatureLicense.WEB_TAB)) {
					return getNewPermissionCriteria(AccountUtil.getCurrentUser().getRole(), moduleName, action);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return getPermissionCriteria(AccountUtil.getCurrentUser().getRole(), moduleName, action);
	}


	private static Criteria getNewPermissionCriteria(Role role, String moduleName, String action) {
		Criteria criteria = null;
		HttpServletRequest request = ServletActionContext.getRequest();
		if (request != null) {
			String currentTab = request.getHeader("X-Tab-Id");
			if (currentTab != null && !currentTab.isEmpty()) {
				long tabId = Long.valueOf(currentTab);
				WebTabContext webTabContext;
				try {
					WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
					webTabContext = tabBean.getWebTab(tabId);
					if (webTabContext != null) {
						boolean hasAccess = false;
						long userPermissionVal = ApplicationApi.getRolesPermissionValForTab(tabId, role.getId());
						int tabType = webTabContext.getType();
						String moduleNameToFetch = moduleName;
//						if (moduleNameToFetch.equals("planned")) {
//							moduleNameToFetch = "workorder";
//						}
						PermissionGroup permissionGroup = NewPermissionUtil.getPermissionGroup(tabType, moduleNameToFetch, action);
						if (permissionGroup != null) {
							List<Permission> permissions = permissionGroup.getPermissions();
							if (permissions != null && !permissions.isEmpty()) {
								for (Permission permission : permissions) {
									hasAccess = ((permission.getValue() & userPermissionVal) == permission.getValue());
									if (hasAccess) {
										if(permission.getActionName().equals("READ") || permission.getActionName().equals("UPDATE") || permission.getActionName().equals("DELETE")) {
											return null;
										} else if(permission.getActionName().equals("READ_OWN") || permission.getActionName().equals("UPDATE_OWN") || permission.getActionName().equals("DELETE_OWN")) {
											if (moduleName.equals("inventory")) {
												Condition storeRoomCondition = new Condition();
												storeRoomCondition.setColumnName("Store_room.OWNER_ID");
												storeRoomCondition.setFieldName("owner");
												storeRoomCondition.setOperator(PickListOperators.IS);
												storeRoomCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

												criteria = new Criteria();
												criteria.addAndCondition(storeRoomCondition);
											}
											else if (moduleName.equals("inventoryrequest")) {
												Condition requestedForCondition = new Condition();
												requestedForCondition.setColumnName("Inventory_Requests.REQUESTED_BY");
												requestedForCondition.setFieldName("requestedBy");
												requestedForCondition.setOperator(PickListOperators.IS);
												requestedForCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

												criteria = new Criteria();
												criteria.addAndCondition(requestedForCondition);
											}
											else {
												Condition userCondition = new Condition();
												boolean isPlanned = false;
												if (moduleName.equals("planned")) {
													isPlanned = true;
													userCondition.setColumnName("Workorder_Template.ASSIGNED_TO_ID");
												}
												else if(moduleName.equals("workorder")){
													userCondition.setColumnName("Tickets.ASSIGNED_TO_ID");
												}
												else {
													userCondition.setColumnName("ASSIGNED_TO_ID");
												}
												userCondition.setFieldName("assignedToid");
												userCondition.setOperator(PickListOperators.IS);
												userCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

												criteria = new Criteria();
												criteria.addAndCondition(userCondition);
												if (!isPlanned) {
													Condition createdByCondition = null;
													if(moduleName.equals("workorder")) {
														createdByCondition = CriteriaAPI.getCondition("Tickets.CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
													}else {
														createdByCondition = CriteriaAPI.getCondition("CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
													}
													criteria.addOrCondition(createdByCondition);
												}

											}
											return criteria;
										} else if(permission.getActionName().equals("READ_TEAM") || permission.getActionName().equals("UPDATE_TEAM") || permission.getActionName().equals("DELETE_TEAM")){
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
											boolean isPlanned = false;
											if (moduleName.equals("planned")) {
												isPlanned = true;
												groupColName = "Workorder_Template.ASSIGNMENT_GROUP_ID";
												colName = "Workorder_Template.ASSIGNED_TO_ID";
											}
											else if(moduleName.equals("workorder")){
												groupColName = "Tickets.ASSIGNMENT_GROUP_ID";
												colName = "Tickets.ASSIGNED_TO_ID";
											}
											Condition groupCondition = CriteriaAPI.getCondition(groupColName, "assignmentGroupId", StringUtils.join(groupIds, ","), PickListOperators.IS);
											Condition userCondition = CriteriaAPI.getCondition(colName, "assignedToid", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);

											criteria = new Criteria();
											criteria.addOrCondition(groupCondition);
											criteria.addOrCondition(userCondition);

											if (!isPlanned) {
												Condition createdByCondition = null;
												if(moduleName.equals("workorder")) {
													createdByCondition = CriteriaAPI.getCondition("Tickets.CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
												}else {
													createdByCondition = CriteriaAPI.getCondition("CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
												}
												criteria.addOrCondition(createdByCondition);
											}
											return criteria;
										}
									}
								}
							}
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return criteria;
	}

	private static Criteria getV3NewPermissionCriteria(Role role, String moduleName, String action) {
		Criteria criteria = null;
		HttpServletRequest request = ServletActionContext.getRequest();
		if (request != null) {
			String currentTab = request.getHeader("X-Tab-Id");
			if (currentTab != null && !currentTab.isEmpty()) {
				long tabId = Long.valueOf(currentTab);
				WebTabContext webTabContext;
				try {
					WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
					webTabContext = tabBean.getWebTab(tabId);
					if (webTabContext != null) {
						boolean hasAccess = false;

						NewPermission newPermission = ApplicationApi.getRolesPermissionForTab(tabId, role.getId());
//						if (moduleNameToFetch.equals("planned")) {
//							moduleNameToFetch = "workorder";
//						}
						List<PermissionGroup> permissionGroups = V3PermissionUtil.getPermissionGroups(webTabContext);
						if(CollectionUtils.isNotEmpty(permissionGroups)) {
							for(PermissionGroup permissionGroup : permissionGroups) {
								if (permissionGroup != null) {
									List<Permission> permissions = permissionGroup.getPermissions();
									if (permissions != null && !permissions.isEmpty()) {
										for (Permission permission : permissions) {
											long userPermissionVal = -1;
											Map<String, Permission> permissionMap = AppModulePermissionUtil.getPermissionsMap();
											if (permissionMap.containsKey(permission.getActionName())) {
												int permissionGroupIndexValue = permissionMap.get(permission.getActionName()).getPermissionMapping().getIndex();
												if (permissionGroupIndexValue == AppModulePermissionUtil.PermissionMapping.GROUP1PERMISSION.getIndex()) {
													userPermissionVal = newPermission.getPermission();
												} else if (permissionGroupIndexValue == AppModulePermissionUtil.PermissionMapping.GROUP2PERMISSION.getIndex()) {
													userPermissionVal = newPermission.getPermission2();
												}
											}
											hasAccess = ((permission.getValue() & userPermissionVal) == permission.getValue());
											if (hasAccess) {
												if (permission.getActionName().equals("READ") || permission.getActionName().equals("UPDATE") || permission.getActionName().equals("DELETE")) {
													return null;
												} else if (permission.getActionName().equals("READ_OWN") || permission.getActionName().equals("UPDATE_OWN") || permission.getActionName().equals("DELETE_OWN")) {
													if (moduleName.equals("inventory")) {
														Condition storeRoomCondition = new Condition();
														storeRoomCondition.setColumnName("Store_room.OWNER_ID");
														storeRoomCondition.setFieldName("owner");
														storeRoomCondition.setOperator(PickListOperators.IS);
														storeRoomCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

														criteria = new Criteria();
														criteria.addAndCondition(storeRoomCondition);
													} else if (moduleName.equals("inventoryrequest")) {
														Condition requestedForCondition = new Condition();
														requestedForCondition.setColumnName("Inventory_Requests.REQUESTED_BY");
														requestedForCondition.setFieldName("requestedBy");
														requestedForCondition.setOperator(PickListOperators.IS);
														requestedForCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

														criteria = new Criteria();
														criteria.addAndCondition(requestedForCondition);
													} else if (moduleName.equals(ContextNames.SERVICE_ORDER)) {
														Condition fieldAgentCondition = new Condition();
														fieldAgentCondition.setColumnName("ServiceOrders.FIELD_AGENT");
														fieldAgentCondition.setFieldName("fieldAgent");
														fieldAgentCondition.setOperator(PickListOperators.IS);
														fieldAgentCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_PEOPLE);

														criteria = new Criteria();
														criteria.addAndCondition(fieldAgentCondition);
													}else if (moduleName.equals(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)) {
														Condition fieldAgentCondition = new Condition();
														fieldAgentCondition.setColumnName("SERVICE_APPOINTMENT.PEOPLE_ID");
														fieldAgentCondition.setFieldName("fieldAgent");
														fieldAgentCondition.setOperator(PickListOperators.IS);
														fieldAgentCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_PEOPLE);

														criteria = new Criteria();
														criteria.addAndCondition(fieldAgentCondition);
													}else if (moduleName.equals(FacilioConstants.TimeOff.TIME_OFF)) {
														Condition fieldAgentCondition = new Condition();
														fieldAgentCondition.setColumnName("Time_Off.PEOPLE_ID");
														fieldAgentCondition.setFieldName("people");
														fieldAgentCondition.setOperator(PickListOperators.IS);
														fieldAgentCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_PEOPLE);

														criteria = new Criteria();
														criteria.addAndCondition(fieldAgentCondition);
													}else if (moduleName.equals(FacilioConstants.Trip.TRIP)) {
														Condition fieldAgentCondition = new Condition();
														fieldAgentCondition.setColumnName("TRIP.PEOPLE_ID");
														fieldAgentCondition.setFieldName("people");
														fieldAgentCondition.setOperator(PickListOperators.IS);
														fieldAgentCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_PEOPLE);

														criteria = new Criteria();
														criteria.addAndCondition(fieldAgentCondition);
													}else if (moduleName.equals(FacilioConstants.TimeSheet.TIME_SHEET)) {
														Condition fieldAgentCondition = new Condition();
														fieldAgentCondition.setColumnName("TIME_SHEET.PEOPLE_ID");
														fieldAgentCondition.setFieldName("fieldAgent");
														fieldAgentCondition.setOperator(PickListOperators.IS);
														fieldAgentCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_PEOPLE);

														criteria = new Criteria();
														criteria.addAndCondition(fieldAgentCondition);
													}
													else {
														Condition userCondition = new Condition();
														boolean isPlanned = false;
														if (moduleName.equals("planned")) {
															isPlanned = true;
															userCondition.setColumnName("Workorder_Template.ASSIGNED_TO_ID");
														} else if (moduleName.equals("workorder")) {
															userCondition.setColumnName("Tickets.ASSIGNED_TO_ID");
														} else {
															userCondition.setColumnName("ASSIGNED_TO_ID");
														}
														userCondition.setFieldName("assignedToid");
														userCondition.setOperator(PickListOperators.IS);
														userCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

														criteria = new Criteria();
														criteria.addAndCondition(userCondition);
														if (!isPlanned) {
															Condition createdByCondition = null;
															if (moduleName.equals("workorder")) {
																createdByCondition = CriteriaAPI.getCondition("Tickets.CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
															} else {
																createdByCondition = CriteriaAPI.getCondition("CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
															}
															criteria.addOrCondition(createdByCondition);
														}

													}
													return criteria;
												} else if (permission.getActionName().equals("READ_TEAM") || permission.getActionName().equals("UPDATE_TEAM") || permission.getActionName().equals("DELETE_TEAM")) {
													long ouid = AccountUtil.getCurrentAccount().getUser().getOuid();
													List<Group> groups = new ArrayList<>();
													try {
														groups = AccountUtil.getGroupBean().getMyGroups(ouid);
													} catch (Exception e) {
														// TODO Auto-generated catch block
														log.info("Exception occurred ", e);
													}
													List<Long> groupIds = new ArrayList<>();
													for (Group group : groups) {
														groupIds.add(group.getId());
													}
													String groupColName = "ASSIGNMENT_GROUP_ID";
													String colName = "ASSIGNED_TO_ID";
													boolean isPlanned = false;
													if (moduleName.equals("planned")) {
														isPlanned = true;
														groupColName = "Workorder_Template.ASSIGNMENT_GROUP_ID";
														colName = "Workorder_Template.ASSIGNED_TO_ID";
													} else if (moduleName.equals("workorder")) {
														groupColName = "Tickets.ASSIGNMENT_GROUP_ID";
														colName = "Tickets.ASSIGNED_TO_ID";
													}
													Condition groupCondition = CriteriaAPI.getCondition(groupColName, "assignmentGroupId", StringUtils.join(groupIds, ","), PickListOperators.IS);
													Condition userCondition = CriteriaAPI.getCondition(colName, "assignedToid", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);

													criteria = new Criteria();
													criteria.addOrCondition(groupCondition);
													criteria.addOrCondition(userCondition);

													if (!isPlanned) {
														Condition createdByCondition = null;
														if (moduleName.equals("workorder")) {
															createdByCondition = CriteriaAPI.getCondition("Tickets.CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
														} else {
															createdByCondition = CriteriaAPI.getCondition("CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
														}
														criteria.addOrCondition(createdByCondition);
													}
													return criteria;
												}
											}
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return criteria;
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
								else if (moduleName.equals("inventoryrequest")) {
									Condition requestedForCondition = new Condition();
									requestedForCondition.setColumnName("Inventory_Requests.REQUESTED_BY");
									requestedForCondition.setFieldName("requestedBy");
									requestedForCondition.setOperator(PickListOperators.IS);
									requestedForCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

									criteria = new Criteria();
									criteria.addAndCondition(requestedForCondition);
								}
									else {
									Condition userCondition = new Condition();
									boolean isPlanned = false;
									if (moduleName.equals("planned")) {
										isPlanned = true;
										userCondition.setColumnName("Workorder_Template.ASSIGNED_TO_ID");
									}
									else if(moduleName.equals("workorder")){
										userCondition.setColumnName("Tickets.ASSIGNED_TO_ID");
									}
									else {
										userCondition.setColumnName("ASSIGNED_TO_ID");
									}
									userCondition.setFieldName("assignedToid");
									userCondition.setOperator(PickListOperators.IS);
									userCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

									criteria = new Criteria();
									criteria.addAndCondition(userCondition);
									if (!isPlanned) {
										Condition createdByCondition = null;
										if(moduleName.equals("workorder"))
										{
											createdByCondition = CriteriaAPI.getCondition("Tickets.CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
										}else {
											createdByCondition = CriteriaAPI.getCondition("CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
										}
										criteria.addOrCondition(createdByCondition);
									}
									
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
									boolean isPlanned = false;
									if (moduleName.equals("planned")) {
										groupColName = "Workorder_Template.ASSIGNMENT_GROUP_ID";
										colName = "Workorder_Template.ASSIGNED_TO_ID";
										isPlanned = true;
									}
									else if(moduleName.equals("workorder")){
										groupColName = "Tickets.ASSIGNMENT_GROUP_ID";
										colName = "Tickets.ASSIGNED_TO_ID";
									}
									Condition groupCondition = CriteriaAPI.getCondition(groupColName, "assignmentGroupId", StringUtils.join(groupIds, ","), PickListOperators.IS);
									Condition userCondition = CriteriaAPI.getCondition(colName, "assignedToid", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);

									criteria = new Criteria();
									criteria.addOrCondition(groupCondition);
									criteria.addOrCondition(userCondition);
									if (!isPlanned) {
										Condition createdByCondition = null;
										if(moduleName.equals("workorder")) {
											createdByCondition = CriteriaAPI.getCondition("Tickets.CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
										}else {
											createdByCondition = CriteriaAPI.getCondition("CREATED_BY", "createdBy", (FacilioConstants.Criteria.LOGGED_IN_USER), PickListOperators.IS);
										}
										criteria.addOrCondition(createdByCondition);
									}
								}break;
							}
						}
					}
				}
			}
		}
		return criteria;
	}

	public static boolean isSpecialModule(String moduleName) {
		return SPECIAL_MODULES.contains(moduleName);
	}

	public static boolean currentUserHasPermission(String moduleName, String action, Role role) throws Exception {

		FacilioModule module = null;
//		if(!isSpecialModule(moduleName)) {
//			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			module = modBean.getModule(moduleName);
//		}
		for (Permissions permission : role.getPermissions()) {
			if(module != null) {
				if(permission.getModuleId() == module.getModuleId()) {
					try {
						return hasPermission(permission, action);
					} catch (Exception e) {
						log.info("Exception occurred ", e);
					}
				}
			}
			else if(permission.getModuleName().equals(moduleName)) {
				try {
					return hasPermission(permission, action);
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
			}
		}
		return false;
	}

	public static boolean currentUserHasPermission(long tabId, String moduleName, String action, Role role, String tabType) {

		try {
			long rolePermissionVal = ApplicationApi.getRolesPermissionValForTab(tabId, role.getRoleId());
			List<String> moduleNames = ApplicationApi.getModulesForTab(tabId);
			if (!moduleNames.isEmpty()) {
				if (moduleNames.contains(moduleName) ){
					boolean hasPerm =  hasPermission(rolePermissionVal, action, tabId);
					return hasPerm;
				}
			}
			else if(moduleName.equalsIgnoreCase("setup")) {
				WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
				WebTabContext tab = tabBean.getWebTab(tabId);
				if(tab != null) {
					if (tabType != null && tab.getTypeEnum().name().equals(tabType)) {
						boolean hasPerm = hasPermission(rolePermissionVal, action, tabId);
						return hasPerm;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public static boolean userHasPermission(long tabId, String moduleName, String action, Role role) {

		try {
			long rolePermissionVal = ApplicationApi.getRolesPermissionValForTab(tabId, role.getRoleId());
			boolean hasPerm =  hasPermission(rolePermissionVal, action, tabId);
			return hasPerm;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private static boolean hasPermission(Permissions perm, long permission) {
		if (perm.getPermission() == 0) {
			return true;
		}
		return (perm.getPermission() & permission) == permission;
	}

	public static boolean hasPermission(Permissions perm, AccountConstants.ModulePermission permission) {
		return hasPermission(perm, permission.getModulePermission());
	}

	public static boolean hasPermission(Permissions perm, String actions) throws Exception {

		boolean hasAccess = false;
		String[] actionArray = actions.split(",");

		for (String action : actionArray) {

			action = action.trim();

			AccountConstants.ModulePermission permType = null;
			try {
				permType = AccountConstants.ModulePermission.valueOf(action);
			} catch (Exception e) {
				e.getMessage();
			}
			if (permType != null) {
				hasAccess = hasPermission(perm, permType);
			}
			else {
				throw new Exception("Invalid permission type: "+action);
			}

			if (hasAccess) {
				break;
			}
		}
		return hasAccess;
	}

	public static boolean hasPermission(long perm, long permission) {
		if (perm == 0) {
			return true;
		}
		if (perm < 0) {
			return false;
		}
		return (perm & permission) == permission;
	}

	public static boolean hasPermission(NewPermission permission, String actions, long tabId) throws Exception {
		long perm = -1;
		boolean hasAccess = false;
		String[] actionArray = actions.split(",");
		Type tabType = null;
		WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
		WebTabContext webTabContext = tabBean.getWebTab(tabId);
		if(webTabContext!=null) {
			tabType = webTabContext.getTypeEnum();
			if(tabType != null){
				for (String action : actionArray) {
					action = action.trim();
					long permVal = -1;

					permVal = V3PermissionUtil.getPermissionValueForActionAndTab(webTabContext, action);
					if (permVal > 0) {
						Map<String, Permission> permissionMap = AppModulePermissionUtil.getPermissionsMap();
						if (permissionMap.containsKey(action)) {
							if (permissionMap.get(action).getPermissionMapping().getIndex() == AppModulePermissionUtil.PermissionMapping.GROUP1PERMISSION.getIndex()) {
								perm = permission.getPermission();
							} else if (permissionMap.get(action).getPermissionMapping().getIndex() == AppModulePermissionUtil.PermissionMapping.GROUP2PERMISSION.getIndex()) {
								perm = permission.getPermission2();
							}
						}
						hasAccess = hasPermission(perm, permVal);
					}
					/*else {
						throw new Exception("Invalid permission type: "+action);
					}*/
					if (hasAccess) {
						break;
					}
				}
			}
			else {
				throw new Exception("Invalid Tab");
			}
		}

		return hasAccess;
	}

	public static boolean hasPermission(long perm,  String actions, long tabId) throws Exception {
		boolean hasAccess = false;
		String[] actionArray = actions.split(",");
		Type tabType = null;
		WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
		WebTabContext webTabContext = tabBean.getWebTab(tabId);
		if(webTabContext!=null) {
			tabType = webTabContext.getTypeEnum();
			if(tabType != null){
				for (String action : actionArray) {
					action = action.trim();
					long permVal = -1;

					permVal = NewPermissionUtil.getPermissionValue(tabType.getIndex(), action);
					if (permVal > 0) {
						hasAccess = hasPermission(perm, permVal);
					}
					/*else {
						throw new Exception("Invalid permission type: "+action);
					}*/
					if (hasAccess) {
						break;
					}
				}
			}
			else {
				throw new Exception("Invalid Tab");
			}
		}

		return hasAccess;
	}

	public static List<String> permCheckSysModules() {
		List<String> checkPermissions = Arrays.asList(
				"site",
				"building",
				"floor",
				"space",
				"vendors",
				"serviceRequest",
				"tenantunit",
				"visitorlog",
				"invitevisitor",
				"visitor",
				"tenant",
				"contactdirectory",
				"admindocuments",
				"audience",
				"announcement",
				"neighbourhood",
				"dealsandoffers",
				"newsandinformation",
				"facilitybooking",
				"tenantcontact",
				"vendorcontact",
				"insurance",
				"watchlist",
				"budget",
				"chartofaccount",
				"accounttype",
				"facility",
				"booking",
				"workorder",
				"asset"
		);
		return checkPermissions;
	}

}
