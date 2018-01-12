package com.facilio.bmsconsole.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;

public class LookupSpecialTypeUtil {
	public static boolean isSpecialType(String specialType) {
		return FacilioConstants.ContextNames.USERS.equals(specialType)
				|| FacilioConstants.ContextNames.GROUPS.equals(specialType)
				|| FacilioConstants.ContextNames.REQUESTER.equals(specialType)
				|| FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)
				|| EventConstants.EventContextNames.EVENT.equals(specialType)
				;
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
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return BusinessHoursAPI.getBusinessHours(id);
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventAPI.getEvent(id);
		}
		return null;
	}
	
	public static List getObjects(String specialType, Criteria criteria) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return AccountUtil.getUserBean().getUsers(criteria);
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return AccountUtil.getGroupBean().getGroups(criteria);
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return null; //Returning null for now
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventAPI.getEvent(criteria);
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
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			BusinessHoursList businessHours = new BusinessHoursList();
			businessHours.setId(id);
			return businessHours;
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			EventContext event = new EventContext();
			event.setId(id);
			return event;
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
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			BusinessHoursList businessHours = BusinessHoursAPI.getBusinessHours(id);
			if(businessHours != null) {
				return businessHours.toString();
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
	
	public static FacilioModule getModule(String specialType) {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return ModuleFactory.getOrgUserModule();
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
//			return null; //Group fields are yet to be moved to FieldFactory
			return null; //Groups are yet to be handled
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return ModuleFactory.getBusinessHoursModule();
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventConstants.EventModuleFactory.getEventModule();
		}
		return null;
	}
	
	public static List<FacilioField> getAllFields(String specialType) {
		if(FacilioConstants.ContextNames.USERS.equals(specialType) || FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			List<FacilioField> fields = FieldFactory.getUserFields();
			fields.addAll(FieldFactory.getOrgUserFields());
			return fields;
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return null; //Group fields are yet to be moved to FieldFactory
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return FieldFactory.getBusinessHoursFields();
		}
		else if(EventConstants.EventContextNames.EVENT.equals(specialType)) {
			return EventConstants.EventFieldFactory.getEventFields();
		}
		return null;
	}
}
