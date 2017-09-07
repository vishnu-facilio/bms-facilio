package com.facilio.bmsconsole.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Locale;


public class DateTimeUtil 
{
	
	
	private static ZoneId getZoneId()
	{
		//TODO TimeZone related changes to be done.
		//like OrgInfo.getCurrentOrgInfo().getTimezone() & set the ZoneId..
		return ZoneId.systemDefault();
	}
	
	private static Locale getLocale()
	{
		//TODO Locale related changes to be done..
		//like OrgInfo.getCurrentOrgInfo().getLocale() & set the Locale..
		return Locale.getDefault();
	}
	
	private static LocalDateTime getMidnight()
	{
		return LocalDateTime.of(LocalDate.now(getZoneId()), LocalTime.MIDNIGHT);
	}
	
	private static LocalDate getWeekFirst()
	{
		return LocalDate.now(getZoneId()).with(WeekFields.of(getLocale()).dayOfWeek(),1L);
	}

	private static LocalDate getMonthFirst()
	{
		return LocalDate.now(getZoneId()).with(TemporalAdjusters.firstDayOfMonth());
	}
	
	private static LocalDate getYearFirst()
	{
		return LocalDate.now(getZoneId()).with(TemporalAdjusters.firstDayOfYear());
	}
	
	private static long getMillis(LocalDateTime ldTime, boolean resetTime )
	{	
		return getMillis(ZonedDateTime.of(ldTime, getZoneId()),resetTime);
	}
	
	private static long getMillis(ZonedDateTime ldTime, boolean resetMillis )
	{	
		if(resetMillis)
		{
			//Since it is midnight or start of the day, no harm in multiplying by 1000 to getMillis;
			return ldTime.toEpochSecond()*1000;
		}
		return ldTime.toInstant().toEpochMilli();
	}
	
	private static LocalDateTime getHourFirst()
	{
		LocalTime lTime =LocalTime.now(getZoneId());
		return LocalTime.of(lTime.getHour(), 0).atDate(LocalDate.now(getZoneId()));
	}
	
	public static HashMap<String,Object> getTimeData(long addedTime)
	{
		HashMap<String,Object> columnVals = new HashMap<String, Object>() ;
		ZonedDateTime zdt = getDateTime(addedTime);
		int hour=zdt.getHour();
		int month=zdt.getMonthValue();
		int week=zdt.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
		String date=zdt.toLocalDate().toString();
		String day=zdt.getDayOfWeek().toString();
		int year=zdt.getYear();
		columnVals.put("date", date);
		columnVals.put("month", month);
		columnVals.put("week", week);
		columnVals.put("day", day);
		columnVals.put("hour",hour);
		columnVals.put("year", year);
		return columnVals;
	}
	
	public static ZonedDateTime getDateTime(long time)
	{
		if(time==-1)
		{
			//for getting current time
			return getDateTime();	
		}
		return ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), getZoneId());
	}
	
	public static ZonedDateTime getDateTime()
	{
		 return ZonedDateTime.now(getZoneId());
	}
	
	public static long getCurrenTime()
	{   //this will give currentTimestamp in millis
		//return System.currentTimeMillis();
	    return getMillis(LocalDateTime.now(getZoneId()),false);	
	}
	
	public static long getDayStartTime(int interval )
	{
		return getMillis(getMidnight().plusDays(interval),true);
	}

	public static long getDayStartTime()
	{
		return getMillis(getMidnight(),true);
	}

	public static long getMonthStartTime()
	{
		//this can also be used as the end range with less than operator
		//for the previous month
		return getMillis(LocalDateTime.of(getMonthFirst(),LocalTime.MIDNIGHT),true);
	}

	public static long getMonthStartTime(int interval)
	{
		//this can also be used as the end range with less than operator 
		//for a particular month by required interval -1.
		return getMillis(LocalDateTime.of(getMonthFirst(),LocalTime.MIDNIGHT).
				plusMonths(interval),true); 
	}

	public static long getWeekStartTime()
	{
		//this can also be used as the end range with less than operator 
		//for the previous week
		return getMillis(LocalDateTime.of(getWeekFirst(),LocalTime.MIDNIGHT),true)	;
	}

	public static long getWeekStartTime(int interval)
	{
		//this can also be used as the end range with less than operator 
		//for a particular week by sending required interval -1..
		return getMillis(LocalDateTime.of(getWeekFirst(),LocalTime.MIDNIGHT).
				plusWeeks(interval),true);
	}

	public static long getYearStartTime()
	{
		//this can also be used as the end range with less than operator
		//for the previous year
		return getMillis(LocalDateTime.of(getYearFirst(),LocalTime.MIDNIGHT),true);
		
	}

	public static long getYearStartTime(int interval)
	{
		//this can also be used as the end range with less than operator 
		//for a particular year by required interval -1.
		return getMillis(LocalDateTime.of(getYearFirst(),LocalTime.MIDNIGHT).
				plusYears(interval),true); 
	}	
	
	public static long getHourStartTime()
	{
		//this can also be used as the end range with less than operator
				//for the previous hour.
		return getMillis(getHourFirst(),true);	
	}

	public static long getHourStartTime(int interval)
	{
		//this can also be used as the end range with less than operator 
				//for a particular hour by required interval -1.
		return getMillis(getHourFirst().plusHours(interval),true);
	}
	
	public static long getStartTime(int day, int month, int year)
	{ 
		return getMillis(ZonedDateTime.of(LocalDateTime.of
				(year, month, day, 0, 0),getZoneId()),true);
	}
	
	public static long getEndTime(int day, int month, int year)
	{ 
		return getMillis(ZonedDateTime.of(LocalDateTime.of
				(year, month, day, 23, 59,59),getZoneId()),true);
	}
	
	public static long getAge(long startTimestamp) {
		return getDaysBetween(startTimestamp, -1);
	}
	
	public static int getDue(long endTimestamp) {
		return getDaysBetween(-1, endTimestamp);
	}
	
	public static int getDaysBetween(long startTimestamp, long endTimestamp) {
		
		 return Period.between(getDateTime(startTimestamp).toLocalDate(), 
				 getDateTime(endTimestamp).toLocalDate()).getDays();
	}
}

