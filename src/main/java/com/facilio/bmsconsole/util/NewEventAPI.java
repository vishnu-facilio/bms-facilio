package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.ReadingEventContext;

public class NewEventAPI {

	private static Class getEventClass(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid alarm type");
		}
		
		switch (type) {
		case READING_ALARM:
			return ReadingEventContext.class;

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

		default:
			throw new IllegalArgumentException("Invalid alarm type");
		}
	}
}
