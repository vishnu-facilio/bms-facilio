package com.facilio.time;

public class DateRange {
	
	public DateRange() {
		// TODO Auto-generated constructor stub
	}
	
	public DateRange(long startTime, long endTime) {
		// TODO Auto-generated constructor stub
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return startTime+", "+endTime;
	}
}
