package com.facilio.bmsconsole.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateTimeUtil 
{
	
	
	private static ZoneId getZoneId()
	{
		//TODO TimeZone related changes to be done.
		//query the db with orgId for TimeZone
		return ZoneId.systemDefault();
	}
	
	private static LocalDateTime getMidnight()
	{
		return LocalDateTime.of(LocalDate.now(getZoneId()), LocalTime.MIDNIGHT);
	}
	
	private static LocalDate getWeekFirst()
	{
		//TODO Locale related changes to be done.. query the db with orgId for Locale
		return LocalDate.now(getZoneId()).with(WeekFields.of(Locale.getDefault()).dayOfWeek(),1L);
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
	
	public static ZonedDateTime getDateTime(long addedTime)
	{
		 return ZonedDateTime.ofInstant(Instant.ofEpochMilli(addedTime), getZoneId());
	}
	
	public static long getCurrenTime()
	{   //this will give currentTimestamp in millis
		//return System.currentTimeMillis();
	    return getMillis(LocalDateTime.now(getZoneId()),false);	
	}
	
	public static long getDayStartTime(int interval )
	{
		return getMillis(getMidnight().minusDays(interval),true);
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
				minusMonths(interval),true); 
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
				minusWeeks(interval),true);
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
				minusYears(interval),true); 
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
		return getMillis(getHourFirst().minusHours(interval),true);
	}
	
	public long getStartTime(int day, int month, int year)
	{ 
		return getMillis(ZonedDateTime.of
				(LocalDateTime.of(year, month, day, 0, 0),getZoneId()),true);
	}
	
	public long getEndTime(int day, int month, int year)
	{ 
		return getMillis(ZonedDateTime.of
				(LocalDateTime.of(year, month, day, 23, 59,59),getZoneId()),true);
	}
}

