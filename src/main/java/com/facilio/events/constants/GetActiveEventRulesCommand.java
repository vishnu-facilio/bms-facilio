package com.facilio.events.constants;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.events.util.EventRulesAPI;

public class GetActiveEventRulesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		context.put(EventConstants.EventContextNames.EVENT_RULE_LIST, EventRulesAPI.getActiveEventRules());
		return false;
	}

}
