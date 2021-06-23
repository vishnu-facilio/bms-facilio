package com.facilio.events.constants;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.util.EventRulesAPI;

public class GetNewEventRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		if (id > 0) {
			context.put(EventConstants.EventContextNames.EVENT_RULE, EventRulesAPI.getEventRule(id));
		}
		return false;
	}

}
