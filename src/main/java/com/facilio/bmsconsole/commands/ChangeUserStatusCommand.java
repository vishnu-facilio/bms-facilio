package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class ChangeUserStatusCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
//		User user = (User) context.get(FacilioConstants.ContextNames.USER);
//		
//		if (user != null) {
//			AccountUtil.getUserBean().updateUser(ouid, user)
//		}
//		else {
//			throw new IllegalArgumentException("User Object cannot be null");
//		}
		return false;
	}
}
