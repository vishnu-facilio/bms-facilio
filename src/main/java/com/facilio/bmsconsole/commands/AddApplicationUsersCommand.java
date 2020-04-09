package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;

public class AddApplicationUsersCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long appId = (Long)context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		if(appId <= 0) {
			throw new IllegalArgumentException("Invalid app id");
		}
		AppDomain appDomain = ApplicationApi.getAppDomainForApplication(appId);
		if(appDomain == null) {
			throw new IllegalArgumentException("Invalid app domain");
		}
		user.setUserVerified(false);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());
		user.setAppDomain(appDomain);
		user.setApplicationId(appId);
	
		AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, appDomain.getIdentifier(), true, false);
		context.put(FacilioConstants.ContextNames.ORG_USER_ID, user.getOuid());
		
		return false;
	}

}
