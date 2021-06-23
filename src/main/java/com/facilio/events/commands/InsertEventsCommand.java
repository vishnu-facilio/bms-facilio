package com.facilio.events.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;

public class InsertEventsCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(InsertEventsCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<JSONObject> payloads = (List<JSONObject>) context.get(EventConstants.EventContextNames.EVENT_PAYLOADS);
		if (payloads == null) {
			JSONObject payload = (JSONObject) context.get(EventConstants.EventContextNames.EVENT_PAYLOAD);
			
			if(AccountUtil.getCurrentOrg().getId() == 78l && payload != null && payload.containsKey("alarmId")) {
				long alarmId = (long) payload.get("alarmId");
				if(alarmId == 3463448l) {
					LOGGER.error("EventPayload : "+payload);
				}
			}
			
			if (payload != null) {
				payloads = Collections.singletonList(payload);
			}
		}

		if (CollectionUtils.isNotEmpty(payloads)) {
			List<EventContext> events = EventAPI.processPayloadsAndEvents(payloads);
			context.put(EventConstants.EventContextNames.EVENT_LIST, events);
		}
		return false;
	}

}
