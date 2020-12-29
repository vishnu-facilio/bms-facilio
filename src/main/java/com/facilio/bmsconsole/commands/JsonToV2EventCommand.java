package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.util.EventAPI;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonToV2EventCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		JSONArray jsonArray = (JSONArray) context.get(EventConstants.EventContextNames.EVENT_PAYLOAD);
		if (jsonArray != null) {
			List<BaseEventContext> eventList = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				Map<String, Object> jsonObject = (Map) jsonArray.get(i);
				Type type = getAlarmType(jsonObject);

				if (type == null) {
					// default event is bms alarm
					type = Type.BMS_ALARM;
				}
				if(jsonObject.containsKey(AgentConstants.AGENT)) {
					long agentId = EventAPI.getAgent((String) jsonObject.get(AgentConstants.AGENT));
					jsonObject.put(AgentConstants.AGENT_ID, agentId);
				}
				BaseEventContext baseEvent = (BaseEventContext) FieldUtil.getAsBeanFromMap(jsonObject, NewEventAPI.getEventClass(type));
				eventList.add(baseEvent);
			}
			context.put(EventConstants.EventContextNames.EVENT_LIST, eventList);
		}
		return false;
	}

	private Type getAlarmType(Map<String, Object> jsonObject) {
		if (jsonObject.containsKey("alarmType")) {
			int alarmType = ((Number) jsonObject.get("alarmType")).intValue();
			Type type = BaseAlarmContext.Type.valueOf(alarmType);
			return type;
		}
		return null;
	}

}
