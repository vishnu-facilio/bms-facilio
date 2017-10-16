package com.facilio.bmsconsole.util;

import java.sql.Timestamp;
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
import java.util.LinkedHashMap;
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
	
	private static ZonedDateTime getMidnight(ZoneId zoneId)
	{
		return ZonedDateTime.of(LocalDate.now(zoneId), LocalTime.MIDNIGHT,zoneId);
	}
	
	private static LocalDate getWeekFirst(ZoneId zoneId, Locale locale)
	{
		return LocalDate.now(zoneId).with(WeekFields.of(locale).dayOfWeek(),1L);
	}
	
	private static LocalDate getMonthFirst(ZoneId zoneId)
	{
		return LocalDate.now(zoneId).with(TemporalAdjusters.firstDayOfMonth());
	}
	
	private static LocalDate getYearFirst(ZoneId zoneId)
	{
		return LocalDate.now(zoneId).with(TemporalAdjusters.firstDayOfYear());
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
	
	private static long getSeconds(ZonedDateTime ldTime)
	{	
		return ldTime.toEpochSecond();
	}
		
	private static ZonedDateTime getHourFirst(ZoneId zoneId)
	{
		LocalTime lTime =LocalTime.now(zoneId);
		lTime=LocalTime.of(lTime.getHour(), 0);
		return ZonedDateTime.of(LocalDate.now(zoneId), lTime, zoneId);
	}
	
	public static HashMap<String, Object> getTimeData(ZonedDateTime zdt) {
		int week=zdt.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
		String date=zdt.toLocalDate().toString();
		String day=zdt.getDayOfWeek().toString();
		HashMap<String,Object> columnVals = new LinkedHashMap<String, Object>() ;
		columnVals.put("date",date);
		columnVals.put("week",week);
		columnVals.put("day",day);
		columnVals.put("month",zdt.getMonthValue());
		columnVals.put("hour",zdt.getHour());
		columnVals.put("year",zdt.getYear());
		return columnVals;
	}
	
	public static ZoneId getZoneId(String zoneId)
	{
		return ZoneId.of(zoneId);
	}
	
	public static Locale getLocale(String language, String country)
	{
		return new Locale(language, country);
	}
	
	public static HashMap<String,Object> getTimeData(long addedTime)
	{
		return getTimeData(getZoneId(),addedTime);
	}
	
	public static HashMap<String,Object> getTimeData(ZoneId zoneId,long addedTime)
	{
		ZonedDateTime zdt = getDateTime(zoneId,addedTime,true);
		return getTimeData(zdt);
	}

	public static HashMap<String,Object> getTimeData(ZoneId zoneId,String timeStamp)
	{
		LocalDateTime ldt = Timestamp.valueOf(timeStamp).toLocalDateTime();
		ZonedDateTime zdt= ZonedDateTime.of(ldt, zoneId);
		return getTimeData(zdt);
	}
	
	public static ZonedDateTime getDateTime(long time, Boolean... seconds)
	{
		return getDateTime(getZoneId(),time, seconds);
	}
	
	public static ZonedDateTime getDateTime(ZoneId zoneId,long time, Boolean... seconds)
	{
		if(time==-1){
			//for getting current time
			return getDateTime(zoneId);	
		}
		if(seconds!=null && seconds.length>0 && seconds[0]==true){
			return ZonedDateTime.ofInstant(Instant.ofEpochSecond(time), zoneId);
		}
		return ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), zoneId);
	}
	
	public static ZonedDateTime getDateTime()
	{
		 return getDateTime(getZoneId());
	}
	
	public static ZonedDateTime getDateTime(ZoneId zoneId)
	{
		 return ZonedDateTime.now(zoneId);
	}
	
	public static long getCurrenTime(Boolean... seconds)
	{   
		return getCurrenTime(getZoneId(), seconds);
	}
	
	public static long getCurrenTime(ZoneId zoneId,Boolean... seconds)
	{   
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(ZonedDateTime.now(zoneId));
		}
	    return getMillis(ZonedDateTime.now(zoneId),false);	
	}
	
	public static long getDayStartTime(int interval,Boolean... seconds )
	{	
		return getDayStartTime(getZoneId(), interval, seconds);
	}
	
	public static long getDayStartTime(ZoneId zoneId,int interval,Boolean... seconds )
	{
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(getMidnight(zoneId).plusDays(interval));
		}
		return getMillis(getMidnight(zoneId).plusDays(interval),true);
	}

	public static long getDayStartTime(Boolean... seconds)
	{
		return getDayStartTime(getZoneId(), seconds);
	}
	public static long getDayStartTime(ZoneId zoneId, Boolean... seconds)
	{
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(getMidnight(zoneId));
		}
		return getMillis(getMidnight(zoneId),true);
	}

	public static long getMonthStartTime(Boolean... seconds)
	{
		return getMonthStartTime(getZoneId(), seconds);
	}
	
	public static long getMonthStartTime(ZoneId zoneId,Boolean... seconds)
	{
		//this can also be used as the end range with less than operator
		//for the previous month
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(ZonedDateTime.of(getMonthFirst(zoneId),LocalTime.MIDNIGHT,zoneId));
		}
		return getMillis(ZonedDateTime.of(getMonthFirst(zoneId),LocalTime.MIDNIGHT,zoneId),true);
	}

	public static long getMonthStartTime(int interval, Boolean... seconds)
	{
		return getMonthStartTime(getZoneId(), interval, seconds);
	}
	
	public static long getMonthStartTime(ZoneId zoneId, int interval, Boolean... seconds)
	{
		//this can also be used as the end range with less than operator 
		//for a particular month by required interval -1.
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(ZonedDateTime.of(getMonthFirst(zoneId),LocalTime.MIDNIGHT,zoneId).
					plusMonths(interval));
		}
		return getMillis(ZonedDateTime.of(getMonthFirst(zoneId),LocalTime.MIDNIGHT,zoneId).
				plusMonths(interval),true); 
	}

	public static long getWeekStartTime(Boolean... seconds)
	{
		return getWeekStartTime(getZoneId(), seconds);
	}
	
	public static long getWeekStartTime(ZoneId zoneId,Boolean... seconds)
	{
		return getWeekStartTime(zoneId, getLocale(), seconds);
	}
	
	public static long getWeekStartTime(ZoneId zoneId,Locale locale,Boolean... seconds)
	{
		//this can also be used as the end range with less than operator 
		//for the previous week
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(ZonedDateTime.of(getWeekFirst(zoneId,locale),LocalTime.MIDNIGHT,zoneId))	;
		}
		return getMillis(ZonedDateTime.of(getWeekFirst(zoneId,locale),LocalTime.MIDNIGHT,zoneId),true)	;
	}
	
	public static long getWeekStartTime(int interval,Boolean... seconds)
	{
		return getWeekStartTime(getZoneId(), interval, seconds);
	}
	
	public static long getWeekStartTime(ZoneId zoneId,int interval,Boolean... seconds)
	{
		return getWeekStartTime(zoneId, getLocale(), interval, seconds);
	}
	public static long getWeekStartTime(ZoneId zoneId,Locale locale,int interval,Boolean... seconds)
	{
		//this can also be used as the end range with less than operator 
		//for a particular week by sending required interval -1..
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(ZonedDateTime.of(getWeekFirst(zoneId,locale),LocalTime.MIDNIGHT,zoneId).
					plusWeeks(interval));
		}
		return getMillis(ZonedDateTime.of(getWeekFirst(zoneId,locale),LocalTime.MIDNIGHT,zoneId).
				plusWeeks(interval),true);
	}

	public static long getYearStartTime(Boolean... seconds)
	{
		return getYearStartTime(getZoneId(), seconds);
	}
	
	public static long getYearStartTime(ZoneId zoneId,Boolean... seconds)
	{
		//this can also be used as the end range with less than operator
		//for the previous year
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(ZonedDateTime.of(getYearFirst(zoneId),LocalTime.MIDNIGHT,zoneId));
		}
		return getMillis(ZonedDateTime.of(getYearFirst(zoneId),LocalTime.MIDNIGHT,zoneId),true);
	}

	public static long getYearStartTime(int interval, Boolean... seconds)
	{
		return getYearStartTime(getZoneId(), interval, seconds);
	}	
	
	public static long getYearStartTime(ZoneId zoneId,int interval, Boolean... seconds)
	{
		//this can also be used as the end range with less than operator 
		//for a particular year by required interval -1.
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(ZonedDateTime.of(getYearFirst(zoneId),LocalTime.MIDNIGHT,zoneId).
					plusYears(interval)); 	
		}
		return getMillis(ZonedDateTime.of(getYearFirst(zoneId),LocalTime.MIDNIGHT,zoneId).
				plusYears(interval),true); 
	}	
	
	public static long getHourStartTime(Boolean... seconds)
	{
		return getHourStartTime(getZoneId(), seconds);
	}
	
	public static long getHourStartTime(ZoneId zoneId,Boolean... seconds)
	{
		//this can also be used as the end range with less than operator
		//for the previous hour.
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(getHourFirst(zoneId));
		}
		return getMillis(getHourFirst(zoneId),true);	
	}

	public static long getHourStartTime(int interval,Boolean... seconds)
	{
		return getHourStartTime(getZoneId(), interval, seconds);
	}
	
	public static long getHourStartTime(ZoneId zoneId,int interval,Boolean... seconds)
	{
		//this can also be used as the end range with less than operator 
				//for a particular hour by required interval -1.
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(getHourFirst(zoneId).plusHours(interval));
		}
		return getMillis(getHourFirst(zoneId).plusHours(interval),true);
	}
	
	public static long getStartTime(int day, int month, int year,Boolean... seconds)
	{ 
		return getStartTime(getZoneId(), day, month, year, seconds);
	}
	
	public static long getStartTime(ZoneId zoneId,int day, int month, int year,Boolean... seconds)
	{ 
		ZonedDateTime zdt=ZonedDateTime.of(year, month, day, 0, 0, 0, 0, zoneId);
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(zdt);
		}
		return getMillis(zdt,true);
	}
	
	public static long getEndTime(int day, int month, int year,Boolean... seconds)
	{ 
		return getEndTime(getZoneId(), day, month, year, seconds);
	}
	
	public static long getEndTime(ZoneId zoneId, int day, int month, int year,Boolean... seconds)
	{ 
		ZonedDateTime zdt=ZonedDateTime.of(year, month, day, 23, 59, 59, 0, zoneId);
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(zdt);
		}
		return getMillis(zdt,false);
	}
	
	public static long getAge(long startTimestamp) {
		return getAge(getZoneId(), startTimestamp);
	}
	public static long getAge(ZoneId zoneId,long startTimestamp) {
		return getDaysBetween(zoneId,startTimestamp, -1);
	}
	
	public static int getDue(long endTimestamp) {
		return getDue(getZoneId(), endTimestamp);
	}
	public static int getDue(ZoneId zoneId, long endTimestamp) {
		return getDaysBetween(zoneId,-1, endTimestamp);
	}
	
	public static int getDaysBetween(long startTimestamp, long endTimestamp) {
		
		return getDaysBetween(getZoneId(), startTimestamp, endTimestamp);
	}
	
	public static int getDaysBetween(ZoneId zoneId,long startTimestamp, long endTimestamp) {
		
		LocalDate startDate=getDateTime(zoneId,startTimestamp).toLocalDate();
		LocalDate endDate= getDateTime(zoneId,endTimestamp).toLocalDate();
		return Period.between(startDate, endDate).getDays();
	}
	
	public long getTime(String timeStamp, Boolean... seconds)
	{
		//eg: timeStamp="2017-08-09T10:06:10.894752+04:00"; 
		//    timeStamp="2017-08-09T10:06:10.894752Z";
		ZonedDateTime zDateTime= ZonedDateTime.parse(timeStamp);
		
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return  getSeconds(zDateTime);
		}
		return getMillis(zDateTime, false);
	}
}

