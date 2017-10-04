package com.facilio.bmsconsole.commands;


import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.constants.FacilioConstants;

public class AddUserCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		UserContext user = (UserContext) context.get(FacilioConstants.ContextNames.USER);
		
		if (user != null) {
			
			long userId = UserAPI.addUser(user);
			
			context.put(FacilioConstants.ContextNames.USER_ID, userId);
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}
}
