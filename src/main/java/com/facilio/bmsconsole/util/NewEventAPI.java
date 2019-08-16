package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;

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
				return "readingevent";

			case BMS_ALARM:
				return "bmsevent";

			default:
				throw new IllegalArgumentException("Invalid alarm type");
		}
	}
}
