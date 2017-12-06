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
	
	
	public static CacheControl getCacheControl(int type)
	{
		return getCacheControl(TYPE.getType(type));
		
	}
		public static CacheControl getCacheControl(TYPE type)
		{
			ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"));
			ZonedDateTime currentmonthend = now.withDayOfMonth(28);
			System.out.println("Month end"+currentmonthend);
			ZonedDateTime currentyearend = now.withDayOfYear(365);
			
			System.out.println("year end"+currentyearend);

			
			int weekday = now.getDayOfWeek().getValue() -1;
			ZonedDateTime weekend = now.plusDays(6 -weekday);
			
			System.out.println("week end"+weekend);

			
			

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
			System.out.println("from = "+from);
			System.out.println("to = "+to);

			long value =  (to.toInstant().toEpochMilli() - from.toInstant().toEpochMilli())/1000;
			return value;
		}
		public static enum TYPE {
			CURRENT_MONTH_END(1),
			CURRENT_WEEK_END(0),	
			NEVER(-1),
			EVER(100),
			CURRENT_YEAR_END(2);
			
			private final int value;

	        TYPE(final int newValue) {
	            value = newValue;
	        }
	        
	        
	        public static TYPE getType(int id) {
	            for (TYPE type : values()) {
	                if (type.value == id) {
	                    return type;
	                }
	            }
	            throw new IllegalArgumentException("Invalid Type id: " + id);
	        }

	        public int getValue() { return value; }
		}
		
		public static void main(String args[]) {
			System.out.println(getCacheControl(2));
		}
		public String toString()
		{
			return this.getCachecontrol();
		}
	}
 
// max-age=2628000, public
 
