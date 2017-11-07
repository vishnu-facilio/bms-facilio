package com.facilio.bmsconsole.context;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.DateTimeUtil;

public class ReadingContext extends ModuleBaseWithCustomFields {

	private long ttime = -1;
	private ZonedDateTime zdt;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
		this.zdt = DateTimeUtil.getDateTime(ttime);
	}
	
	public String getDate() {
		if(zdt != null) {
			return zdt.toLocalDate().toString();
		}
		return null;
	}
	
	public int getYear() {
		if(zdt != null) {
			return zdt.getYear();
		}
		return -1;
	}
	
	public int getMonth() {
		if(zdt != null) {
			return zdt.getMonthValue();
		}
		return -1;
	}
	
	public int getWeek() {
		if(zdt != null) {
			return zdt.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
		}
		return -1;
	}
	
	public int getDay() {
		if(zdt != null) {
			return zdt.getDayOfWeek().getValue();
		}
		return -1;
	}
	
	public int getHour() {
		if(zdt != null) {
			return zdt.getHour();
		}
		return -1;
	}

	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public Map<String, Object> getReadings() {
		return super.getCustomProps();
	}
	public void setReadings(Map<String, Object> readings) {
		super.setCustomProps(readings);
	}
	public void addReading(String key, Object value) {
		super.setCustomProp(key, value);
	}
	public Object getReading(String key) {
		return super.getCustomProp(key);
	}
} 
