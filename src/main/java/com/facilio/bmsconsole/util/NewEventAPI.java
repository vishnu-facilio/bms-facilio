package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.MLAnomalyEvent;
import com.facilio.bmsconsole.context.RCAEvent;
import com.facilio.bmsconsole.context.ReadingEventContext;

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

		default:
			throw new IllegalArgumentException("Invalid alarm type");
		}
	}
}
