package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
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
		int page, perPage = -1, offset = -1;

		if(appId <= 0) {
			throw new IllegalArgumentException("Invalid app id");
		}

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
			List<User> users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), appId, false, fetchNonAppUsers, offset, perPage);
			context.put(FacilioConstants.ContextNames.USERS, users);
		}
		else {
			Long count = AccountUtil.getOrgBean().getAppUsersCount(AccountUtil.getCurrentOrg().getOrgId(), appId, fetchNonAppUsers);
			context.put(FacilioConstants.ContextNames.COUNT, count);
		}

		return false;
	}

}
