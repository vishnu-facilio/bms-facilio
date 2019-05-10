package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.db.criteria.DateRange;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.ScheduleInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.*;
import java.util.*;


public class DateTimeUtil 
{
	private static final Logger LOGGER = LogManager.getLogger(DateTimeUtil.class.getName());
	
	public static final long ONE_HOUR_MILLIS_VALUE = 3600000l;
	public static final long ONE_MINUTE_MILLIS_VALUE = 60000l;
	private static ZoneId getZoneId()
	{
		//TODO TimeZone related changes to be done.
		Organization org = AccountUtil.getCurrentOrg();
		if(org != null) {
			String zone = org.getTimezone();
			if(zone != null && !zone.isEmpty()) {
				return ZoneId.of(zone.trim());
			}
		}
		return AwsUtil.isDevelopment() ? ZoneId.systemDefault() : ZoneId.of("Z");
	}
	
	public static Locale getLocale()
	{
		//TODO Locale related changes to be done..
		//like OrgInfo.getCurrentOrgInfo().getLocale() & set the Locale..
		if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getCountry() != null && !"".equalsIgnoreCase(AccountUtil.getCurrentOrg().getCountry().trim())) {
			return new Locale("en", AccountUtil.getCurrentOrg().getCountry());
		}
		return Locale.US;
	}
	
	private static ZonedDateTime getMidnight(ZoneId zoneId)
	{
		return LocalDate.now(zoneId).atStartOfDay(zoneId).withLaterOffsetAtOverlap();// ZonedDateTime.of(LocalDate.now(zoneId), LocalTime.MIDNIGHT,zoneId);
	}
	
	private static LocalDate getWeekFirst(ZoneId zoneId, Locale locale)
	{
		return LocalDate.now(zoneId).with(WeekFields.of(locale).dayOfWeek(),1L);
	}
	
	private static LocalDate getMonthFirst(ZoneId zoneId)
	{
		return LocalDate.now(zoneId).with(TemporalAdjusters.firstDayOfMonth());
	}
	
	private static LocalDate getMonthLast(ZoneId zoneId)
	{
		return LocalDate.now(zoneId).with(TemporalAdjusters.lastDayOfMonth());
	}
	
	private static LocalDate getYearFirst(ZoneId zoneId)
	{
		return LocalDate.now(zoneId).with(TemporalAdjusters.firstDayOfYear());
	}
	
	public static long getMillis(ZonedDateTime ldTime, boolean resetMillis )
	{	
		if(resetMillis)
		{
			//Since it is midnight or start of the day, no harm in multiplying by 1000 to getMillis;
			return ldTime.toEpochSecond()*1000;
		}
		return ldTime.toInstant().toEpochMilli();
	}
	
	public static long getSeconds(ZonedDateTime ldTime)
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
		HashMap<String,Object> columnVals = new LinkedHashMap<String, Object>() ;
		columnVals.put("date",date);
		columnVals.put("week",week);
		columnVals.put("day",zdt.getDayOfWeek().getValue());
		columnVals.put("month",zdt.getMonthValue());
		columnVals.put("hour",zdt.getHour());
		columnVals.put("year",zdt.getYear());
		return columnVals;
	}
	
	//if format is null it will take the 
	//default yyyy-MM-dd format for parsing the date String..
	public static long getDayStartTime(String date,String format, Boolean... seconds)
	{
		ZonedDateTime zdt= ZonedDateTime.of(LocalDate.parse(date,getDateTimeFormat(format)),LocalTime.MIDNIGHT,getZoneId());
	     return getLong(zdt,true,seconds);
	}

	//if format is null it will take the 
	//default yyyy-MM-dd format for parsing the date String..
	
	public static Instant getTimeInstant(String datePattern, String dateString) {
		DateTimeFormatter FMT = new DateTimeFormatterBuilder()
			    .appendPattern(datePattern)
			    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
			    .parseDefaulting(ChronoField.MONTH_OF_YEAR,1)
			    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
			    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
			    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
			    .toFormatter()
			    .withZone(DateTimeUtil.getZoneId())
			    .withLocale(DateTimeUtil.getLocale());
		
		Instant instant = FMT.parse(dateString, Instant::from);
		return instant;
	}
	public static long getDayEndTime(String date,String format,Boolean... seconds)
	{
		ZonedDateTime zdt= ZonedDateTime.of(LocalDate.parse(date,getDateTimeFormat(format)),LocalTime.MAX,getZoneId());
		return getLong(zdt,false,seconds);
	}

	public static DateTimeFormatter getDateTimeFormat(String format)
	{
		if(format==null) {
			return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", getLocale());
		}
		return DateTimeFormatter.ofPattern(format, getLocale());
	}
	
	public static WeekFields getWeekFields() {
		return WeekFields.of(getLocale());
	}
	
	public static ZoneId getZoneId(String zoneId)
	{
		return ZoneId.of(zoneId);
	}
	
	public static Locale getLocale(String language, String country)
	{
		return new Locale(language, country);
	}
	
	public static HashMap<String,Object> getTimeData(long addedTime, Boolean... seconds)
	{
		return getTimeData(getZoneId(),addedTime, seconds);
	}
	
	public static ZonedDateTime getZonedDateTime(long addedTime, Boolean... seconds)
	{
		return getDateTime(getZoneId(),addedTime,seconds);
	}
	
	public static long addYears(long time,int yearCount) {
		return getLong(getZonedDateTime(time).plusYears((long)yearCount),true);
	}
	public static long addMonths(long time,int monthCount) {
		return getLong(getZonedDateTime(time).plusMonths((long)monthCount),true);
	}
	
	public static long addWeeks(long time,int weekCount) {
		return getLong(getZonedDateTime(time).plusWeeks((long)weekCount),true);
	}
	public static long addDays(long time,int dayCount) {
		return getLong(getZonedDateTime(time).plusDays((long)dayCount),true);
	}
	public static long addHours(long time,int hourCount) {
		return getLong(getZonedDateTime(time).plusHours((long)hourCount),true);
	}
	public static long addMinutes(long time,int minCount) {
		return getLong(getZonedDateTime(time).plusMinutes((long)minCount),true);
	}
	public static long addSeconds(long time,int hourCount) {
		return getLong(getZonedDateTime(time).plusSeconds((long)hourCount),true);
	}
	public static long utcTimeToOrgTime(long utcTime) {
		
		String orgTimeZone = getDateTime().getOffset().toString().equalsIgnoreCase("Z") ? "+00:00":DateTimeUtil.getDateTime().getOffset().toString();
		long timeZoneMillis =  timeZonetoMin(orgTimeZone) * (60*1000);
		return utcTime - timeZoneMillis;
	}
	public static long timeZonetoMin(String timeZone) { //eg: -10:30, +00:00, +05:30
		String[] res = timeZone.split(":");
		
		String hourString = res[0];
		String minString = res[1];
		
		Long hourToMins = Long.parseLong(hourString) * 60;
		long mins;
		if(hourToMins >= 0) {
			mins = hourToMins + Long.parseLong(minString);
		}
		else {
			mins = hourToMins - Long.parseLong(minString);
		}
		System.out.println(mins);
		return mins;
	}
	public static HashMap<String,Object> getTimeData(ZoneId zoneId,long addedTime, Boolean... seconds)
	{
		ZonedDateTime zdt = getDateTime(zoneId,addedTime,seconds);
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
		ZonedDateTime zdt= ZonedDateTime.now(zoneId);
		return getLong(zdt,false,seconds);
		
	}
	
	public static long getDayStartTime(int interval,Boolean... seconds )
	{	
		return getDayStartTime(getZoneId(), interval, seconds);
	}
	
	public static long getDayStartTime(ZoneId zoneId,int interval,Boolean... seconds )
	{
		ZonedDateTime zdt=getMidnight(zoneId).plusDays(interval);
		return getLong(zdt,true,seconds);
	}

	public static long getDayStartTime(Boolean... seconds)
	{
		return getDayStartTime(getZoneId(), seconds);
	}
	public static long getDayStartTime(ZoneId zoneId, Boolean... seconds)
	{
		ZonedDateTime zdt=getMidnight(zoneId);
		return getLong(zdt,true,seconds);
	}
	public static long getDayStartTime(ZonedDateTime zdt, Boolean... seconds) {
		return getLong(getDayStartZDT(zdt), true, seconds);
	}
	
	
	public static ZonedDateTime getDayStartZDT(int interval) {	
		return getDayStartZDT(getZoneId(), interval);
	}
	public static ZonedDateTime getDayStartZDT(ZoneId zoneId,int interval) {
		ZonedDateTime zdt=getMidnight(zoneId).plusDays(interval);
		return zdt;
	}
	public static ZonedDateTime getDayStartZDT() {
		return getDayStartZDT(getZoneId());
	}
	public static ZonedDateTime getDayStartZDT(ZoneId zoneId) {
		return getDayStartZDT(getZoneId(), 0);
	}
	public static ZonedDateTime getDayStartZDT(ZonedDateTime zdt) {
		return zdt.toLocalDate().atStartOfDay().atZone(zdt.getZone()).withLaterOffsetAtOverlap();
	}
	public static ZonedDateTime getDayEndZDT(ZonedDateTime zdt) {
		return zdt.toLocalDate().atStartOfDay().plusHours(24).minusNanos(1).atZone(zdt.getZone()).withLaterOffsetAtOverlap();
	}

	public static long getMonthStartTime(Boolean... seconds)
	{
		return getMonthStartTime(getZoneId(), seconds);
	}
	
	public static long getAnyYearThisMonthStartTime(int interval, Boolean... seconds)
	{
		return getAnyYearThisMonthStartTime(getZoneId(), interval, seconds);
	}
	
	public static long getAnyYearThisMonthStartTime(ZoneId zoneId, int interval, Boolean... seconds)
	{
		ZonedDateTime zdt=ZonedDateTime.of(getAnyYearThisMonthFirst(zoneId,interval),LocalTime.MIDNIGHT,zoneId);
		return getLong(zdt,true,seconds);
	}
	
	private static LocalDate getAnyYearThisMonthFirst(ZoneId zoneId, int interval)
	{
		return getMonthFirst(zoneId).plusYears(interval);
	}
	
	
	private static long getLong(ZonedDateTime zdt,boolean resetMillis, Boolean...seconds)
	{
		if(seconds!=null && seconds.length>0 && seconds[0]==true) {
			return getSeconds(zdt);
		}
		return getMillis(zdt,true);
		
	}
	
	public static ZonedDateTime getQuarterStartTimeOf (ZonedDateTime zdt) {
		return getDayStartZDT(zdt.with(zdt.getMonth().firstMonthOfQuarter()).with(TemporalAdjusters.firstDayOfMonth()));
	}
	public static ZonedDateTime getQuarterEndTimeOf (ZonedDateTime zdt) {
		return getDayEndZDT(zdt.with(zdt.getMonth().firstMonthOfQuarter()).plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()));
	}
	
	public static long getQuarterStartTimeOf(long milliseconds) {
		LocalDateTime day = LocalDateTime.ofInstant(Instant.ofEpochMilli(getDayStartTimeOf(milliseconds)), getZoneId());
		LocalDateTime firstQuarter = day.with(day.getMonth().firstMonthOfQuarter()).with(TemporalAdjusters.firstDayOfMonth());
		return getLong(firstQuarter.atZone(getZoneId()), true);
	}
	
	public static long getQuarterEndTimeOf(long milliseconds) {
		LocalDateTime day = LocalDateTime.ofInstant(Instant.ofEpochMilli(getDayStartTimeOf(milliseconds)), getZoneId());
		LocalDateTime firstQuarter = day.with(day.getMonth().firstMonthOfQuarter()).with(TemporalAdjusters.firstDayOfMonth());
		LocalDateTime lastQuarter = firstQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(LocalTime.MAX);
		return getLong(lastQuarter.atZone(getZoneId()),true);
	}
	
	public static long getQuarterStartTime(int interval) {
		return getQuarterStartTime(getZoneId(), interval).toInstant().toEpochMilli();
	}
	public static ZonedDateTime getQuarterStartTime(ZoneId zoneId, int interval) {
		LocalDateTime day = LocalDateTime.ofInstant(Instant.ofEpochMilli(getDayStartTime()), zoneId);
		LocalDateTime thisQuarter = day.with(day.getMonth().firstMonthOfQuarter()).with(TemporalAdjusters.firstDayOfMonth());
		LocalDateTime quarterStart = thisQuarter.plusMonths(interval * 3).with(TemporalAdjusters.firstDayOfMonth());
		return quarterStart.atZone(zoneId);
	}
	public static long getQuarterEndTime(int interval) {
		return getQuarterEndTime(getZoneId(), interval).toInstant().toEpochMilli();
	}
	public static ZonedDateTime getQuarterEndTime(ZoneId zoneId, int interval) {
		LocalDateTime quarterStart = getQuarterStartTime(zoneId, interval).toLocalDateTime();
		LocalDateTime quarterEnd = quarterStart.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(LocalTime.MAX);
		return quarterEnd.atZone(zoneId);
	}
	public static long getAnyYearThisMonthEndTime(int interval, Boolean... seconds)
	{
		return getAnyYearThisMonthEndTime(getZoneId(),interval,seconds);
	}
	
	public static long getAnyYearThisMonthEndTime(ZoneId zoneId, int interval, Boolean... seconds)
	{
		ZonedDateTime zdt=ZonedDateTime.of(getAnyYearThisMonthEnd(zoneId,interval),LocalTime.MAX,zoneId);
		return getLong(zdt,false,seconds);
	}
	
	private static LocalDate getAnyYearThisMonthEnd(ZoneId zoneId, int interval)
	{
		return getMonthLast(zoneId).plusYears(interval);
	}
	public static long getMonthStartTime(ZoneId zoneId,Boolean... seconds)
	{
		return getMonthStartTime(zoneId,0,seconds);
	}

	public static long getMonthStartTime(int interval, Boolean... seconds)
	{
		return getMonthStartTime(getZoneId(), interval, seconds);
	}
	
	public static long getMonthStartTime(ZoneId zoneId, int interval, Boolean... seconds)
	{
		//this can also be used as the end range with less than operator 
		//for a particular month by required interval -1.
		
		ZonedDateTime zdt=ZonedDateTime.of(getMonthFirst(zoneId),LocalTime.MIDNIGHT,zoneId).
				plusMonths(interval);
		return getLong(zdt,true,seconds);
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
		return getWeekStartTime(zoneId,locale,0,seconds);
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
		ZonedDateTime zdt=ZonedDateTime.of(getWeekFirst(zoneId,locale),LocalTime.MIDNIGHT,zoneId).
				plusWeeks(interval);
		return getLong(zdt,true,seconds);
	}

	public static long getYearStartTime(Boolean... seconds)
	{
		return getYearStartTime(getZoneId(), seconds);
	}
	
	public static long getYearStartTime(ZoneId zoneId,Boolean... seconds)
	{
		
		return getYearStartTime(zoneId,0,seconds);
	}

	public static long getYearStartTime(int interval, Boolean... seconds)
	{
		return getYearStartTime(getZoneId(), interval, seconds);
	}	
	
	public static long getYearStartTime(ZoneId zoneId,int interval, Boolean... seconds)
	{
		//this can also be used as the end range with less than operator 
		//for a particular year by required interval -1.
		ZonedDateTime zdt=ZonedDateTime.of(getYearFirst(zoneId),LocalTime.MIDNIGHT,zoneId).
				plusYears(interval);
		return getLong(zdt,true,seconds);
	}	
	
	public static long getHourStartTime(Boolean... seconds)
	{
		return getHourStartTime(getZoneId(), seconds);
	}
	
	public static long getHourStartTime(ZoneId zoneId,Boolean... seconds)
	{
		return getHourStartTime(zoneId,0,seconds);
	}

	public static long getHourStartTime(int interval,Boolean... seconds)
	{
		return getHourStartTime(getZoneId(), interval, seconds);
	}
	
	public static long getHourStartTime(ZoneId zoneId,int interval,Boolean... seconds)
	{
		//this can also be used as the end range with less than operator 
				//for a particular hour by required interval -1.
		ZonedDateTime zdt=getHourFirst(zoneId).plusHours(interval);
		return getLong(zdt,true,seconds);
	}
	
	public static long getMonthStartTime(int month, int year,Boolean... seconds)
	{ 
		return getStartTime(getZoneId(), 1, month, year, seconds);
	}
	
	public static long getMonthEndTime(int month, int year,Boolean... seconds)
	{ 
		LocalDate date=LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth());
		ZonedDateTime zdt=ZonedDateTime.of(date, LocalTime.MAX, getZoneId());
		return getLong(zdt,false,seconds);
	}
	
	
	public static long getStartTime(int day, int month, int year,Boolean... seconds)
	{ 
		return getStartTime(getZoneId(), day, month, year, seconds);
	}
	
	public static long getStartTime(ZoneId zoneId,int day, int month, int year,Boolean... seconds)
	{ 
		LocalDate date=LocalDate.of(year, month, day);
		ZonedDateTime zdt=ZonedDateTime.of(date, LocalTime.MIDNIGHT, zoneId);
		return getLong(zdt,true,seconds);
	}
	
	public static long getEndTime(int day, int month, int year,Boolean... seconds)
	{ 
		return getEndTime(getZoneId(), day, month, year, seconds);
	}
	
	public static long getEndTime(ZoneId zoneId, int day, int month, int year,Boolean... seconds)
	{ 
		LocalDate date=LocalDate.of(year, month, day);
		ZonedDateTime zdt=ZonedDateTime.of(date, LocalTime.MAX, zoneId);
		return getLong(zdt,false,seconds);
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
	
	public static long getTime(String timeStamp, Boolean... seconds)
	{
		//eg: timeStamp="2017-08-09T10:06:10.894752+04:00"; for Muscat /Dubai
		//    timeStamp="2017-08-09T10:06:10.894752Z"; for UTC
		return getTime(timeStamp,getDateTimeFormat(null),seconds);
	}
	
	public static long getTime(String timeStamp, String format, Boolean... seconds)
	{
		//eg: timeStamp="2017-08-09T10:06:10.894752+04:00"; for Muscat /Dubai
		//    timeStamp="2017-08-09T10:06:10.894752Z";
		return getTime(timeStamp,getDateTimeFormat(format),seconds);
		
	}
	
	private static long getTime(String timeStamp, DateTimeFormatter dateFormat,Boolean... seconds ) {
		
		LocalDateTime dateTime = LocalDateTime.parse(timeStamp, dateFormat);
		ZonedDateTime zDateTime= dateTime.atZone(getZoneId());
		return getLong(zDateTime,false,seconds);
	}
	
	public static String getFormattedTime(long time) {
		return DateTimeUtil.getZonedDateTime(time).format(FacilioConstants.READABLE_DATE_FORMAT);
	}
	
	public static String getFormattedTime(long time, String format) {
		return DateTimeUtil.getZonedDateTime(time).format(DateTimeFormatter.ofPattern(format));
	}
	
	public static long getLastNMinute(long currentTime,int minute) {
		return currentTime - (minute * ONE_MINUTE_MILLIS_VALUE);
	}
	
	public static long getLastNHour(long currentTime,int hour) {
		return currentTime - (hour * ONE_HOUR_MILLIS_VALUE);
	}
	
	public static boolean isSameHour (long startTime, long endTime) {
		ZonedDateTime start = getDateTime(startTime);
		ZonedDateTime end = getDateTime(endTime);
		return isSameHour(start, end);
	}
	public static boolean isSameHour (ZonedDateTime start, ZonedDateTime end) {
		return isSameDay(start, end) && start.getHour() == end.getHour();
	}
	
	public static boolean isSameDay (long startTime, long endTime) {
		ZonedDateTime start = getDateTime(startTime);
		ZonedDateTime end = getDateTime(endTime);
		return isSameDay(start, end);
	}
	public static boolean isSameDay (ZonedDateTime start, ZonedDateTime end) {
		return start.toLocalDate().equals(end.toLocalDate());
	}
	
	public static boolean isSameWeek (long startTime, long endTime) {
		ZonedDateTime start = getDateTime(startTime);
		ZonedDateTime end = getDateTime(endTime);
		return isSameWeek(start, end);
	}
	public static boolean isSameWeek (ZonedDateTime start, ZonedDateTime end) {
		WeekFields weekFields = getWeekFields();
		return isSameYear(start, end) && start.get(weekFields.weekOfYear()) == end.get(weekFields.weekOfYear());
	}
	
	public static boolean isSameMonth (long startTime, long endTime) {
		ZonedDateTime start = getDateTime(startTime);
		ZonedDateTime end = getDateTime(endTime);
		return isSameMonth(start, end);
	}
	public static boolean isSameMonth (ZonedDateTime start, ZonedDateTime end) {
		return isSameYear(start, end) && Month.from(start) == Month.from(end);
	}
	
	public static boolean isSameQuarter (long startTime, long endTime) {
		ZonedDateTime start = getDateTime(startTime);
		ZonedDateTime end = getDateTime(endTime);
		return isSameQuarter(start, end);
	}
	public static boolean isSameQuarter (ZonedDateTime start, ZonedDateTime end) {
		return isSameYear(start, end) && start.get(IsoFields.QUARTER_OF_YEAR) == end.get(IsoFields.QUARTER_OF_YEAR);
	}
	
	public static boolean isSameYear (long startTime, long endTime) {
		ZonedDateTime start = getDateTime(startTime);
		ZonedDateTime end = getDateTime(endTime);
		return isSameYear(start, end);
	}
	public static boolean isSameYear (ZonedDateTime start, ZonedDateTime end) {
		return Year.from(start).equals(Year.from(end));
	}
	
	public static long getHourStartTimeOf(long time) {
		return getHourStartTimeOf(time, false);
	}
	public static long getHourStartTimeOf(long time, boolean isEpochSecond) {
		ZonedDateTime zdt = getHourStartTimeOf(getDateTime(time, isEpochSecond));
		return getLong(zdt, true, isEpochSecond);
	}
	public static ZonedDateTime getHourStartTimeOf(ZonedDateTime zdt) {
		return zdt.truncatedTo(ChronoUnit.HOURS);
	}
	
	public static long getHourEndTimeOf(long time) {
		return getHourEndTimeOf(time, false);
	}
	public static long getHourEndTimeOf(long time, boolean isEpochSecond) {
		ZonedDateTime zdt = getHourEndTimeOf(getDateTime(time, isEpochSecond));
		return getLong(zdt, true, isEpochSecond);
	}
	public static ZonedDateTime getHourEndTimeOf(ZonedDateTime zdt) {
		return zdt.plusHours(1).truncatedTo(ChronoUnit.HOURS).minusNanos(1);
	}
	
	public static long getDayStartTimeOf(long time) {
		return getDayStartTimeOf(time, false);
	}
	public static long getDayStartTimeOf(long time, boolean isEpochSecond) {
		ZonedDateTime zdt = getDayStartTimeOf(getDateTime(time, isEpochSecond));
		return getLong(zdt, true, isEpochSecond);
	}
	public static ZonedDateTime getDayStartTimeOf(ZonedDateTime zdt) {
		return zdt.toLocalDate().atTime(LocalTime.MIDNIGHT).atZone(getZoneId());
	}
	
	public static long getDayEndTimeOf(long time) {
		return getDayEndTimeOf(time, false);
	}
	public static long getDayEndTimeOf(long time, boolean isEpochSecond) {
		ZonedDateTime zdt = getDayEndTimeOf(getDateTime(time, isEpochSecond));
		return getLong(zdt, true, isEpochSecond);
	}
	public static ZonedDateTime getDayEndTimeOf(ZonedDateTime zdt) {
		return zdt.toLocalDate().atTime(LocalTime.MAX).atZone(getZoneId());
	}
	
	public static long getWeekStartTimeOf(long time) {
		return getWeekStartTimeOf(time, false);
	}
	public static long getWeekStartTimeOf(long time, boolean isEpochSecond) {
		ZonedDateTime zdt = getWeekStartTimeOf(getDateTime(time, isEpochSecond));
		return getLong(zdt, true, isEpochSecond);
	}
	public static ZonedDateTime getWeekStartTimeOf(ZonedDateTime zdt) {
		return zdt.toLocalDate().atTime(LocalTime.MIDNIGHT).atZone(getZoneId()).with(getWeekFields().dayOfWeek(),1);
	}
	
	public static long getWeekEndTimeOf(long time) {
		return getWeekEndTimeOf(time, false);
	}
	public static long getWeekEndTimeOf(long time, boolean isEpochSecond) {
		ZonedDateTime zdt = getWeekEndTimeOf(getDateTime(time, isEpochSecond));
		return getLong(zdt, true, isEpochSecond);
	}
	public static ZonedDateTime getWeekEndTimeOf(ZonedDateTime zdt) {
		return zdt.toLocalDate().atTime(LocalTime.MAX).atZone(getZoneId()).with(getWeekFields().dayOfWeek(),7);
	}
	
	public static long getMonthStartTimeOf(long time) {
		return getMonthStartTimeOf(time, false);
	}
	public static long getMonthStartTimeOf(long time, boolean isEpochSecond) {
		ZonedDateTime zdt = getMonthStartTimeOf(getDateTime(time, isEpochSecond));
		return getLong(zdt, true, isEpochSecond);
	}
	public static ZonedDateTime getMonthStartTimeOf(ZonedDateTime zdt) {
		return zdt.toLocalDate().atTime(LocalTime.MIDNIGHT).atZone(getZoneId()).with(TemporalAdjusters.firstDayOfMonth());
	}
	
	public static long getMonthEndTimeOf(long time) {
		return getMonthEndTimeOf(time, false);
	}
	public static long getMonthEndTimeOf(long time, boolean isEpochSecond) {
		ZonedDateTime zdt = getMonthEndTimeOf(getDateTime(time, isEpochSecond));
		return getLong(zdt, true, isEpochSecond);
	}
	public static ZonedDateTime getMonthEndTimeOf(ZonedDateTime zdt) {
		return zdt.toLocalDate().atTime(LocalTime.MAX).atZone(getZoneId()).with(TemporalAdjusters.lastDayOfMonth());
	}
	
	public static long getYearStartTimeOf(long time) {
		return getYearStartTimeOf(time, false);
	}
	public static long getYearStartTimeOf(long time, boolean isEpochSecond) {
		ZonedDateTime zdt = getYearStartTimeOf(getDateTime(time, isEpochSecond));
		return getLong(zdt, true, isEpochSecond);
	}
	public static ZonedDateTime getYearStartTimeOf(ZonedDateTime zdt) {
		return zdt.toLocalDate().atTime(LocalTime.MIDNIGHT).atZone(getZoneId()).with(TemporalAdjusters.firstDayOfYear());
	}
	
	public static long getYearEndTimeOf(long time) {
		return getYearEndTimeOf(time, false);
	}
	public static long getYearEndTimeOf(long time, boolean isEpochSecond) {
		ZonedDateTime zdt = getYearEndTimeOf(getDateTime(time, isEpochSecond));
		return getLong(zdt, true, isEpochSecond);
	}
	public static ZonedDateTime getYearEndTimeOf(ZonedDateTime zdt) {
		return zdt.toLocalDate().atTime(LocalTime.MAX).atZone(getZoneId()).with(TemporalAdjusters.lastDayOfYear());
	}
	
	
	public static List<DateRange> getTimeIntervals(long startTime, long endTime, int minutesInterval){

//		startTime=removeMillisPrecision(startTime, true);
//		endTime=removeMillisPrecision(endTime, false);

		long interval=minutesInterval*60*1000;
		long modTime=startTime+interval;
		if(startTime>=endTime) {
			return null;
		}
		List<DateRange> intervals = new ArrayList<>();

		while(modTime<endTime) {
			intervals.add(new DateRange(startTime, modTime - 1));
			startTime=modTime;
			modTime=modTime+interval;
		}
		if(startTime<endTime) {
			intervals.add(new DateRange(startTime, endTime));
		}
		return intervals;
	}
	
	public static List<DateRange> getTimeIntervals(long startTime, long endTime, ScheduleInfo schedule) {
		List<DateRange> intervals = new ArrayList<>();
		startTime = startTime/1000;
		endTime = endTime/1000;
		
		long currentEndTime = schedule.nextExecutionTime(startTime);
		while (currentEndTime <= endTime) {
			intervals.add(new DateRange(startTime*1000, (currentEndTime*1000)-1));
			startTime = currentEndTime;
			currentEndTime = schedule.nextExecutionTime(startTime);
		}
		return intervals;
	}
	
	private static Long removeMillisPrecision(long time, boolean floor) {
		try {
			if(floor) {
				return (long)Math.floor(time/1000)*1000;
			}
			return (long) Math.ceil(time/1000)*1000;
		}catch(Exception e) {
			return time;
		}
	}
	
	private static final List<String> sunWeekendDaysCountries = Arrays.asList(new String[]{"GQ", "IN", "TH", "UG"});
	private static final List<String> fryWeekendDaysCountries = Arrays.asList(new String[]{"DJ", "IR"});
	private static final List<String> frySunWeekendDaysCountries = Arrays.asList(new String[]{"BN"});
	private static final List<String> thuFryWeekendDaysCountries = Arrays.asList(new String[]{"AF"});
	private static final List<String> frySatWeekendDaysCountries = Arrays.asList(new String[]{"AE", "DZ", "BH", "BD", "EG", "IQ", "IL", "JO", "KW", "LY", "MV", "MR", "OM", "PS", "QA", "SA", "SD", "SY", "YE"});

	public static int[] getWeekendDays(Locale locale) {
		if (locale == null) {
			locale = getLocale();
		}
		
	    if (thuFryWeekendDaysCountries.contains(locale.getCountry())) {
	        return new int[]{Calendar.THURSDAY, Calendar.FRIDAY};
	    }
	    else if (frySunWeekendDaysCountries.contains(locale.getCountry())) {
	        return new int[]{Calendar.FRIDAY, Calendar.SUNDAY};
	    }
	    else if (fryWeekendDaysCountries.contains(locale.getCountry())) {
	        return new int[]{Calendar.FRIDAY};
	    }
	    else if (sunWeekendDaysCountries.contains(locale.getCountry())) {
	        return new int[]{Calendar.SUNDAY};
	    }
	    else if (frySatWeekendDaysCountries.contains(locale.getCountry())) {
	        return new int[]{Calendar.FRIDAY, Calendar.SATURDAY};
	    }
	    else {
	        return new int[]{Calendar.SATURDAY, Calendar.SUNDAY};
	    }
	}
}

