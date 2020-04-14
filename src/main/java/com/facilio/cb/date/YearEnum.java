package com.facilio.cb.date;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.cb.context.ChatBotDateContext;

public enum YearEnum implements ChatBotDateInterface {
	
	THIS(1,"THIS") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.YEAR, getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			int year = dateContext.getCalendar().get(Calendar.YEAR);
			return year;
		}

	},
	LAST(2,"LAST") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.YEAR, getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			int year = dateContext.getCalendar().get(Calendar.YEAR);
			return year-1;
		}

	},
	NEXT(3,"NEXT") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.YEAR, getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			int year = dateContext.getCalendar().get(Calendar.YEAR);
			return year+1;
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
	YearEnum(Integer value,String name) {
		this.value = value;
		this.name = name;
		
	}
	
	public static Map<String, YearEnum> getAllDateEnums() {
		return DIRECT_DATE_ENUM_MAP;
	}
	public static YearEnum getDateEnum(String functionName) {
		return DIRECT_DATE_ENUM_MAP.get(functionName);
	}
	static final Map<String, YearEnum> DIRECT_DATE_ENUM_MAP = Collections.unmodifiableMap(initTypeMap());
	static Map<String, YearEnum> initTypeMap() {
		Map<String, YearEnum> typeMap = new HashMap<>();
		for(YearEnum type : YearEnum.values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
}