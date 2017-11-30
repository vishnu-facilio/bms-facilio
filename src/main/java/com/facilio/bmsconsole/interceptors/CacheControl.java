package com.facilio.bmsconsole.interceptors;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;


public class CacheControl {

	private CacheControl()
	{
		
	}
	private CacheControl(String cachecontrol, String expires) {
		super();
		this.cachecontrol = cachecontrol;
		this.expires = expires;
		
	}
	public String getCachecontrol() {
		return cachecontrol;
	}
	public void setCachecontrol(String cachecontrol) {
		this.cachecontrol = cachecontrol;
	}
	
	public String getExpires() {
		return expires;
	}
	public void setExpires(String expires) {
		this.expires = expires;
	}
	
	private String cachecontrol="no-cache, no-store, must-revalidate";
	private String expires="-1";
	
	
		public static CacheControl getCacheControl(TYPE type)
		{
			ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"));
			ZonedDateTime currentmonthend = now.withDayOfMonth(28);
			ZonedDateTime currentyearend = now.withDayOfYear(365);
			
			int weekday = now.getDayOfWeek().getValue() -1;
			ZonedDateTime weekend = now.plusDays(6 -weekday);
			
			new Date(currentmonthend.toInstant().toEpochMilli());
			

			switch(type)
			{
			case CURRENT_MONTH_END:
				return new CacheControl("max-age="+getSeconds(now,currentmonthend),java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(currentmonthend));
			case CURRENT_YEAR_END:
				return new CacheControl("max-age="+getSeconds(now,currentyearend),java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(currentyearend));	
			case CURRENT_WEEK_END:
				return new CacheControl("max-age="+getSeconds(now,weekend),java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(weekend));
			case EVER:
				return new CacheControl("max-age="+getSeconds(now,currentyearend),java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(now.plusMonths(24)));
			case NEVER:
				return new CacheControl();
			}
			return new CacheControl();
		}
		
		private static long getSeconds(ZonedDateTime from, ZonedDateTime to)
		{
			return (to.toInstant().toEpochMilli() - from.toInstant().toEpochMilli())/1000;
		}
	}
 
// max-age=2628000, public
 enum TYPE {
	CURRENT_MONTH_END,
	CURRENT_WEEK_END,	
	NEVER,
	EVER,
	CURRENT_YEAR_END
}
