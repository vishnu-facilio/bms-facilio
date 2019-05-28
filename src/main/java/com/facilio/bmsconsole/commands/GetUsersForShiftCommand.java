package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class GetUsersForShiftCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		List<Long> users = new ArrayList<>();
		
		context.put(FacilioConstants.ContextNames.USERS, users);
		context.put(FacilioConstants.ContextNames.DATE, users);
		return false;
	}

}
