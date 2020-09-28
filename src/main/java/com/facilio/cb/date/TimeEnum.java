package com.facilio.cb.date;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.cb.context.ChatBotDateContext;
import com.facilio.cb.util.ChatBotDateTimeUtil;

public enum TimeEnum implements CalenderAndClockInterface {
	
	MORNING(1,"MORNING") {

		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			dateContext.getCalendar().set(Calendar.HOUR_OF_DAY, getIntValue(dateContext));
			dateContext.getCalendar().set(Calendar.MINUTE, 0);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 9;
		}

	},
	NOON(2,"NOON") {

		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			dateContext.getCalendar().set(Calendar.HOUR_OF_DAY, getIntValue(dateContext));
			dateContext.getCalendar().set(Calendar.MINUTE, 0);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 12;
		}

	},
	EVENING(3,"EVENING") {

		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			dateContext.getCalendar().set(Calendar.HOUR_OF_DAY, getIntValue(dateContext));
			dateContext.getCalendar().set(Calendar.MINUTE, 0);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 16;
		}

	},
	NIGHT(4,"NIGHT") {

		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			dateContext.getCalendar().set(Calendar.HOUR_OF_DAY, getIntValue(dateContext));
			dateContext.getCalendar().set(Calendar.MINUTE, 0);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 21;
		}

	},
	AFTER_LUNCH(5,"AFTER_LUNCH") {

		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			dateContext.getCalendar().set(Calendar.HOUR_OF_DAY, getIntValue(dateContext));
			dateContext.getCalendar().set(Calendar.MINUTE, 0);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 15;
		}

	},
	BEFORE_LUNCH(6,"BEFORE_LUNCH") {

		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			dateContext.getCalendar().set(Calendar.HOUR_OF_DAY, getIntValue(dateContext));
			dateContext.getCalendar().set(Calendar.MINUTE, 0);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 11;
		}

	},
	;
	
	private Integer value;
	private String name;
	
	public Integer getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	TimeEnum(Integer value,String name) {
		this.value = value;
		this.name = name;
		
	}
	
	public static Map<String, TimeEnum> getAllDateEnums() {
		return DIRECT_DATE_ENUM_MAP;
	}
	public static TimeEnum getDateEnum(String functionName) {
		return DIRECT_DATE_ENUM_MAP.get(functionName);
	}
	static final Map<String, TimeEnum> DIRECT_DATE_ENUM_MAP = Collections.unmodifiableMap(initTypeMap());
	static Map<String, TimeEnum> initTypeMap() {
		Map<String, TimeEnum> typeMap = new HashMap<>();
		for(TimeEnum type : TimeEnum.values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
}