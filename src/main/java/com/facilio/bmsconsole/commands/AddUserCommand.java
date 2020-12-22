package com.facilio.bmsconsole.commands;


import com.facilio.accounts.dto.Role;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;

public class AddUserCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		boolean isEmailverificationNeeded = (Boolean)context.getOrDefault(FacilioConstants.ContextNames.IS_EMAIL_VERIFICATION_NEEDED, true);
		Long appId = (Long)context.getOrDefault(FacilioConstants.ContextNames.APPLICATION_ID, -1);

		if ( (user != null) && (AccountUtil.getCurrentOrg() != null)) {

			if (user.getRoleId() > 0) {
				Role role = AccountUtil.getRoleBean().getRole(user.getRoleId());
				FacilioUtil.throwIllegalArgumentException(role == null || role.getName().equals(AccountConstants.DefaultRole.SUPER_ADMIN), "Invalid role specified for user");
			}

			user.setUserType(AccountConstants.UserType.USER.getValue());
			if(appId == null || appId == -1) {
				appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
			if(appDomainObj == null) {
				throw new IllegalArgumentException("Invalid App Domain");
			}
			user.setApplicationId(appId);
			user.setAppDomain(appDomainObj);
			AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, appDomainObj.getIdentifier(), isEmailverificationNeeded, false);
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}
}
