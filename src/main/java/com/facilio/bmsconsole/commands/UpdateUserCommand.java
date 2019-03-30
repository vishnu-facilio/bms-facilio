package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateUserCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		
		if (user != null) {
			
			AccountUtil.getUserBean().updateUser(user.getOuid(), user);
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}

}
