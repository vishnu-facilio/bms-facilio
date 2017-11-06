package com.facilio.bmsconsole.context;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class SingleDayBusinessHourContext {
	private long parentId;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private DayOfWeek dayOfWeek;
	public int getDayOfWeek() {
		if(dayOfWeek != null) {
			return dayOfWeek.getValue();
		}
		return -1;
	}
	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = DayOfWeek.of(dayOfWeek);
	}
	public DayOfWeek getDayOfWeekEnum() {
		return dayOfWeek;
	}
	
	private LocalTime startTime;
	public LocalTime getStartTimeAsLocalTime() {
		return startTime;
	}
	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}
	public void setStartTime(String value) {
		this.startTime = LocalTime.parse(value);
	}
	public String getStartTime() {
		if(startTime != null) {
			return startTime.toString();
		}
		return null;
	}
	public Time getStartTimeObject() {
		if(startTime != null) {
			return Time.valueOf(startTime);
		}
		return null;
	}
	public void setStartTimeObject(Time startTime) {
		this.startTime = startTime.toLocalTime();	
	}
	
	private LocalTime endTime;
	public LocalTime getEndTimeAsLocalTime() {
		return endTime;
	}
	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	public void setEndTime(String value) {
		this.endTime = LocalTime.parse(value);
	}
	public String getEndTime() {
		if(endTime != null) {
			return endTime.toString();
		}
		return null;
	}
	public Time getEndTimeObject() {
		if(endTime != null) {
			return Time.valueOf(endTime);
		}
		return null;
	}
	public void setEndTimeObject(Time endTime) {
		this.endTime = endTime.toLocalTime();
	}
}
