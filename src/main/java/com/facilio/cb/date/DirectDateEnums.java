package com.facilio.cb.date;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.cb.context.ChatBotDateContext;
import com.facilio.time.DateTimeUtil;

public enum DirectDateEnums implements ChatBotDateInterface {
	
	TODAY(2,"TODAY") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 0;
		}

	},
	YESTERDAY(3,"YESTERDAY") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().setTimeInMillis(DateTimeUtil.addDays(DateTimeUtil.getDayStartTime(),-1));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 0;
		}

	},
	TOMORROW(4,"TOMORROW") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().setTimeInMillis(DateTimeUtil.addDays(DateTimeUtil.getDayStartTime(),1));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 0;
		}

	},
	DAY_AFTER_TOMORROW(5,"DAY_AFTER_TOMORROW") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().setTimeInMillis(DateTimeUtil.addDays(DateTimeUtil.getDayStartTime(),2));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 0;
		}

	},
	DAY_BEFORE_YESTERDAY(6,"DAY_BEFORE_YESTERDAY") {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().setTimeInMillis(DateTimeUtil.addDays(DateTimeUtil.getDayStartTime(),-2));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 0;
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
	DirectDateEnums(Integer value,String name) {
		this.value = value;
		this.name = name;
		
	}
	
	public static Map<String, DirectDateEnums> getAllDateEnums() {
		return DIRECT_DATE_ENUM_MAP;
	}
	public static DirectDateEnums getDateEnum(String functionName) {
		return DIRECT_DATE_ENUM_MAP.get(functionName);
	}
	static final Map<String, DirectDateEnums> DIRECT_DATE_ENUM_MAP = Collections.unmodifiableMap(initTypeMap());
	static Map<String, DirectDateEnums> initTypeMap() {
		Map<String, DirectDateEnums> typeMap = new HashMap<>();
		for(DirectDateEnums type : DirectDateEnums.values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
}
