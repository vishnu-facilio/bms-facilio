package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class GetTriggerForRecurringLogCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long id = (Long)context.get(FacilioConstants.ContextNames.ID);
		if(id > 0) {
			VisitorLoggingContext vLog = VisitorManagementAPI.getVisitorLoggingTriggers(id, null, true);
			context.put(FacilioConstants.ContextNames.RECORD, vLog);
		}
		return false;
	}

	
}
