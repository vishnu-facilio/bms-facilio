package com.facilio.cb.date;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.cb.context.ChatBotDateContext;
import com.facilio.time.DateTimeUtil;

public enum WeekEnum implements CalenderAndClockInterface {
	
	THIS(1,"THIS",false) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.WEEK_OF_YEAR,getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return dateContext.getCalendar().get(Calendar.WEEK_OF_YEAR);
		}

	},
	LAST(2,"LAST",false) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.WEEK_OF_YEAR, getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			
			int week = dateContext.getCalendar().get(Calendar.WEEK_OF_YEAR);
			int max = dateContext.getCalendar().getActualMaximum(Calendar.WEEK_OF_YEAR);
			int lastWeek = -1;
			if(week == 0) {
				lastWeek = max;
			}
			else {
				lastWeek = week-1;
			}
			return lastWeek;
		}

	},
	NEXT(3,"NEXT",false) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.WEEK_OF_YEAR, getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			
			int week = dateContext.getCalendar().get(Calendar.WEEK_OF_YEAR);
			int max = dateContext.getCalendar().getActualMaximum(Calendar.WEEK_OF_YEAR);
			int nextWeek = 0;
			if(week >= max) {
				nextWeek = 0;
			}
			else {
				nextWeek = week+1;
			}
			return nextWeek;
		}

	},
	FIRST(3,"FIRST",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.WEEK_OF_MONTH, getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 1;
		}

	},
	SECOND(3,"SECOND",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.WEEK_OF_MONTH, getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 2;
		}

	},
	THIRD(3,"THIRD",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.WEEK_OF_MONTH, getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 3;
		}

	},
	FOURTH(3,"FOURTH",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.WEEK_OF_MONTH, getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 4;
		}

	},
	FIFTH(3,"FIFTH",true) {
		@Override
		public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception {
			
			dateContext.getCalendar().set(Calendar.WEEK_OF_MONTH, getIntValue(dateContext));
		}

		@Override
		public int getIntValue(ChatBotDateContext dateContext) throws Exception {
			// TODO Auto-generated method stub
			return 5;
		}
	},
	;
	
	private Integer value;
	private String name;
	private boolean isMonthDependent; 
	
	public String getName() {
		return name;
	}
	public boolean isMonthDependent() {
		return isMonthDependent;
	}
	WeekEnum(Integer value,String name,boolean isMonthDependent) {
		this.value = value;
		this.name = name;
		this.isMonthDependent = isMonthDependent;
		
	}
	
	public static Map<String, WeekEnum> getAllDateEnums() {
		return DIRECT_DATE_ENUM_MAP;
	}
	public static WeekEnum getDateEnum(String functionName) {
		return DIRECT_DATE_ENUM_MAP.get(functionName);
	}
	static final Map<String, WeekEnum> DIRECT_DATE_ENUM_MAP = Collections.unmodifiableMap(initTypeMap());
	static Map<String, WeekEnum> initTypeMap() {
		Map<String, WeekEnum> typeMap = new HashMap<>();
		for(WeekEnum type : WeekEnum.values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
}