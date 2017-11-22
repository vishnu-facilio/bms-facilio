package com.facilio.events.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddEventCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		JSONObject payload = (JSONObject) context.get(EventConstants.EventContextNames.EVENT_PAYLOAD);
		if(payload != null) {
			EventContext event = EventAPI.processPayload(System.currentTimeMillis(), payload, OrgInfo.getCurrentOrgInfo().getOrgid());
			
			Map<String, Object> props = FieldUtil.getAsProperties(event);
			
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
																.table("Event")
																.fields(EventConstants.EventFieldFactory.getEventFields())
																.addRecord(props);
			
			builder.save();
		}
		return false;
	}
}
