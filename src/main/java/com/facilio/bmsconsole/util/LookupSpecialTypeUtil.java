package com.facilio.bmsconsole.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.BeanFactory;

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
				|| FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE.equals(specialType)
				|| "trigger".equals(specialType)
				;
	}
	
	public static FacilioField getIdField (String specialType) {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || 
			FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return FieldFactory.getAsMap(AccountConstants.getOrgUserFields()).get("ouid");
		}
		else if (FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return FieldFactory.getAsMap(AccountConstants.getGroupFields()).get("groupId"); 
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			return FieldFactory.getAsMap(AccountConstants.getRoleFields()).get("roleId");
		}
		else {
			return FieldFactory.getIdField(getModule(specialType));
		}
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
			List<User> users = AccountUtil.getOrgBean().getRequesters(AccountUtil.getCurrentOrg().getOrgId());
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
				groupList.put(group.getGroupId(), group.getName());
			}
			return groupList;
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			List<Role> roles = AccountUtil.getRoleBean().getRoles(AccountUtil.getCurrentOrg().getId());
			return roles.stream().collect(Collectors.toMap(Role::getRoleId, Role::getName));
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
			return AccountUtil.getUserBean().getUser(id);
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
		else if("trigger".equals(specialType)) {
			Map<Long, List<PMTriggerContext>> pmMap = PreventiveMaintenanceAPI.getPMTriggers(Arrays.asList(id));
			if (pmMap != null && !pmMap.isEmpty()) {
				return pmMap.get(id).get(0);
			}
			return null;
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
			return AccountUtil.getUserBean().getUsers(criteria);
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return AccountUtil.getGroupBean().getGroups(criteria);
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			return AccountUtil.getRoleBean().getRoles(criteria);
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return null; //Returning null for now
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			return null; //Returning null for now
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventAPI.getEvent(criteria);
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
				groups.stream().collect(Collectors.toMap(Group::getId, Function.identity()));
			}
			return null;
		}
		else if (FacilioConstants.ContextNames.ROLE.equals(specialType)) {
			List<Role> roles = AccountUtil.getRoleBean().getRoles(ids);
			if (CollectionUtils.isNotEmpty(roles)) {
				roles.stream().collect(Collectors.toMap(Role::getId, Function.identity()));
			}
			return null;
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return null; //Returning null for now
		}
		else if(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE.equals(specialType)) {
			List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getPMsDetails(ids);
			if (CollectionUtils.isNotEmpty(pms)) {
				pms.stream().collect(Collectors.toMap(PreventiveMaintenance::getId, Function.identity()));
			}
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			List<EventContext> events = EventAPI.getEvents(ids);
			if (CollectionUtils.isNotEmpty(events)) {
				events.stream().collect(Collectors.toMap(EventContext::getId, Function.identity()));
			}
			return null;
		}
		return null;
	}
	
	public static List<? extends Object> getRecords (String specialType, Collection<Long> ids) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return AccountUtil.getUserBean().getUsers(null, ids);
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
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventAPI.getEvents(ids);
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
		else if ("trigger".equals(specialType)) {
			PMTriggerContext pmTriggerContext = new PMTriggerContext();
			pmTriggerContext.setId(id);
			return pmTriggerContext;
		}
		return null;
	}
	
	public static String getWhereClause(String specialType, FacilioField field, Criteria value) {
		return null;
	}
	
	public static Object getPrimaryFieldValue(String specialType, long id) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			User user = AccountUtil.getUserBean().getUser(id);
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
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			EventContext event = EventAPI.getEvent(id);
			if (event != null) {
				return event.toString();
			}
		}
		return null;
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
					idVsKey.put(businessHours.getId(), businessHours.toString());
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
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			for(Object obj:listObjects) {
				
				EventContext event = (EventContext)obj;
				if (event != null) {
					idVsKey.put(event.getId(), event.toString());
				}
			}
		}
		return idVsKey;
	}
	
	public static FacilioModule getModule(String specialType) {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return AccountConstants.getOrgUserModule();
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
			return ModuleFactory.getPreventiveMaintenancetModule();
		}
		else if(FacilioConstants.ContextNames.READING_RULE_MODULE.equals(specialType)) {
			return ModuleFactory.getReadingRuleModule();
		}
		else if(FacilioConstants.ContextNames.WORKFLOW_RULE_MODULE.equals(specialType)) {
			return ModuleFactory.getWorkflowRuleModule();
		}
		else if(FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE.equals(specialType)) {
			return ModuleFactory.getWorkOrderTemplateModule();
		}
		else if("trigger".equals(specialType)) {
			return ModuleFactory.getPMTriggersModule();
		}
		return null;
	}
	
	public static List<FacilioField> getAllFields(String specialType) {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			List<FacilioField> fields = AccountConstants.getUserFields();
			fields.addAll(AccountConstants.getOrgUserFields());
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
			return FieldFactory.getReadingRuleFields();
		}
		else if("trigger".equals(specialType)) {
			return FieldFactory.getPMTriggerFields();
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
