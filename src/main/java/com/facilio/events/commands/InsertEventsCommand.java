package com.facilio.events.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.util.EventAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InsertEventsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
