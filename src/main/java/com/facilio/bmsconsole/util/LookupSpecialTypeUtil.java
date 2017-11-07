package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.UserType;
import com.facilio.fw.OrgInfo;

public class LookupSpecialTypeUtil {
	public static boolean isSpecialType(String specialType) {
		return FacilioConstants.ContextNames.USERS.equals(specialType)
				|| FacilioConstants.ContextNames.GROUPS.equals(specialType)
				|| FacilioConstants.ContextNames.REQUESTER.equals(specialType)
				|| FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)
				;
	}
	
	public static Map<Long, String> getPickList(String specialType) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType)) {
			return UserAPI.getOrgUsers(OrgInfo.getCurrentOrgInfo().getOrgid(), UserType.USER.getValue());
		}
		else if(FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return UserAPI.getOrgUsers(OrgInfo.getCurrentOrgInfo().getOrgid(), UserType.REQUESTER.getValue());
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			List<GroupContext> groups = GroupAPI.getGroupsOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid(), true);
			Map<Long, String> groupList = new HashMap<>();
			for(GroupContext group : groups) {
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
		if(FacilioConstants.ContextNames.USERS.equals(specialType)) {
			return UserAPI.getUserFromOrgUserId(id);
		}
		else if(FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			return UserAPI.getUserFromOrgUserId(id);
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			return GroupAPI.getGroup(id);
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			return BusinessHoursAPI.getBusinessHours(id);
		}
		return null;
	}
	
	public static Object getEmptyLookedupObject(String specialType, long id) throws Exception {
		if(FacilioConstants.ContextNames.USERS.equals(specialType)) {
			UserContext user = new UserContext();
			user.setId(id);
			return user;
		}
		else if(FacilioConstants.ContextNames.REQUESTER.equals(specialType)) {
			UserContext requester = new UserContext();
			requester.setId(id);
			return requester;
		}
		else if(FacilioConstants.ContextNames.GROUPS.equals(specialType)) {
			GroupContext group = new GroupContext();
			group.setGroupId(id);
			return group;
		}
		else if(FacilioConstants.ContextNames.BUSINESS_HOUR.equals(specialType)) {
			BusinessHoursList businessHours = new BusinessHoursList();
			businessHours.setId(id);
			return businessHours;
		}
		return null;
	}
	
	public static String getWhereClause(String specialType, FacilioField field, Criteria value) {
		return null;
	}
}
