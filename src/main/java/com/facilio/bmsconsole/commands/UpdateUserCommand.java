package com.facilio.bmsconsole.commands;

import java.sql.Connection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class UpdateUserCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		UserContext user = (UserContext) context.get(FacilioConstants.ContextNames.USER);
		
		if (user != null) {
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			UserAPI.updateUser(user, OrgInfo.getCurrentOrgInfo().getOrgid());
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}

}
