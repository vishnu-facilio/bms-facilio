package com.facilio.events.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;

public class InsertEventsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long timeStamp = (long) context.get(EventConstants.EventContextNames.EVENT_TIMESTAMP);

		List<JSONObject> payloads = (List<JSONObject>) context.get(EventConstants.EventContextNames.EVENT_PAYLOADS);
		if (payloads == null) {
			JSONObject payload = (JSONObject) context.get(EventConstants.EventContextNames.EVENT_PAYLOAD);
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
