package com.facilio.bmsconsole.commands;


import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class AddUserCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		if ( (user != null) && (AccountUtil.getCurrentOrg() != null)) {
			long userId = AccountUtil.getTransactionalUserBean().inviteUser(AccountUtil.getCurrentOrg().getOrgId(), user);
			context.put(FacilioConstants.ContextNames.USER_ID, userId);
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}
}
