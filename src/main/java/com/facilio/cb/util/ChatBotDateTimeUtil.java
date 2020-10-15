package com.facilio.cb.util;

import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.date.calenderandclock.*;
import com.facilio.time.DateTimeUtil;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class ChatBotDateTimeUtil {
	
	public static final long ONE_HOUR_MILLISEC = 3600000l;
	public static final long ONE_MIN_MILLISEC = 60000l;
	
	public static TimeZone getTimeZone() {
	    final TimeZone timeZone = TimeZone.getTimeZone(DateTimeUtil.getDateTime().getZone());
	    
	    String tz =Arrays.stream(TimeZone.getAvailableIDs(timeZone.getRawOffset())).findFirst().get();
	    return TimeZone.getTimeZone(tz);
	}
	

	public static long compute(ChatBotIntentParam param, CalenderAndClockContext dateContext) throws Exception {

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
						if(dateContext.getDateInt() < 0) {
							dateContext.setDateInt(1);
						}
					}
					else {
						dateContext.setDayEnum(WeekDayEnum.SUNDAY.getName());
					}
				}
				else {
					if(dateContext.getDateInt() < 0) {
						dateContext.setDateInt(1);
					}
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
			if(dateContext.getDateInt() > 0) {
				dateContext.getCalendar().set(Calendar.DAY_OF_MONTH, dateContext.getDateInt());
			}
		}
		
		if(dateContext.getTimeEnum() != null) {
			dateContext.getTimeEnum().fillCalenderObj(dateContext);
		}
		if(dateContext.getHour() > -1) {
			dateContext.getCalendar().set(Calendar.HOUR_OF_DAY, dateContext.getHour());
		}
		if(dateContext.getMin() > -1) {
			dateContext.getCalendar().set(Calendar.MINUTE, dateContext.getMin());
		}
		
		if(returnMillisec < 0) {
			returnMillisec = dateContext.getCalendar().getTimeInMillis();
		}
		return returnMillisec;
	}
}
