package com.facilio.bmsconsole.commands;


import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;

public class AddUserCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		if ( (user != null) && (AccountUtil.getCurrentOrg() != null)) {
			AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, 1, AccountUtil.getDefaultAppDomain());
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}
}
