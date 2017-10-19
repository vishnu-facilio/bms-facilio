package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.EventContext;
import com.facilio.constants.FacilioConstants;

public class ExecuteEventMappingRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		EventContext event = (EventContext) context.get(FacilioConstants.ContextNames.EVENT);
		
		return false;
	}
}
