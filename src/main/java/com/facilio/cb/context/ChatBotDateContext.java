package com.facilio.cb.context;

import java.util.Calendar;

import com.facilio.cb.date.DayEnum;
import com.facilio.cb.date.DirectDateEnums;
import com.facilio.cb.date.MonthEnum;
import com.facilio.cb.date.WeekEnum;
import com.facilio.cb.date.YearEnum;
import com.facilio.time.DateTimeUtil;

public class ChatBotDateContext {
	
	String date;
	DirectDateEnums day;
	YearEnum yearEnum;
	MonthEnum monthEnum;
	WeekEnum weekEnum;
	DayEnum dayEnum;
	
	Calendar calendar = Calendar.getInstance(DateTimeUtil.getLocale());
	
	{
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
	}
	
	public WeekEnum getWeekEnum() {
		return weekEnum;
	}
	public void setWeekEnum(String weekEnum) {
		this.weekEnum = WeekEnum.getDateEnum(weekEnum);
	}
	public DayEnum getDayEnum() {
		return dayEnum;
	}
	public void setDayEnum(String dayEnum) {
		this.dayEnum = DayEnum.getDateEnum(dayEnum);
	}
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public DirectDateEnums getDay() {
		return day;
	}
	public void setDay(String day) {
		if(day != null) {
			this.day = DirectDateEnums.getDateEnum(day);
		}
	}
	public YearEnum getYearEnum() {
		return yearEnum;
	}
	public void setYearEnum(String yearEnum) {
		this.yearEnum = YearEnum.getDateEnum(yearEnum);
	}
	public MonthEnum getMonthEnum() {
		return monthEnum;
	}
	public void setMonthEnum(String monthEnum) {
		this.monthEnum = MonthEnum.getDateEnum(monthEnum);
	}
	
}
