package com.facilio.bmsconsole.context;

public class AttendanceStateContext {
	private String type;
	private long breakId;
	public AttendanceStateContext() {
	}
	
	public AttendanceStateContext(String type, long breakId) {
		super();
		this.type = type;
		this.breakId = breakId;
	}

	public long getBreakId() {
		return breakId;
	}
	public void setBreakId(long breakId) {
		this.breakId = breakId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
