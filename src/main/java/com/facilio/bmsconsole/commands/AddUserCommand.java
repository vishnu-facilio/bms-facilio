package com.facilio.bmsconsole.commands;


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
		if ( (user != null) && (AccountUtil.getCurrentOrg() != null)) {
			user.setUserType(AccountConstants.UserType.USER.getValue());
			AppDomain appDomainObj = IAMAppUtil.getAppDomain(AccountUtil.getDefaultAppDomain());
			if(appDomainObj == null) {
				throw new IllegalArgumentException("Invalid App Domain");
			}
			long appId = ApplicationApi.getApplicationIdForApp(appDomainObj);
			user.setApplicationId(appId);
			user.setAppDomain(appDomainObj);
			
			
			AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, 1);
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}
}
