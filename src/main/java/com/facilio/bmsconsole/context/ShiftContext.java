package com.facilio.bmsconsole.context;

import java.time.LocalTime;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

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
}
