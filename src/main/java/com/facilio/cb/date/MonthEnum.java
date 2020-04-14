package com.facilio.cb.date;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.cb.context.ChatBotDateContext;

public enum MonthEnum implements ChatBotDateInterface {
	
	THIS(1,"THIS",false) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			return 0;
		}

	},
	LAST(2,"LAST",false) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
			
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			
			int month = dateContext.getCalendar().get(Calendar.MONTH);
			int lastMonth = 0;
			if(month == 0) {
				lastMonth = 11;
				
			}
			else {
				lastMonth = month - 1;
			}
			
			return lastMonth;
		}

	},
	NEXT(3,"NEXT",false) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			
			int month = dateContext.getCalendar().get(Calendar.MONTH);
			int nextMonth = 0;
			if(month == 11) {
				nextMonth = 0;
			}
			else {
				nextMonth = month + 1;
			}
			return nextMonth;
		}

	},
	JANUARY(4,"JANUARY",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.JANUARY;
		}

	},
	FEBRUARY(5,"FEBRUARY",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.FEBRUARY;
		}

	},
	MARCH(6,"MARCH",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.MARCH;
		}

	},
	APRIL(7,"APRIL",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.APRIL;
		}

	},
	MAY(8,"MAY",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.MAY;
		}

	},
	JUNE(9,"JUNE",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.JUNE;
		}

	},
	JULY(10,"JULY",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.JULY;
		}

	},
	AUGUEST(11,"AUGUEST",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.AUGUST;
		}

	},
	SEPTEMBER(12,"SEPTEMBER",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.SEPTEMBER;
		}

	},
	OCTOBER(13,"OCTOBER",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.OCTOBER;
		}

	},
	NOVEMBER(14,"NOVEMBER",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.NOVEMBER;
		}

	},
	DECEMBER(15,"DECEMBER",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.MONTH, getIntValue(dateContext));
		}
		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return Calendar.DECEMBER;
		}

	},
	
	
	;
	
	private Integer value;
	private String name;
	boolean isYearAdjustable;
	
	public Integer getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	public boolean isYearAdjustable() {
		return isYearAdjustable;
	}
	MonthEnum(Integer value,String name, boolean isYearAdjustable) {
		this.value = value;
		this.name = name;
		this.isYearAdjustable = isYearAdjustable;
	}
	
	public static Map<String, MonthEnum> getAllDateEnums() {
		return DIRECT_DATE_ENUM_MAP;
	}
	public static MonthEnum getDateEnum(String functionName) {
		return DIRECT_DATE_ENUM_MAP.get(functionName);
	}
	static final Map<String, MonthEnum> DIRECT_DATE_ENUM_MAP = Collections.unmodifiableMap(initTypeMap());
	static Map<String, MonthEnum> initTypeMap() {
		Map<String, MonthEnum> typeMap = new HashMap<>();
		for(MonthEnum type : MonthEnum.values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
}