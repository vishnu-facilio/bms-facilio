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
	private int dayOfWeek=-1;

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}
	public DayOfWeek getDayOfWeekEnum() {
		if(dayOfWeek!=-1){
			return DayOfWeek.of(dayOfWeek);
		}
		return null;
	}

	private LocalTime startTime;

	public LocalTime getStartTimeAsLocalTime() {
		return startTime;
	}

	public LocalTime getStartTimeOrDefault() {
		if (startTime != null) {
			return startTime;
		}
		return LocalTime.MIN;
	}

	public String getStartTime() {
		if (startTime != null) {
			return startTime.toString();
		}
		return null;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public void setStartTime(String value) {
		if(value!=null&&value.trim().length()>0){
			this.startTime = LocalTime.parse(value);
		}
	}

	private LocalTime endTime;

	public LocalTime getEndTimeAsLocalTime() {
		return endTime;
	}
	public LocalTime getEndTimeOrDefault() {
		if (endTime != null) {
			return endTime;
		}
		return LocalTime.MAX;
	}

	public String getEndTime() {
		if (endTime != null) {
			return endTime.toString();
		}
		return null;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public void setEndTime(String value) {
		if(value!=null&&value.trim().length()>0){
			this.endTime = LocalTime.parse(value);
		}
	}
}
