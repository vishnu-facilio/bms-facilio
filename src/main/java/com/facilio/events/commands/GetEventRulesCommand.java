package com.facilio.events.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.util.EventRulesAPI;

public class GetEventRulesCommand extends FacilioCommand {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		context.put(EventConstants.EventContextNames.EVENT_RULE_LIST, EventRulesAPI.getEventRules(AccountUtil.getCurrentOrg().getOrgId()));
		return false;
	}
}
