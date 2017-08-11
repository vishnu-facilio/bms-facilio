package com.facilio.bmsconsole.commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class LoadActionFormCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ActionForm actionForm = new ActionForm();
		actionForm.setGroupList(getGroupList());
		actionForm.setUserList(getUserList());
		actionForm.setAssetList(getAssetList());
		actionForm.setLocations(getLocations());
		
		context.put(FacilioConstants.ContextNames.ACTION_FORM, actionForm);
		
		return false;
	}
	
	private Map<Long, String> getGroupList() throws SQLException {
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		List<GroupContext> groups = GroupAPI.getGroupsOfOrg(orgId, true);
		Map<Long, String> groupList = new HashMap<>();
		if(groups != null && groups.size() > 0) {
			for(GroupContext group : groups) {
				groupList.put(group.getGroupId(), group.getName());
			}
		}
		return groupList;
	}
	
	private Map<Long, String> getUserList() throws SQLException {
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
//		List<UserContext> users = UserAPI.getUsersOfOrg(orgId);
//		Map<Long, String> userList = new HashMap<>();
//		if(users != null && users.size() > 0) {
//			for(UserContext user : users) {
//				userList.put(user.getOrgUserId(), user.getEmail());
//			}
//		}
//		return userList;
		return UserAPI.getOrgUsers(orgId);
	}
	
	private Map<Long, String> getAssetList() throws SQLException {
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		return AssetsAPI.getOrgAssets(orgId);
	}

	private Map<Long, String> getLocations() throws SQLException {
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		return LocationAPI.getLocations(orgId);
	}
}
