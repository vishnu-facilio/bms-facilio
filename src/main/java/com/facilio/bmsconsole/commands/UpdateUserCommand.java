package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class UpdateUserCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		if (user.getRoleId() > 0) {
			Role role = AccountUtil.getRoleBean().getRole(user.getRoleId());
			FacilioUtil.throwIllegalArgumentException(role == null || role.getName().equals(AccountConstants.DefaultRole.SUPER_ADMIN), "Invalid role specified for user");
		}
		PeopleAPI.updatePeopleOnUserUpdate(user);
		boolean result = AccountUtil.getUserBean().updateUser(user);
		context.put(FacilioConstants.ContextNames.RESULT, result);
		return false;
	}

}
