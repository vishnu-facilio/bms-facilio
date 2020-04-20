package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class GetApplicationUsersCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long appId = (Long)context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		if(appId <= 0) {
			throw new IllegalArgumentException("Invalid app id");
		}
		boolean fetchNonAppUsers= (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_NON_APP_USERS, false);
		List<User> users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), appId, false, fetchNonAppUsers);
		context.put(FacilioConstants.ContextNames.USERS, users);
	
		return false;
	}

}
