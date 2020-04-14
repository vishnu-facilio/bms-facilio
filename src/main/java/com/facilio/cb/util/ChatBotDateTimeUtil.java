package com.facilio.cb.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.facilio.cb.context.ChatBotDateContext;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotTimeContext;
import com.facilio.cb.date.DayEnum;
import com.facilio.cb.date.MonthEnum;
import com.facilio.cb.date.WeekEnum;
import com.facilio.cb.date.YearEnum;
import com.facilio.time.DateTimeUtil;

public class ChatBotDateTimeUtil {
	
	public static final long ONE_HOUR_MILLISEC = 3600000l;
	public static final long ONE_MIN_MILLISEC = 60000l;
	
	public static long compute(ChatBotIntentParam param, ChatBotTimeContext timeContext) throws Exception {

		long returnMillisec = 0;
		if (timeContext.getHour() >= 0 || timeContext.getMin() >= 0) {
			
			if(timeContext.getHour() >= 0) {
				returnMillisec += timeContext.getHour() * ONE_HOUR_MILLISEC;
			}
			if(timeContext.getMin() >= 0) {
				returnMillisec += timeContext.getMin() * ONE_MIN_MILLISEC;
			}
		} 
		else if (timeContext.getTimeEnum() != null) {
			returnMillisec = timeContext.getTimeEnum().getMillisec(timeContext);
		} 
		
		return returnMillisec;
	}

	public static long compute(ChatBotIntentParam param, ChatBotDateContext dateContext) throws Exception {

		long returnMillisec = -1;
		if (dateContext.getDate() != null) {
			returnMillisec = DateTimeUtil.getTime(dateContext.getDate(), "dd-MM-yyyy HH:mm:ss");
		} 
		else if (dateContext.getDay() != null) {
			dateContext.getDay().fillCalenderObj(dateContext);
		} 
		else {
			if (dateContext.getDayEnum() == null && dateContext.getWeekEnum() == null && dateContext.getMonthEnum() == null && dateContext.getYearEnum() == null) {
				return returnMillisec;
			}
			Map<Integer,Integer> dataToBeFilledAtLast = new HashMap<>();
			if (dateContext.getDayEnum() != null) {
				
				if (dateContext.getWeekEnum() == null) {
					if(dateContext.getYearEnum() != null || dateContext.getMonthEnum() != null) {
						dateContext.setWeekEnum(WeekEnum.FIRST.getName());
					}
					else if (param.getDatePeriodEnum() == ChatBotIntentParam.Date_Period.PREVIOUS) {
						if (dateContext.getCalendar().get(Calendar.DAY_OF_WEEK) < dateContext.getDayEnum().getIntValue(dateContext)) {

							dateContext.setWeekEnum(WeekEnum.LAST.getName());
						} else {
							dateContext.setWeekEnum(WeekEnum.THIS.getName());
						}
					} else {
						if (dateContext.getCalendar().get(Calendar.DAY_OF_WEEK) > dateContext.getDayEnum().getIntValue(dateContext)) {

							dateContext.setWeekEnum(WeekEnum.NEXT.getName());
						} else {
							dateContext.setWeekEnum(WeekEnum.THIS.getName());
						}
					}
				}
			}
			else {
				if(dateContext.getWeekEnum() != null) {
					if(dateContext.getWeekEnum() == WeekEnum.FIRST) {
						dataToBeFilledAtLast.put(Calendar.DATE, 1);
					}
					else {
						dateContext.setDayEnum(DayEnum.SUNDAY.getName());
					}
				}
				else {
					dataToBeFilledAtLast.put(Calendar.DATE, 1);
				}
			}
			if (dateContext.getWeekEnum() != null) {

				if (dateContext.getWeekEnum().isMonthDependent()) {
					if (dateContext.getMonthEnum() == null) {
						dateContext.setMonthEnum(MonthEnum.JANUARY.getName());
					}
				} 
				else {
					if (dateContext.getYearEnum() == null) {
						if (dateContext.getWeekEnum() == WeekEnum.THIS) {
							dateContext.setYearEnum(YearEnum.THIS.getName());
						} 
						else if (dateContext.getWeekEnum() == WeekEnum.NEXT) {
							if (dateContext.getWeekEnum().getIntValue(dateContext) == 0) {
								dateContext.setYearEnum(YearEnum.NEXT.getName());
							} 
							else {
								dateContext.setYearEnum(YearEnum.THIS.getName());
							}
						} 
						else if (dateContext.getWeekEnum() == WeekEnum.LAST) {

							if (dateContext.getWeekEnum().getIntValue(dateContext) == 52) {
								dateContext.setYearEnum(YearEnum.LAST.getName());
							} 
							else {
								dateContext.setYearEnum(YearEnum.THIS.getName());
							}
						}
					}
				}
			}

			if (dateContext.getMonthEnum() != null) {

				if (dateContext.getYearEnum() == null && dateContext.getMonthEnum().isYearAdjustable()) {

					if (param.getDatePeriodEnum() == ChatBotIntentParam.Date_Period.PREVIOUS) {
						if (dateContext.getCalendar().get(Calendar.MONTH) < dateContext.getMonthEnum().getIntValue(dateContext)) {

							dateContext.setYearEnum(YearEnum.LAST.getName());
						} 
						else {
							dateContext.setYearEnum(YearEnum.THIS.getName());
						}
					} 
					else {
						if (dateContext.getCalendar().get(Calendar.MONTH) > dateContext.getMonthEnum().getIntValue(dateContext)) {

							dateContext.setYearEnum(YearEnum.NEXT.getName());
						} 
						else {
							dateContext.setYearEnum(YearEnum.THIS.getName());
						}
					}
				}
			}
			
			
			if(dateContext.getYearEnum() != null) {
				dateContext.getYearEnum().fillCalenderObj(dateContext);
			}
			if(dateContext.getMonthEnum() != null) {
				dateContext.getMonthEnum().fillCalenderObj(dateContext);
			}
			if(dateContext.getWeekEnum() != null) {
				dateContext.getWeekEnum().fillCalenderObj(dateContext);
			}
			if(dateContext.getDayEnum() != null) {
				dateContext.getDayEnum().fillCalenderObj(dateContext);
			}
			
			if(dataToBeFilledAtLast != null && !dataToBeFilledAtLast.isEmpty()) {
				for(Integer key : dataToBeFilledAtLast.keySet()) {
					dateContext.getCalendar().set(key, dataToBeFilledAtLast.get(key));
				}
			}
		}
		
		if(returnMillisec < 0) {
			returnMillisec = dateContext.getCalendar().getTimeInMillis();
		}
		return returnMillisec;
	}
}
