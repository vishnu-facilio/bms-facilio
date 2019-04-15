package com.facilio.events.constants;

import com.facilio.events.util.EventRulesAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class GetActiveEventRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		context.put(EventConstants.EventContextNames.EVENT_RULE_LIST, EventRulesAPI.getActiveEventRules());
		return false;
	}

}
