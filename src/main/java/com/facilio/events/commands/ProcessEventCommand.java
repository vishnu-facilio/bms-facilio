package com.facilio.events.commands;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.chain.FacilioContext;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventAPI;
import com.facilio.events.util.EventRulesAPI;

public class ProcessEventCommand implements Command {

	private static Logger log = LogManager.getLogger(ProcessEventCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		JSONObject payload = (JSONObject) context.get(EventConstants.EventContextNames.EVENT_PAYLOAD);
		if(payload != null) {
			List<EventRuleContext> ruleList = EventRulesAPI.getActiveEventRules();
			EventAPI.populateProcessEventParams((FacilioContext) context, System.currentTimeMillis(), payload, ruleList, new HashMap<>(), -1);
		}
		return false;
	}
}
