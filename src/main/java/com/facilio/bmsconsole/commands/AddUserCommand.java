package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.UserAPI;

public class AddUserCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		UserContext userContext = (UserContext) context;
		long userId = UserAPI.addUser(userContext);
		userContext.setUserId(userId);
		
		return true;
	}

}
