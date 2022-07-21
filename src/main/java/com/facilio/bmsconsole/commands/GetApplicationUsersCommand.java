package com.facilio.bmsconsole.commands;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;

public class GetApplicationUsersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long appId = (Long)context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		Boolean getCount = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		Boolean inviteAcceptStatus = (Boolean)context.get(FacilioConstants.ContextNames.INVITE_ACCEPT_STATUS);
		Boolean userStatus = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.USER_STATUS,true);
		int page, perPage = -1, offset = -1;
		String searchQuery = (String) context.get(FacilioConstants.ContextNames.SEARCH);
		
		if (pagination != null) {
			page = (int) pagination.get("page");
			perPage = (int) pagination.get("perPage");

			offset = ((page - 1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
		}
		
		boolean fetchNonAppUsers= (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_NON_APP_USERS, false);
		if(getCount == null || !getCount) {
			List<User> users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), appId, -1, false, fetchNonAppUsers, offset, perPage, searchQuery, inviteAcceptStatus,userStatus,null,null,null);
			context.put(FacilioConstants.ContextNames.USERS, users);
		}
		else {
			List<User> users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), appId, -1, false, fetchNonAppUsers, 0, 5000, searchQuery, inviteAcceptStatus,userStatus,null,null,null);
			int count = users.size();
			context.put(FacilioConstants.ContextNames.COUNT, count);

		}

		return false;
	}

}
