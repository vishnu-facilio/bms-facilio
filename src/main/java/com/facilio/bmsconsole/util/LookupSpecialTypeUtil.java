package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.bmsconsole.context.*;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.StringSystemEnumField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowCommitmentRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FieldOption;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

public class LookupSpecialTypeUtil {
	public static boolean isSpecialType(String specialType) {
		return FacilioConstants.ContextNames.USERS.equals(specialType)
				|| FacilioConstants.ContextNames.GROUPS.equals(specialType)
				|| FacilioConstants.ContextNames.ROLE.equals(specialType)
				|| FacilioConstants.ContextNames.REQUESTER.equals(specialType)
				|| FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)
				|| FacilioConstants.ContextNames.WORKFLOW_RULE_MODULE.equals(specialType)
				|| FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)
				|| EventConstants.EventContextNames.EVENT.equals(specialType)
				|| FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)
				|| ModuleFactory.getTaskTemplateModule().getName().equals(specialType)
				|| ModuleFactory.getTaskSectionTemplateModule().getName().equals(specialType)
				|| ModuleFactory.getTemplatesModule().getName().equals(specialType)
				|| ModuleFactory.getPMIncludeExcludeResourceModule().getName().equals(specialType)
				|| ModuleFactory.getPMTriggersModule().getName().equals(specialType)
				|| ModuleFactory.getPMResourcePlannerModule().getName().equals(specialType)
				|| ModuleFactory.getPMReminderModule().getName().equals(specialType)
				|| FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE.equals(specialType)
				|| FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)
				|| FacilioConstants.ContextNames.KPI.equals(specialType)
				|| FacilioConstants.ContextNames.READING_DATA_META.equals(specialType)
				|| FacilioConstants.ContextNames.CONTROL_GROUP.equals(specialType)
				|| FacilioConstants.ContextNames.TASK_SECTION_MODULE.equals(specialType)
				|| FacilioConstants.Workflow.WORKFLOW.equals(specialType)
				|| FacilioConstants.ContextNames.KPI_CATEGORY.equals(specialType)
				|| FacilioConstants.ModuleNames.PRINTERS.equals(specialType)
				|| ContextNames.SLA_RULE_MODULE.equals(specialType)
				|| FacilioConstants.ContextNames.PM_TRIGGER.equals(specialType)
				|| FacilioConstants.ContextNames.CONNECTED_APPS.equals(specialType)
				|| FacilioConstants.ContextNames.READING_TEMPLATE_MODULE.equals(specialType)
				|| ContextNames.AGENT_DATA.equals(specialType) || ContextNames.AGENT.equals(specialType)
				|| FacilioConstants.ContextNames.CONNECTED_APP_WIDGETS.equals(specialType)
				|| ModuleFactory.getNewAgentModule().getName().equals(specialType)
				|| ModuleFactory.getAgentDataModule().getName().equals(specialType)
				|| ModuleFactory.getTriggerModule().getName().equals(specialType)
				|| ModuleFactory.getNewReadingRuleModule().getName().equals(specialType)
				|| ModuleFactory.getPointModule().getName().equals(specialType)
				;
	}
	
	public static FacilioField getIdField (String specialType) {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || 
			FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return FieldFactory.getAsMap(AccountConstants.getAppOrgUserFields()).get("ouid");
		}
		else if (FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return FieldFactory.getAsMap(AccountConstants.getGroupFields()).get("id");
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			return FieldFactory.getAsMap(AccountConstants.getRoleFields()).get("roleId");
		}
		else {
			return FieldFactory.getIdField(getModule(specialType));
		}
	}

	private static List<FieldOption<Long>> getUserPickList (List<User> users) {
		return getUserPickList(users, true);
	}

	private static List<FieldOption<Long>> getUserPickList (List<User> users, boolean returnSorted) {
		List<FieldOption<Long>> userList = null;
		if (CollectionUtils.isNotEmpty(users)) {
			if (returnSorted) {
				userList = users.stream()
						.map(usr -> new FieldOption<>(usr.getId(), usr.getName()))
						.sorted(Comparator.comparing(FieldOption::getLabel))
						.collect(Collectors.toList());
			} else {
				userList = users.stream()
						.map(usr -> new FieldOption<>(usr.getId(), usr.getName()))
						.collect(Collectors.toList());
			}
		}
		return userList;
	}

	private static List<FieldOption<Long>> getRulePickList (List<WorkflowRuleContext> rules) {
		List<FieldOption<Long>> ruleList = null;
		if (CollectionUtils.isNotEmpty(rules)) {
			ruleList = rules.stream()
					.map(rule -> new FieldOption<>(rule.getId(), rule.getName()))
					.collect(Collectors.toList());
		}
		return ruleList;
	}

	private static List<FieldOption<Long>> getNewRulePickList (List<NewReadingRuleContext> rules) {
		List<FieldOption<Long>> ruleList = null;
		if (CollectionUtils.isNotEmpty(rules)) {
			ruleList = rules.stream()
					.map(rule -> new FieldOption<>(rule.getId(), rule.getName()))
					.collect(Collectors.toList());
		}
		return ruleList;
	}

		public static List<FieldOption<Long>> getNewPickList(String specialType,Map<String,Object> paramsMap) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType)) {
			List<User> users = ApplicationApi.getUsersList(paramsMap);
			boolean returnSorted = StringUtils.isEmpty(String.valueOf(paramsMap.get("orderBy")));
			return getUserPickList(users, returnSorted);
		}
		else if(FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			List<User> users = null;
			long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
			if(appId > 0) {
				users = ApplicationApi.getRequesterList(AccountUtil.getCurrentOrg().getOrgId(), appId, paramsMap);
			}
			boolean returnSorted = StringUtils.isEmpty(String.valueOf(paramsMap.get("orderBy")));
			return getUserPickList(users, returnSorted);
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			int page = 0,perPage = 5000,offset = 0;
			String search = null;
			String orderBy = null, orderType = null;
			List<Long> filteredSiteId = new ArrayList<>();
			if(!paramsMap.isEmpty()){
				page = (int) paramsMap.get("page");
				perPage = (int) paramsMap.get("perPage");
				search = (String) paramsMap.get("search");
				offset = ((page - 1) * perPage);
				if (offset < 0) {
					offset = 0;
				}
			}
			if(paramsMap.containsKey("filters")) {
				JSONObject filters = (JSONObject) paramsMap.get("filters");
				if (filters.containsKey("siteId")) {
					JSONObject values = (JSONObject) filters.get("siteId");
					if (values.containsKey("value")) {
						filteredSiteId = ((List<String>) values.get("value")).stream()
								.map(s -> Long.parseLong(String.valueOf(s)))
								.collect(Collectors.toList());
					}
				}
			}
			if (paramsMap.containsKey("orderType")) {
				FacilioField nameField = Constants.getModBean().getField("name", FacilioConstants.PeopleGroup.PEOPLE_GROUP);
				orderBy = nameField.getCompleteColumnName();
				orderType = (String) paramsMap.get("orderType");
			}
			List<Group> groups = AccountUtil.getGroupBean().getOrgGroups(AccountUtil.getCurrentOrg().getOrgId(), true,true,
					offset,perPage,search,filteredSiteId,orderBy,orderType);
			List<FieldOption<Long>> groupList = null;
			if (CollectionUtils.isNotEmpty(groups)) {
				groupList = groups.stream()
								.map(group -> new FieldOption<>(group.getId(), group.getName()))
								.collect(Collectors.toList());
			}
			return groupList;
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			List<Long> appIds = Collections.singletonList(AccountUtil.getCurrentApp().getId());
			if (paramsMap.containsKey("filters")) {
				JSONObject filter = (JSONObject) paramsMap.get("filters");
				if (filter != null && filter.containsKey("appLinkNames")) {
					List<String> appLinkNames = (List<String>) filter.get("appLinkNames");
					if (CollectionUtils.isNotEmpty(appLinkNames)) {
						List<ApplicationContext> apps = ApplicationApi.getApplicationForLinkNames(appLinkNames);
						if (CollectionUtils.isNotEmpty(apps)) {
							appIds = apps.stream().map(ApplicationContext::getId).collect(Collectors.toList());
						}
					}
				}
			}
			int page = 0, perPage = 5000, offset = 0;
			String orderBy = null, orderType = null;
			String search = null;
			if(!paramsMap.isEmpty()){
				page = (int) paramsMap.get("page");
				perPage = (int) paramsMap.get("perPage");
				search = (String) paramsMap.get("search");
				offset = ((page - 1) * perPage);
				if (offset < 0) {
					offset = 0;
				}
			}
			if (paramsMap.containsKey("orderType")) {
				FacilioField nameField = Constants.getModBean().getField("name", ContextNames.ROLE);
				orderBy = nameField.getCompleteColumnName();
				orderType = (String) paramsMap.get("orderType");
			}
			List<Role> roles = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getId()).getRolesForApps(appIds, perPage, offset, search, orderBy, orderType);
			List<FieldOption<Long>> roleList = null;
			if (CollectionUtils.isNotEmpty(roles)) {
				roleList = roles.stream()
						.map(role -> new FieldOption<>(role.getId(), role.getName()))
						.collect(Collectors.toList());
			}
			return roleList;
		}

		else if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)){
			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE)) {
				if (paramsMap.containsKey("orderType")) {
					ModuleBean moduleBean = Constants.getModBean();
					FacilioField primaryField = moduleBean.getPrimaryField(FacilioConstants.ReadingRules.NEW_READING_RULE);
					if (primaryField == null) {
						primaryField = FieldFactory.getIdField(moduleBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE));
					}
					paramsMap.put("orderBy", primaryField.getCompleteColumnName());
				}
				List<NewReadingRuleContext> readingRules = NewReadingRuleAPI.getAllRules(paramsMap);
				return getNewRulePickList(readingRules);
			}else{
				List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getAllWorkflowRuleContextOfType(WorkflowRuleContext.RuleType.READING_RULE, false,false);
				return getRulePickList(workflowRules);
			}
		}
		else if (ContextNames.SLA_RULE_MODULE.equals(specialType)) {
			List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getAllWorkflowRuleContextOfType(WorkflowRuleContext.RuleType.SLA_RULE, false,false);
			return getRulePickList(workflowRules);
		}
		else if(FacilioConstants.ContextNames.KPI_CATEGORY.equals(specialType)) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getKPICategoryFields())
					.table(ModuleFactory.getKPICategoryModule().getTableName());

			String search = (String) paramsMap.get("search");
			if(StringUtils.isNotEmpty(search)){
			selectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name",search, StringOperators.CONTAINS));
            }

			List<Map<String, Object>> props = selectBuilder.get();
			if (CollectionUtils.isNotEmpty(props)) {
				List<KPICategoryContext> kpiCategoryContext = FieldUtil.getAsBeanListFromMapList(props, KPICategoryContext.class);
				List<FieldOption<Long>> kpiCategoryList = kpiCategoryContext.stream()
														.map(kpi -> new FieldOption<>(kpi.getId(), kpi.getName()))
														.collect(Collectors.toList());
				return kpiCategoryList;
			}
		}
		else if(ContextNames.AGENT.equals(specialType)) {
			FacilioModule module = ModuleFactory.getNewAgentModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getNewAgentFields())
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(module), "NULL", CommonOperators.IS_EMPTY));

			String search = (String) paramsMap.get("search");
			if(StringUtils.isNotEmpty(search)){
				selectBuilder.andCondition(CriteriaAPI.getCondition("DISPLAY_NAME", "displayName",search, StringOperators.CONTAINS));
			}

			List<Map<String, Object>> props = selectBuilder.get();
			if (CollectionUtils.isNotEmpty(props)) {
				List<FacilioAgent> agentContext = FieldUtil.getAsBeanListFromMapList(props, FacilioAgent.class);
				List<FieldOption<Long>> agentList = agentContext.stream()
						.map(agent -> new FieldOption<>(agent.getId(), agent.getDisplayName()))
						.collect(Collectors.toList());
				return agentList;
			}
		}
		return null;
	}
	
	public static Map<Long, String> getPickList(String specialType) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType)) {
			List<User> users = AccountUtil.getOrgBean().getOrgUsers(AccountUtil.getCurrentOrg().getOrgId(), true);
			Map<Long, String> userMap = new HashMap<Long, String>();
			if (users != null) {
				for (User usr : users) {
					userMap.put(usr.getId(), usr.getName());
				}
			}
			return userMap;
		}
		else if(FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			List<User> users = null;
			long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
			if(appId > 0) {
				users = AccountUtil.getOrgBean().getOrgPortalUsers(AccountUtil.getCurrentOrg().getOrgId(), appId);
			}
			Map<Long, String> userMap = new HashMap<Long, String>();
			if (users != null) {
				for (User usr : users) {
					userMap.put(usr.getId(), usr.getName());
				}
			}
			return userMap;
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			List<Group> groups = AccountUtil.getGroupBean().getOrgGroups(AccountUtil.getCurrentOrg().getOrgId(), true);
			Map<Long, String> groupList = new HashMap<>();
			for(Group group : groups) {
				groupList.put(group.getId(), group.getName());
			}
			return groupList;
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			List<Role> roles = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getId()).getRolesForApps(Collections.singletonList(AccountUtil.getCurrentApp().getId()));
			return roles.stream().collect(Collectors.toMap(Role::getRoleId, Role::getName));
		}
		else if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)){
			List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getAllWorkflowRuleContextOfType(WorkflowRuleContext.RuleType.READING_RULE, false,false);
			if (workflowRules != null){
				return workflowRules.stream().collect(Collectors.toMap(WorkflowRuleContext::getId, WorkflowRuleContext::getName));
			}
		}
		else if (ContextNames.SLA_RULE_MODULE.equals(specialType)) {
			List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getAllWorkflowRuleContextOfType(WorkflowRuleContext.RuleType.SLA_RULE, false,false);
			if (workflowRules != null){
				return workflowRules.stream().collect(Collectors.toMap(WorkflowRuleContext::getId, WorkflowRuleContext::getName));
			}
		}
		else if(FacilioConstants.ContextNames.KPI_CATEGORY.equals(specialType)) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getKPICategoryFields())
					.table(ModuleFactory.getKPICategoryModule().getTableName());

			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<KPICategoryContext> kpiCategoryContext = FieldUtil.getAsBeanListFromMapList(props, KPICategoryContext.class);
				Map<Long, String> kpiCategoryList = new HashMap<>();
				
				for(KPICategoryContext kpiCategory: kpiCategoryContext) {
					kpiCategoryList.put(kpiCategory.getId(), kpiCategory.getName());
				}
				
				return kpiCategoryList;
			}
		}
		return null;
	}
	
	public static String getLookupIcon(String specialType) {
		if (FacilioConstants.ContextNames.USERS.equalsIgnoreCase(specialType)) {
			return "fa fa-user";
		}
		else if (FacilioConstants.ContextNames.GROUPS.equalsIgnoreCase(specialType)) {
			return "fa fa-users";
		}
		else {
			return "fa fa-search";
		}
	}
	
	public static Object getLookedupObject(String specialType, long id) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return AccountUtil.getUserBean().getUser(id, true);
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return AccountUtil.getGroupBean().getGroup(id);
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			return AccountUtil.getRoleBean().getRole(id);
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return BusinessHoursAPI.getBusinessHours(id);
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			return PreventiveMaintenanceAPI.getPM(id, false);
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventAPI.getEvent(id);
		}
		else if(FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)) {
			return FormulaFieldAPI.getFormulaField(id);
		}
		else if(FacilioConstants.Workflow.WORKFLOW.equals(specialType)) {
			return WorkflowUtil.getWorkflowContext(id);
		}
		else if (ContextNames.SLA_RULE_MODULE.equals(specialType)) {
			return WorkflowRuleAPI.getWorkflowRule(id);
		}
		else if(ModuleFactory.getPMTriggersModule().getName().equals(specialType) || FacilioConstants.ContextNames.PM_TRIGGER.equals(specialType)) {
			Map<Long, List<PMTriggerContext>> pmMap = PreventiveMaintenanceAPI.getPMTriggers(Arrays.asList(id));
			if (pmMap != null && !pmMap.isEmpty()) {
				return pmMap.get(id).get(0);
			}
			return null;
		}
		else if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
            return WorkflowRuleAPI.getWorkflowRule(id, false, false);
        } else if (ContextNames.AGENT_DATA.equals(specialType) || ContextNames.AGENT.equals(specialType)) {
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            return agentBean.getAgent(id);
        } else if (FacilioConstants.ContextNames.SENSOR_RULE_MODULE.equals(specialType)) {
            return SensorRuleUtil.getSensorRuleByIds(Collections.singletonList(id));
        }
		return null;
	}
	
	public static Map<Long, Object> getPickList(String specialType, List<Long> idList) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(getIdField(specialType), idList, PickListOperators.IS));
		List<Object> list = getObjects(specialType,criteria);
		return getPrimaryFieldValues(specialType, list);
	}
	
	
	public static List getObjects(String specialType, Criteria criteria) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return AccountUtil.getUserBean().getUsers(criteria, false, true);
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return AccountUtil.getGroupBean().getGroups(criteria);
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			return AccountUtil.getRoleBean().getRoles(criteria);
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return BusinessHoursAPI.getBusinessHours(criteria);
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			return PreventiveMaintenanceAPI.getPM(criteria);
		}
		else if(FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)) {
			return FormulaFieldAPI.getFormulaFields(criteria);
		}
		else if(FacilioConstants.Workflow.WORKFLOW.equals(specialType)) {
			return WorkflowUtil.getWorkflowContext(criteria);
		}
		else if(FacilioConstants.ContextNames.TASK_SECTION_MODULE.equals(specialType)) {
			return TicketAPI.getTaskSections(criteria);
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventAPI.getEvent(criteria);
		}
        else if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE)) {
                return NewReadingRuleAPI.getAllRules(null);
            }else{
                return ReadingRuleAPI.getReadingRules(criteria);
            }
        }
        else if (FacilioConstants.ContextNames.WORKFLOW_RULE_MODULE.equals(specialType)) {
            return WorkflowRuleAPI.getWorkflowRules(WorkflowRuleContext.RuleType.READING_RULE, true ,criteria, null, null);
        }
		else if (ContextNames.SLA_RULE_MODULE.equals(specialType)) {
			return WorkflowRuleAPI.getWorkflowRules(WorkflowRuleContext.RuleType.SLA_RULE, true, criteria, null, null);
		}
		else if (ModuleFactory.getPMTriggersModule().getName().equals(specialType) || FacilioConstants.ContextNames.PM_TRIGGER.equals(specialType)) {
			return PreventiveMaintenanceAPI.getPMTriggers(criteria);
		}
		else if(ContextNames.AGENT.equals(specialType)) {
			AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
			return agentBean.getAgents(criteria);
		}
		else if(ContextNames.POINTS.equals(specialType)) {
			return PointsAPI.getPoints(criteria);
		}
		return null;
	}
	
	public static Map<Long, ? extends Object> getRecordsAsMap (String specialType, Collection<Long> ids) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return AccountUtil.getUserBean().getUsersAsMap(null, ids);
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			List<Group> groups = AccountUtil.getGroupBean().getGroups(ids);
			if (CollectionUtils.isNotEmpty(groups)) {
				return groups.stream().collect(Collectors.toMap(Group::getId, Function.identity()));
			}
			return null;
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			List<Role> roles = AccountUtil.getRoleBean().getRoles(ids);
			if (CollectionUtils.isNotEmpty(roles)) {
				return roles.stream().collect(Collectors.toMap(Role::getId, Function.identity()));
			}
			return null;
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return null; //Returning null for now
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI. getPMs(ids, null, null, null, null);
			if (CollectionUtils.isNotEmpty(pms)) {
				return pms.stream().collect(Collectors.toMap(PreventiveMaintenance::getId, Function.identity()));
			}
		}
		else if(FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)) {
			List<FormulaFieldContext> formulaFieldContexts = FormulaFieldAPI.getFormulaFields(ids);
			if (CollectionUtils.isNotEmpty(formulaFieldContexts)) {
				return formulaFieldContexts.stream().collect(Collectors.toMap(FormulaFieldContext::getId, Function.identity()));
			}
		}
		else if(FacilioConstants.Workflow.WORKFLOW.equals(specialType)) {
			return WorkflowUtil.getWorkflowsAsMap(ids, false);
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			List<EventContext> events = EventAPI.getEvents(ids);
			if (CollectionUtils.isNotEmpty(events)) {
				return events.stream().collect(Collectors.toMap(EventContext::getId, Function.identity()));
			}
			return null;
		}
		else if (ModuleFactory.getPMTriggersModule().getName().equals(specialType) || FacilioConstants.ContextNames.PM_TRIGGER.equals(specialType)) {
			List<PMTriggerContext> triggers = PreventiveMaintenanceAPI.getPMTriggersByTriggerIds(ids);
			if (CollectionUtils.isNotEmpty(triggers)) {
				return triggers.stream().collect(Collectors.toMap(PMTriggerContext::getId, Function.identity()));
			}
		}
		else if (ContextNames.SLA_RULE_MODULE.equals(specialType)) {
			if (!(ids instanceof List)) {
				ids = new ArrayList<>(ids);
			}
			List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getWorkflowRules((List<Long>) ids, true, true);
			if (CollectionUtils.isNotEmpty(workflowRules)) {
				return workflowRules.stream().collect(Collectors.toMap(WorkflowRuleContext::getId, Function.identity()));
            }
        } else if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
            if (!(ids instanceof List)) {
                ids = new ArrayList<>(ids);
            }
            if (CollectionUtils.isNotEmpty(ids)) {
                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE)) {
                    List<NewReadingRuleContext> newReadingRules = NewReadingRuleAPI.getReadingRules((List<Long>) ids);
                    return newReadingRules.stream().collect(Collectors.toMap(NewReadingRuleContext::getId, Function.identity()));
                }
                List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getWorkflowRules((List<Long>) ids, false, true);
                if (CollectionUtils.isNotEmpty(workflowRules)) {
                    return workflowRules.stream().collect(Collectors.toMap(WorkflowRuleContext::getId, Function.identity()));
                }
            }
        } else if (FacilioConstants.ContextNames.SENSOR_RULE_MODULE.equals(specialType)) {
            List<SensorRuleContext> sensorRules = SensorRuleUtil.getSensorRuleByIds((List<Long>) ids);
            if (CollectionUtils.isNotEmpty(sensorRules)) {
                return sensorRules.stream().collect(Collectors.toMap(SensorRuleContext::getId, Function.identity()));
            }
        } else if (ContextNames.AGENT.equals(specialType)) {
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            List<FacilioAgent> agents = agentBean.getAgents(ids);
            if (CollectionUtils.isNotEmpty(agents)) {
                return agents.stream().collect(Collectors.toMap(FacilioAgent::getId, Function.identity()));
            }
        }
		return null;
	}
	
	public static List<? extends Object> getRecords (String specialType, Collection<Long> ids) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return AccountUtil.getUserBean().getUsers(null, false, true, ids);
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return AccountUtil.getGroupBean().getGroups(ids);
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			return AccountUtil.getRoleBean().getRoles(ids);
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return null; //Returning null for now
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			return PreventiveMaintenanceAPI.getPMsDetails(ids);
		}
		else if(FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)) {
			return FormulaFieldAPI.getFormulaFields(ids);
		}
		else if(FacilioConstants.Workflow.WORKFLOW.equals(specialType)) {
			return new ArrayList<>(WorkflowUtil.getWorkflowsAsMap(ids, false).values());
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventAPI.getEvents(ids);
		}
		else if (ModuleFactory.getPMTriggersModule().getName().equals(specialType) || FacilioConstants.ContextNames.PM_TRIGGER.equals(specialType)) {
			return PreventiveMaintenanceAPI.getPMTriggersByTriggerIds(ids);
		}
		else if (ContextNames.SLA_RULE_MODULE.equals(specialType)) {
			return WorkflowRuleAPI.getWorkflowRules((List<Long>) ids);

        } else if ((FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType))) {
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE)) {
                return NewReadingRuleAPI.getReadingRules((List<Long>) ids);
            }
            return WorkflowRuleAPI.getWorkflowRules((List<Long>) ids, false, true);
        } else if (FacilioConstants.ContextNames.SENSOR_RULE_MODULE.equals(specialType)) {
            return SensorRuleUtil.getSensorRuleByIds((List<Long>) ids);
        } else if (ContextNames.AGENT.equals(specialType)) {
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            return agentBean.getAgents(ids);
        }
		return null;
	}
	
	public static Object getEmptyLookedupObject(String specialType, long id) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType)) {
			User user = new User();
			user.setOuid(id);
			return user;
		}
		else if(FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			User user = new User();
			user.setOuid(id);
			return user;
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			Group group = new Group();
			group.setGroupId(id);
			group.setId(id);
			return group;
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			Role role = new Role();
			role.setRoleId(id);;
			return role;
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			BusinessHoursList businessHours = new BusinessHoursList();
			businessHours.setId(id);
			return businessHours;
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			PreventiveMaintenance pm = new PreventiveMaintenance();
			pm.setId(id);
			return pm;
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			EventContext event = new EventContext();
			event.setId(id);
			return event;
		}
		else if(FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)) {
			FormulaFieldContext formulaFieldContext = new FormulaFieldContext();
			formulaFieldContext.setId(id);
			return formulaFieldContext;
		}
		else if(FacilioConstants.Workflow.WORKFLOW.equals(specialType)) {
			WorkflowContext workflowContext = new WorkflowContext();
			workflowContext.setId(id);
			return workflowContext;
		}
		else if (ModuleFactory.getPMTriggersModule().getName().equals(specialType) || FacilioConstants.ContextNames.PM_TRIGGER.equals(specialType)) {
			PMTriggerContext pmTriggerContext = new PMTriggerContext();
			pmTriggerContext.setId(id);
			return pmTriggerContext;
		}
		else if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
			ReadingRuleContext readingRuleContext = new ReadingRuleContext();
			readingRuleContext.setId(id);
			return readingRuleContext;
		}
		else if (ContextNames.SLA_RULE_MODULE.equals(specialType)) {
			SLAWorkflowCommitmentRuleContext slaWorkflowCommitmentRuleContext = new SLAWorkflowCommitmentRuleContext();
			slaWorkflowCommitmentRuleContext.setId(id);
			return slaWorkflowCommitmentRuleContext;
		}
		else if (FacilioConstants.ContextNames.SENSOR_RULE_MODULE.equals(specialType)) {
			SensorRuleContext sensorRuleContext = new SensorRuleContext();
			sensorRuleContext.setId(id);
			return sensorRuleContext;
		}
		return null;
	}

	public static long getLookupObjectId (String specialType, Object obj) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType)) {
			return ((User) obj).getId();
		}
		else if(FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return ((User) obj).getId();
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return ((Group) obj).getId();
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			return ((Role) obj).getId();
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return ((BusinessHoursList) obj).getId();
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			return ((PreventiveMaintenance) obj).getId();
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return ((EventContext) obj).getId();
		}
		else if(FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)) {
			return ((FormulaFieldContext) obj).getId();
		}
		else if(FacilioConstants.Workflow.WORKFLOW.equals(specialType)) {
			return ((WorkflowContext) obj).getId();
		}
		else if (ModuleFactory.getPMTriggersModule().getName().equals(specialType) || FacilioConstants.ContextNames.PM_TRIGGER.equals(specialType)) {
			return ((PMTriggerContext) obj).getId();
		}
		else if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
			return ((ReadingRuleContext) obj).getId();
		}
		else if (FacilioConstants.ContextNames.SENSOR_RULE_MODULE.equals(specialType)) {
			return ((SensorRuleContext) obj).getId();
		}
		return -1;
	}

	public static String getWhereClause(String specialType, FacilioField field, Criteria value) {
		return null;
	}
	
	public static Object getPrimaryFieldValue(String specialType, long id) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			User user = AccountUtil.getUserBean().getUser(id, true);
			if(user != null) {
				return user.getName();
			}
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			Group group = AccountUtil.getGroupBean().getGroup(id);
			if(group != null) {
				return group.getName();
			}
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			Role role = AccountUtil.getRoleBean().getRole(id);
			if (role != null) {
				return role.getName();
			}
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			BusinessHoursList businessHours = BusinessHoursAPI.getBusinessHours(id);
			if(businessHours != null) {
				return businessHours.toString();
			}
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			PreventiveMaintenance pm = PreventiveMaintenanceAPI.getPM(id, false);
			if (pm != null) {
				return pm.getTitle();
			}
		}
		else if(FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)) {
			FormulaFieldContext formulaFieldContext = FormulaFieldAPI.getFormulaField(id);
			if (formulaFieldContext != null) {
				return formulaFieldContext.getName();
			}
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			EventContext event = EventAPI.getEvent(id);
			if (event != null) {
				return event.toString();
			}
		}
        else if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType) || ContextNames.SLA_RULE_MODULE.equals(specialType)) {
            WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(id);
            if (rule != null) {
                return  rule.toString();
            }
        }
        else if(FacilioConstants.ContextNames.SENSOR_RULE_MODULE.equals(specialType)) {
			List<SensorRuleContext> sensorRule = SensorRuleUtil.getSensorRuleByIds(Collections.singletonList(id));
			if (sensorRule != null && sensorRule.get(0) != null) {
				return sensorRule.toString();
			}
		}
		return null;
	}

	public static Object getPrimaryFieldValue(String specialType, Object obj) throws Exception {
		Objects.requireNonNull(obj, "Lookup special object cannot be null for getting primary field value");
		switch (specialType) {
			case FacilioConstants.ContextNames.USERS:
				return ((User) obj).getName();
			case FacilioConstants.ContextNames.GROUPS:
				return ((Group) obj).getName();
			case FacilioConstants.ContextNames.ROLE:
				return ((Role) obj).getName();
			case FacilioConstants.ContextNames.BUSINESS_HOUR:
				return ((BusinessHoursList) obj).getName();
			case FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE:
				return ((PreventiveMaintenance) obj).getTitle();
			case FacilioConstants.ContextNames.FORMULA_FIELD:
				return ((FormulaFieldContext) obj).getName();
			case EventConstants.EventContextNames.EVENT:
				return ((EventContext) obj).getEventMessage();
			case FacilioConstants.ContextNames.READING_RULE_MODULE:
			case FacilioConstants.ContextNames.SLA_RULE_MODULE:
				return ((WorkflowRuleContext) obj).getName();
			case FacilioConstants.ContextNames.SENSOR_RULE_MODULE:
				return ((SensorRuleContext) obj).getSensorRuleTypeEnum().name();
			default:
				return null;
		}
	}

	public static Object getPrimaryFieldValue(String specialType, Map<String, Object> prop) {
		if (MapUtils.isEmpty(prop)) {
			return null;
		}
		String primaryFieldName = getPrimaryFieldName(specialType);
		return primaryFieldName != null ? prop.get(primaryFieldName) : null;
	}
	
	// TODO IsMainField property should be set in field object itself and return primary field using that
	public static String getPrimaryFieldName(String specialType) {
		switch (specialType) {
			case FacilioConstants.ContextNames.USERS:
			case FacilioConstants.ContextNames.GROUPS:
			case FacilioConstants.ContextNames.ROLE:
			case FacilioConstants.ContextNames.BUSINESS_HOUR:
			case FacilioConstants.ContextNames.FORMULA_FIELD:
			case FacilioConstants.ContextNames.READING_RULE_MODULE:
			case FacilioConstants.ContextNames.SLA_RULE_MODULE:
				return "name";
			case FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE:
				return "title";
			case EventConstants.EventContextNames.EVENT:
				return "eventMessage";
			case FacilioConstants.ContextNames.SENSOR_RULE_MODULE:
				return "sensorRuleTypeEnum";
			default:
				return null;
		}
	}
	
	public static Map<Long,Object> getPrimaryFieldValues(String specialType, List<Object> listObjects) throws Exception {
		
		if(listObjects==null) {
			return null;
		}
		
		Map<Long,Object> idVsKey= new HashMap<Long,Object>();
		
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			
			for(Object obj:listObjects) {
				User user = (User)obj;
				if(user != null) {
					idVsKey.put(user.getId(),user.getName());
				}

			}
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			
			for(Object obj:listObjects) {
				Group group = (Group)obj;
				if(group != null) {
					idVsKey.put(group.getId(), group.getName());
				}
			}
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			return listObjects.stream().collect(Collectors.toMap(r -> ((Role) r).getId(), r -> ((Role) r).getName()));
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			
			for(Object obj:listObjects) {
				BusinessHoursList businessHours = (BusinessHoursList)obj;
				if(businessHours != null) {
					idVsKey.put(businessHours.getId(), businessHours.getName());
				}
			}
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			
			for(Object obj:listObjects) {
				PreventiveMaintenance pm = (PreventiveMaintenance)obj;
				if(pm != null) {
					idVsKey.put(pm.getId(), pm.getTitle());
				}
			}
		}
		else if(FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)) {
			for(Object obj:listObjects) {
				FormulaFieldContext formulaFieldContext = (FormulaFieldContext)obj;
				if(formulaFieldContext != null) {
					idVsKey.put(formulaFieldContext.getId(), formulaFieldContext.getName());
				}
			}
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			for(Object obj:listObjects) {
				
				EventContext event = (EventContext)obj;
				if (event != null) {
					idVsKey.put(event.getId(), event.getEventMessage());
				}
			}
		}
        else if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
            for(Object obj:listObjects) {
				if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE)) {
					NewReadingRuleContext newReadingRule = (NewReadingRuleContext) obj;
					if (newReadingRule != null) {
						idVsKey.put(newReadingRule.getId(), newReadingRule.getName());
					}
				}else{
					WorkflowRuleContext workflowRule = (WorkflowRuleContext) obj;
					if (workflowRule != null) {
						idVsKey.put(workflowRule.getId(), workflowRule.getName());
					}
				}
            }
        }
		else if (ContextNames.SLA_RULE_MODULE.equals(specialType)) {
			for(Object obj:listObjects) {

				WorkflowRuleContext workflowRule = (WorkflowRuleContext) obj;
				if (workflowRule != null) {
					idVsKey.put(workflowRule.getId(), workflowRule.getName());
				}
			}
		}
        else if (FacilioConstants.ContextNames.SENSOR_RULE_MODULE.equals(specialType)) {
            for(Object obj:listObjects) {

            	SensorRuleContext sensorRuleContext = (SensorRuleContext) obj;
                if (sensorRuleContext != null) {
                    idVsKey.put(sensorRuleContext.getId(), sensorRuleContext.getSensorRuleTypeEnum().name());
                }
            }
        }
		else if (ContextNames.AGENT.equals(specialType)) {
			for (Object obj : listObjects) {
				FacilioAgent agent = (FacilioAgent) obj;
				if (agent != null) {
					idVsKey.put(agent.getId(), agent.getDisplayName());
				}
			}
		}
		return idVsKey;
	}
	
	public static FacilioModule getModule(String specialType) {
		if(FacilioConstants.ContextNames.USERS.equals(specialType)) {
			return AccountConstants.getAppOrgUserModule();
		}
		else if (FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			FacilioModule module = AccountConstants.getAppOrgUserModule();
			module.setName(specialType);
			module.setDisplayName("Requesters");
			return module;
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return AccountConstants.getGroupModule();
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			return AccountConstants.getRoleModule();
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return ModuleFactory.getBusinessHoursModule();
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventConstants.EventModuleFactory.getEventModule();
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			return ModuleFactory.getPreventiveMaintenanceModule();
		}
		else if(FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
			return ModuleFactory.getReadingRuleModule();
		}
		else if(FacilioConstants.ContextNames.NEW_READING_RULE_MODULE.equals(specialType)) {
			return ModuleFactory.getNewReadingRuleModule();
		}
//		else if (ContextNames.SLA_RULE_MODULE.equals(specialType)) {
//			return ModuleFactory.getSLAWorkflowRuleModule();
//		}
		else if(FacilioConstants.ContextNames.WORKFLOW_RULE_MODULE.equals(specialType)) {
			return ModuleFactory.getWorkflowRuleModule();
		}
		else if(FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE.equals(specialType)) {
			return ModuleFactory.getWorkOrderTemplateModule();
		}
		else if(FacilioConstants.ContextNames.TASK_SECTION_MODULE.equals(specialType)) {
			return ModuleFactory.getTaskSectionModule();
		}
		else if(FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)) {
			return ModuleFactory.getFormulaFieldModule();
		}
		else if(FacilioConstants.ContextNames.READING_DATA_META.equals(specialType)) {
			return ModuleFactory.getReadingDataMetaModule();
		}
		else if(FacilioConstants.ContextNames.CONTROL_GROUP.equals(specialType)) {
			return ModuleFactory.getControlGroupModule();
		}
		else if(FacilioConstants.Workflow.WORKFLOW.equals(specialType)) {
			return ModuleFactory.getWorkflowModule();
		}
		else if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
            return ModuleFactory.getReadingRuleModule();
        }
		else if(FacilioConstants.ContextNames.CONNECTED_APPS.equals(specialType)) {
			return ModuleFactory.getConnectedAppsModule();
		}
		else if(FacilioConstants.ContextNames.CONNECTED_APP_WIDGETS.equals(specialType)) {
			return ModuleFactory.getConnectedAppWidgetsModule();
		}
		else if(ContextNames.PRINTERS.equals(specialType)) {
			return ModuleFactory.getPrinterModule();
		}
		else if(ContextNames.READING_TEMPLATE_MODULE.equals(specialType)) {
			return ModuleFactory.getTemplateModule();
		}
		else if(ContextNames.AGENT_DATA.equals(specialType) || ContextNames.AGENT.equals(specialType)){
			return ModuleFactory.getNewAgentModule();
		}
		else if(ContextNames.KPI.equals(specialType)){
			return ModuleFactory.getKpiModule();
		}
		else if(ContextNames.SENSOR_RULE_MODULE.equals(specialType)){
			return ModuleFactory.getSensorRuleModule();
		}
		else if(ModuleFactory.getTaskTemplateModule().getName().equals(specialType)){
			return ModuleFactory.getTaskTemplateModule();
		}
		else if(ModuleFactory.getTaskSectionTemplateModule().getName().equals(specialType)){
			return ModuleFactory.getTaskSectionTemplateModule();
		}
		else if(ModuleFactory.getTemplatesModule().getName().equals(specialType)){
			return ModuleFactory.getTemplatesModule();
		}
		else if(ModuleFactory.getPMIncludeExcludeResourceModule().getName().equals(specialType)){
			return ModuleFactory.getPMIncludeExcludeResourceModule();
		}
		else if(ModuleFactory.getPMTriggersModule().getName().equals(specialType) || FacilioConstants.ContextNames.PM_TRIGGER.equals(specialType)){
			return ModuleFactory.getPMTriggersModule();
		}
		else if(ModuleFactory.getPMResourcePlannerModule().getName().equals(specialType)){
			return ModuleFactory.getPMResourcePlannerModule();
		}
		else if(ModuleFactory.getPMReminderModule().getName().equals(specialType)){
			return ModuleFactory.getPMReminderModule();
		}
		else if(ModuleFactory.getNewAgentModule().getName().equals(specialType)){
			return ModuleFactory.getNewAgentModule();
		}
		else if(ModuleFactory.getAgentDataModule().getName().equals(specialType)){
			return ModuleFactory.getAgentDataModule();
		}
		else if(ModuleFactory.getTriggerModule().getName().equals(specialType)){
			return ModuleFactory.getTriggerModule();
		}
		else if(ModuleFactory.getPointModule().getName().equals(specialType)){
			return ModuleFactory.getPointModule();
		}
		return null;
	}
	
	public static List<FacilioField> getAllFields(String specialType) {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			List<FacilioField> fields =AccountConstants.getAppOrgUserFields();
			return fields;
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return AccountConstants.getGroupFields();
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			return AccountConstants.getRoleFields();
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return FieldFactory.getBusinessHoursFields();
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventConstants.EventFieldFactory.getEventFields();
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			return FieldFactory.getPreventiveMaintenanceFields();
		}
		else if(FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE.equals(specialType)) {
			return FieldFactory.getWorkOrderTemplateFields();
		} 
		else if(FacilioConstants.ContextNames.WORKFLOW_RULE_MODULE.equals(specialType)) {
			return FieldFactory.getWorkflowRuleFields();
		}
		else if(FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
			List<FacilioField> fields = FieldFactory.getReadingRuleFields();
			fields.addAll(FieldFactory.getWorkflowRuleFields());
			for(FacilioField field: fields) {
				if(field.getDataTypeEnum() == FieldType.SYSTEM_ENUM && field instanceof SystemEnumField) {
					SystemEnumField enumField = (SystemEnumField) field;
					enumField.setValues(FacilioEnum.getEnumValues(enumField.getEnumName()));
				}
				else if(field.getDataTypeEnum() == FieldType.STRING_SYSTEM_ENUM && field instanceof StringSystemEnumField) {
					StringSystemEnumField systemEnumField = (StringSystemEnumField) field;
					systemEnumField.setValues(FacilioEnum.getEnumValues(systemEnumField.getEnumName()));
				}
			}
			return fields;
		}
//		else if (ContextNames.SLA_RULE_MODULE.equals(specialType)) {
//			List<FacilioField> fields = FieldFactory.getSLAWorkflowRuleFields();
//			fields.addAll(FieldFactory.getWorkflowRuleFields());
//			return fields;
//		}
		else if(FacilioConstants.ContextNames.TASK_SECTION_MODULE.equals(specialType)) {
			return FieldFactory.getTaskSectionFields();
		}
		else if(FacilioConstants.ContextNames.FORMULA_FIELD.equals(specialType)) {
			return FieldFactory.getFormulaFieldFields();
		}
		else if(FacilioConstants.ContextNames.READING_DATA_META.equals(specialType)) {
			return FieldFactory.getReadingDataMetaFields();
		}
		else if(FacilioConstants.ContextNames.CONTROL_GROUP.equals(specialType)) {
			return FieldFactory.getControlGroupFields();
		}
		else if(FacilioConstants.Workflow.WORKFLOW.equals(specialType)) {
			return FieldFactory.getWorkflowFields();
		}
		else  if (FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
		    return  FieldFactory.getReadingRuleFields();
        }
		else if (ContextNames.NEW_READING_RULE_MODULE.equals(specialType)) {
			return FieldFactory.getNewReadingRuleFields();
		}
		else  if (FacilioConstants.ContextNames.PRINTERS.equals(specialType)) {
		    return  FieldFactory.getPrinterFields();
        }
		else  if (FacilioConstants.ContextNames.CONNECTED_APPS.equals(specialType)) {
		    return  FieldFactory.getConnectedAppFields();
        }
		else  if (FacilioConstants.ContextNames.CONNECTED_APP_WIDGETS.equals(specialType)) {
		    return  FieldFactory.getConnectedAppWidgetsFields();
        }
		
		else if(FacilioConstants.ContextNames.KPI.equals(specialType)) {
			return FieldFactory.getKPIFields();
		}
		else if(ContextNames.AGENT_DATA.equals(specialType) || ContextNames.AGENT.equals(specialType)) {
			return FieldFactory.getNewAgentFields();
		}
		else  if (FacilioConstants.ContextNames.SENSOR_RULE_MODULE.equals(specialType)) {
		    return  FieldFactory.getSensorRuleFields();
        }
		else if(ModuleFactory.getTaskTemplateModule().getName().equals(specialType)){
			return FieldFactory.getTaskTemplateFields();
		}
		else if(ModuleFactory.getTaskSectionTemplateModule().getName().equals(specialType)){
			return FieldFactory.getTaskSectionTemplateFields();
		}
		else if(ModuleFactory.getTemplatesModule().getName().equals(specialType)){
			return FieldFactory.getTemplateFields();
		}
		else if(ModuleFactory.getPMIncludeExcludeResourceModule().getName().equals(specialType)){
			return FieldFactory.getPMIncludeExcludeResourceFields();
		}
		else if(ModuleFactory.getPMTriggersModule().getName().equals(specialType) || FacilioConstants.ContextNames.PM_TRIGGER.equals(specialType)){
			return FieldFactory.getPMTriggerFields();
		}
		else if(ModuleFactory.getPMResourcePlannerModule().getName().equals(specialType)){
			return FieldFactory.getPMResourcePlannerFields();
		}
		else if(ModuleFactory.getPMReminderModule().getName().equals(specialType)){
			return FieldFactory.getPMReminderFields();
		}
		else if(ModuleFactory.getPointModule().getName().equals(specialType)){
			return FieldFactory.getPointFields();
		}
		return null;
	}
	
	public static FacilioField getField(String fieldName, String specialType) {
		List<FacilioField> fields = getAllFields(specialType);
		if (fields != null && !fields.isEmpty()) {
			return FieldFactory.getAsMap(fields).get(fieldName);
		}
		return null;
	}
	
	public static List<FacilioModule> getAllSubModules (String moduleName) throws Exception {
		if (moduleName != null && !moduleName.isEmpty()) {
			switch (moduleName) {
				case FacilioConstants.ContextNames.USERS:
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					List<FacilioModule> modules = new ArrayList<>();
					modules.add(modBean.getModule(FacilioConstants.ContextNames.USER_SHIFT_READING));
					modules.add(modBean.getModule(FacilioConstants.ContextNames.USER_WORK_HOURS_READINGS));
					return modules;
				default:
					break;
			}
		}
		return null;
	}
	
	public static List<FacilioModule> getSubModules (String moduleName, FacilioModule.ModuleType... types) throws Exception {
		if (moduleName != null && !moduleName.isEmpty()) {
			switch (moduleName) {
				case FacilioConstants.ContextNames.USERS:
					return getAllSubModules(moduleName);
				default:
					break;
			}
		}
		return null;
	}
}
