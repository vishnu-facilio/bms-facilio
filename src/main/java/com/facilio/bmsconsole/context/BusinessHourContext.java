package com.facilio.bmsconsole.context;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class BusinessHourContext {
	
	
	private long id = -1;
	@JsonGetter("childId")
	public long getId() {
		return id;
	}
	@JsonSetter("childId")
	public void setId(long id) {
		this.id = id;
	}

	private long parentId = -1;
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
	public String getDayOfWeekVal() {
		if(dayOfWeek != null) {
			return dayOfWeek.toString();
		}
		return null;
	}
	
	public DayOfWeek getDayOfWeekEnum() {
		return dayOfWeek;
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
		this.endTime = LocalTime.parse(value);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return dayOfWeek.name()+", "+startTime+" - "+endTime;
	}
}
