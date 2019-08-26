package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;

public class CreateAppSuperAdminCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long orgId = (long) context.get("orgId");
		Role superAdminRole = AccountUtil.getRoleBean(orgId).getRole(orgId, AccountConstants.DefaultRole.SUPER_ADMIN, false);
		User user = (User) AccountUtil.getCurrentUser();
		
		user.setRoleId(superAdminRole.getRoleId());
		user.setInviteAcceptStatus(true);
		user.setDefaultOrg(true);
		user.setInvitedTime(System.currentTimeMillis());
		if(AwsUtil.isDevelopment()) {
			user.setUserVerified(true);
		}
		AccountUtil.getUserBean().createUserEntry(orgId, user, true);
		return false;
	}

}
