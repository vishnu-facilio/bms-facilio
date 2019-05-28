package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class AttendanceContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;
	private User user;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	private long checkInTime = -1;
	public long getCheckInTime() {
		return checkInTime;
	}
	public void setCheckInTime(long checkInTime) {
		this.checkInTime = checkInTime;
	}
	
	private long checkOutTime = -1;
	public long getCheckOutTime() {
		return checkOutTime;
	}
	public void setCheckOutTime(long checkOutTime) {
		this.checkOutTime = checkOutTime;
	}
	
	private long workingHours = -1;
	public long getWorkingHours() {
		return workingHours;
	}
	public void setWorkingHours(long workingHours) {
		this.workingHours = workingHours;
	}
	
	private long day = -1;
	public long getDay() {
		return day;
	}
	public void setDay(long day) {
		this.day = day;
	}
	
	private Status status;
	public Status getStatusEnum() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}
	
	public enum Status {
		PRESENT,
		ABSENT,
		LEAVE,
		HOLIDAY;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private long lastCheckInTime = -1;
	public long getLastCheckInTime() {
		return lastCheckInTime;
	}
	public void setLastCheckInTime(long lastCheckInTime) {
		this.lastCheckInTime = lastCheckInTime;
	}
}
