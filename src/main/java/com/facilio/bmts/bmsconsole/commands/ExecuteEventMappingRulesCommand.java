package com.facilio.bmts.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmts.bmsconsole.context.EventContext;
import com.facilio.bmts.constants.BmtsConstants;

public class ExecuteEventMappingRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		EventContext event = (EventContext) context.get(BmtsConstants.EVENT);
		
		return false;
	}
}
