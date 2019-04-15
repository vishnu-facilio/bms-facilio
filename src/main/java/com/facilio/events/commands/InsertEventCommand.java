package com.facilio.events.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.util.EventAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Map;

public class InsertEventCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long timeStamp = (long) context.get(EventConstants.EventContextNames.EVENT_TIMESTAMP);
		JSONObject payload = (JSONObject) context.get(EventConstants.EventContextNames.EVENT_PAYLOAD);
		long orgId = AccountUtil.getCurrentOrg().getId();
        EventContext event = EventAPI.processPayload(timeStamp, payload, orgId);
        Map<String, Object> prop = FieldUtil.getAsProperties(event);
        EventAPI.insertObject(prop);
        event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
        event.setInternalState(EventInternalState.ADDED);
        context.put(EventConstants.EventContextNames.EVENT, event);
		return false;
	}

}
