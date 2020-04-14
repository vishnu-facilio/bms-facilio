package com.facilio.cb.date;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.cb.context.ChatBotTimeContext;
import com.facilio.cb.util.ChatBotDateTimeUtil;

public enum TimeEnum implements ChatBotTimeInterface {
	
	MORNING(1,"MORNING") {

		@Override
		public long getMillisec(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return getIntValue(dateContext) * ChatBotDateTimeUtil.ONE_HOUR_MILLISEC;
		}

		@Override
		public int getIntValue(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 9;
		}

	},
	NOON(2,"NOON") {

		@Override
		public long getMillisec(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return getIntValue(dateContext) * ChatBotDateTimeUtil.ONE_HOUR_MILLISEC;
		}

		@Override
		public int getIntValue(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 12;
		}

	},
	EVENING(3,"EVENING") {

		@Override
		public long getMillisec(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return getIntValue(dateContext) * ChatBotDateTimeUtil.ONE_HOUR_MILLISEC;
		}

		@Override
		public int getIntValue(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 16;
		}

	},
	NIGHT(3,"NIGHT") {

		@Override
		public long getMillisec(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return getIntValue(dateContext) * ChatBotDateTimeUtil.ONE_HOUR_MILLISEC;
		}

		@Override
		public int getIntValue(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 21;
		}

	},
	AFTER_LUNCH(3,"AFTER_LUNCH") {

		@Override
		public long getMillisec(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return getIntValue(dateContext) * ChatBotDateTimeUtil.ONE_HOUR_MILLISEC;
		}

		@Override
		public int getIntValue(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 15;
		}

	},
	BEFORE_LUNCH(3,"BEFORE_LUNCH") {

		@Override
		public long getMillisec(ChatBotTimeContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return getIntValue(dateContext) * ChatBotDateTimeUtil.ONE_HOUR_MILLISEC;
		}

		@Override
		public int getIntValue(ChatBotTimeContext dateContext) throws Exception {
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