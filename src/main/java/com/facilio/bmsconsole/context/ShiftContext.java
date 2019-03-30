package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

import java.time.LocalTime;
import java.util.List;

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
	
	private Boolean isSameTime;
	public void setIsSameTime(Boolean isSameTime) {
		this.isSameTime = isSameTime;
	}
	
	public Boolean getIsSameTime() {
		return this.isSameTime;
	}
	
	public Boolean isSameTime() {
		return this.isSameTime;
	}
	
	private LocalTime startTime;
	public LocalTime getStartTimeAsLocalTime() {
		return startTime;
	}
	public String getStartTime() {
		if(startTime != null) {
			return startTime.toString();
		}
		return null;
	}
	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}
	public void setStartTime(String value) {
		if (value == null || value.isEmpty()) {return;}
		this.startTime = LocalTime.parse(value);
	}
	
	private LocalTime endTime;
	public LocalTime getEndTimeAsLocalTime() {
		return endTime;
	}
	public String getEndTime() {
		if(endTime != null) {
			return endTime.toString();
		}
		return null;
	}
	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	public void setEndTime(String value) {
		if (value == null || value.isEmpty()) {return;}
		this.endTime = LocalTime.parse(value);
	}
	
	private List<BusinessHourContext> days;
	public List<BusinessHourContext> getDays() {
		return days;
	}
	public void setDays(List<BusinessHourContext> days) {
		this.days = days;
	}
	
	private long businessHoursId;
	public void setBusinessHoursId(long businessHoursId) {
		this.businessHoursId = businessHoursId;
	}
	
	public long getBusinessHoursId() {
		return this.businessHoursId;
	}
}
