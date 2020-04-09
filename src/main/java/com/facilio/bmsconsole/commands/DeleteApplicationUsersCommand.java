package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;

public class DeleteApplicationUsersCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long appId = (Long)context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		if(appId <= 0) {
			throw new IllegalArgumentException("Invalid app id");
		}
	
		int rowsUpdated = ApplicationApi.deleteUserFromApp(user, appId);
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return false;
	}

}
