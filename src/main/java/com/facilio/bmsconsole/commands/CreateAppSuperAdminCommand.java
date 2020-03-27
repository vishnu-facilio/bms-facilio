package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.services.procon.consumer.FacilioConsumer;

public class CreateAppSuperAdminCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long orgId = (long) context.get("orgId");
		Role superAdminRole = AccountUtil.getRoleBean(orgId).getRole(orgId, AccountConstants.DefaultRole.SUPER_ADMIN, false);
		User user = AccountUtil.getCurrentUser();
		
		user.setRoleId(superAdminRole.getRoleId());
		user.setInviteAcceptStatus(true);
		user.setDefaultOrg(true);
		user.setInvitedTime(System.currentTimeMillis());
		if(FacilioProperties.isDevelopment()) {
			user.setUserVerified(true);
		}
		AppDomain appDomain = IAMAppUtil.getAppDomain(AccountUtil.getDefaultAppDomain());
		user.setAppDomain(appDomain);
		user.setUserType(UserType.USER.getValue());
		AccountUtil.getUserBean().createUserEntry(orgId, user, true, false);
		context.put(FacilioConstants.ContextNames.USER, user);
		return false;
	}

}
