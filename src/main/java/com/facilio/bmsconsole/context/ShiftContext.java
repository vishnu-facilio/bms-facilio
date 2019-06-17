package com.facilio.bmsconsole.context;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;

public class ShiftContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	private long siteId;
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	public long getSiteId() {
		return this.siteId;
	}
	
	private long startTime = -1;
	@JSON(serialize = false)
	public LocalTime getStartTimeAsLocalTime() {
		if (startTime >= 0) {
			return LocalTime.ofSecondOfDay(startTime);
		}
		return null;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime.getSecond();
	}
	public void setStartTime(long secondOfDay) {
		this.startTime = secondOfDay;
	}
	
	private long endTime = -1;
	@JSON(serialize = false)
	public LocalTime getEndTimeAsLocalTime() {
		if (endTime >= 0) {
			return LocalTime.ofSecondOfDay(endTime);
		}
		return null;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime.getSecond();
	}
	public void setEndTime(long secondOfDay) {
		this.endTime = secondOfDay;
	}
	
	private Boolean defaultShift;
	public Boolean getDefaultShift() {
		return defaultShift;
	}
	public void setDefaultShift(Boolean defaultShift) {
		this.defaultShift = defaultShift;
	}
	
	private String colorCode;
	public String getColorCode() {
		return colorCode;
	}
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	
	private JSONObject weekend;
	public JSONObject getWeekendJSON() {
		return weekend;
	}
	public String getWeekend() {
		if (weekend != null) {
			return weekend.toJSONString();
		}
		return null;
	}
	public void setWeekend(String s) throws ParseException {
		if (StringUtils.isNotEmpty(s)) {
			this.weekend = (JSONObject) new JSONParser().parse(s);
		}
	}
	public void setWeekend(JSONObject weekend) {
		this.weekend = weekend;
	}
	
	public boolean isWeekend(long timestamp) {
		ZonedDateTime dateTime = DateTimeUtil.getDateTime(timestamp);
		int weekOfMonth = dateTime.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
		if (weekend != null) {
			List<Long> dayList = (List<Long>) weekend.get(String.valueOf(weekOfMonth));
			if (CollectionUtils.isNotEmpty(dayList)) {
				int i = dateTime.getDayOfWeek().getValue();
				return dayList.contains((long) i);
			}
		}
		return false;
	}
}
