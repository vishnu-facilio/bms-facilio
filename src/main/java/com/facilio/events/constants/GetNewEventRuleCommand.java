package com.facilio.events.constants;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.events.util.EventRulesAPI;

public class GetNewEventRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		if (id > 0) {
			context.put(EventConstants.EventContextNames.EVENT_RULE, EventRulesAPI.getEventRule(id));
		}
		return false;
	}

}
