package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.modules.FieldUtil;
import org.json.simple.JSONObject;

import java.util.Map;

public class NewEventAPI {

	public static Class getEventClass(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid alarm type");
		}

		switch (type) {
			case READING_ALARM:
				return ReadingEventContext.class;

			case ML_ANOMALY_ALARM:
				return MLAnomalyEvent.class;

			case RCA_ALARM:
				return RCAEvent.class;

			case READING_RCA_ALARM:
				return ReadingRCAEvent.class;

			case BMS_ALARM:
				return BMSEventContext.class;

			default:
				throw new IllegalArgumentException("Invalid alarm type");
		}
	}

	public static String getEventModuleName(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid alarm type");
		}

		switch (type) {
			case READING_ALARM:
				return "readingevent";

			case ML_ANOMALY_ALARM:
				return "mlanomalyevent";

			case RCA_ALARM:
				return "RcaEvent";

			case READING_RCA_ALARM:
				return "readingrcaevent";

			case BMS_ALARM:
				return "bmsevent";

			default:
				throw new IllegalArgumentException("Invalid alarm type");
		}
	}

	public static BMSEventContext transformEvent(BMSEventContext event, JSONTemplate template, Map<String, Object> placeHolders) throws Exception {
		Map<String, Object> eventProp = FieldUtil.getAsProperties(event);
		JSONObject content = template.getTemplate(placeHolders);
		if (content != null && !content.isEmpty()) {
			content.put("severityString", content.remove("severity"));
		}
		eventProp.putAll(FieldUtil.getAsProperties(content));
		event = FieldUtil.getAsBeanFromMap(eventProp, BMSEventContext.class);
		event.setMessageKey(null);
		eventProp.put("messageKey", event.getMessageKey()); //Setting the new key in case if it's updated
		CommonCommandUtil.appendModuleNameInKey(null, "event", eventProp, placeHolders);//Updating the placeholders with the new event props
		return event;
	}
}
