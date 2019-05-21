package com.facilio.bmsconsole.util;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;
import org.json.simple.JSONObject;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class CommonAPI {

    public static long getActualLastRecordedTime(FacilioModule module) {
    	if(module.getDataInterval() <= 0) {
    		return -1l;
    	}
		try {
			ZonedDateTime zdt = DateTimeUtil.getDateTime(DateTimeUtil.getCurrenTime());
			zdt = zdt.truncatedTo(module.getDateIntervalUnit());
			return DateTimeUtil.getMillis(zdt, true);
		}
		catch(Exception e) {
			return -1l;
		}
	}

	public static void addNotificationLogger (NotificationType type, String to, JSONObject info) throws Exception {
		Map<String, Object> props = new HashMap<>();
		props.put("type", type.getValue());
		props.put("to", to);
		props.put("threadName", Thread.currentThread().getName());
		props.put("createdTime", System.currentTimeMillis());
		if (info != null) {
			props.put("info", info.toJSONString());
		}

		new GenericInsertRecordBuilder()
				.table(ModuleFactory.getNotificationLoggerModule().getTableName())
				.fields(FieldFactory.getNotificationLoggerFields())
				.insert(props)
		;
	}

	public static enum NotificationType {
		EMAIL,
		SMS
		;

		public int getValue() {
			return ordinal() + 1;
		}

		public static NotificationType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
