package com.facilio.cb.date;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.cb.context.ChatBotDateContext;

public enum DayEnum implements ChatBotDateInterface {
	
	SUNDAY(1,"SUNDAY") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.SUNDAY;
		}
	},
	MONDAY(2,"MONDAY") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.MONDAY;
		}
	},
	TUESDAY(3,"TUESDAY") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.TUESDAY;
		}
	},
	WEDNESDAY(4,"WEDNESDAY") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			dateContext.getCalendar().set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.WEDNESDAY;
		}
	},
	THURSDAY(5,"THURSDAY") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.THURSDAY;
		}
	},
	FRIDAY(6,"FRIDAY") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.FRIDAY;
		}
	},
	SATURDAY(7,"SATURDAY") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.SATURDAY;
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
	DayEnum(Integer value,String name) {
		this.value = value;
		this.name = name;
		
	}
	
	public static Map<String, DayEnum> getAllDateEnums() {
		return DIRECT_DATE_ENUM_MAP;
	}
	public static DayEnum getDateEnum(String functionName) {
		return DIRECT_DATE_ENUM_MAP.get(functionName);
	}
	static final Map<String, DayEnum> DIRECT_DATE_ENUM_MAP = Collections.unmodifiableMap(initTypeMap());
	static Map<String, DayEnum> initTypeMap() {
		Map<String, DayEnum> typeMap = new HashMap<>();
		for(DayEnum type : DayEnum.values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
}