package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.events.constants.EventConstants;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.OrgInfo;

public class GetEventRulesCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		context.put(EventConstants.EventContextNames.EVENT_RULE_LIST, EventRulesAPI.getEventRules(OrgInfo.getCurrentOrgInfo().getOrgid()));
		return false;
	}
}
