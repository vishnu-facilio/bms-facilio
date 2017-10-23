package com.facilio.bmts.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.EventContext;
import com.facilio.constants.FacilioConstants;

public class ExecuteEventTransformRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		EventContext event = (EventContext) context.get("event");
		
		return false;
	}
}
