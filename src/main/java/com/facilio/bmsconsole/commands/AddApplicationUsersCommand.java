package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddApplicationUsersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long appId = (Long)context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		if(appId <= 0) {
			throw new IllegalArgumentException("Invalid app id");
		}
		ApplicationContext app = ApplicationApi.getApplicationForId(appId);

		List<AppDomain> appDomains = IAMAppUtil.getAppDomainForType(app.getDomainType(), AccountUtil.getCurrentOrg().getOrgId());
		if(CollectionUtils.isEmpty(appDomains)) {
			throw new IllegalArgumentException("Invalid app domain");
		}

		boolean isEmailverificationNeeded = (Boolean)context.getOrDefault(FacilioConstants.ContextNames.IS_EMAIL_VERIFICATION_NEEDED, true);
		if(isEmailverificationNeeded){
			user.setUserVerified(false);
			user.setInviteAcceptStatus(false);
		}
		else{
			user.setUserVerified(true);
			user.setInviteAcceptStatus(true);
		}

		user.setInvitedTime(System.currentTimeMillis());
		user.setAppDomain(appDomains.get(0));
		user.setApplicationId(appId);

		AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, appDomains.get(0).getIdentifier(), isEmailverificationNeeded, false);
		context.put(FacilioConstants.ContextNames.ORG_USER_ID, user.getOuid());
		
		return false;
	}
	
}
