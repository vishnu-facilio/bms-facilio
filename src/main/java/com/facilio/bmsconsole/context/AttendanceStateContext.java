package com.facilio.bmsconsole.context;

public class AttendanceStateContext {
	private String type;
	private BreakContext breakContext;
	public AttendanceStateContext() {
	}
	
	public AttendanceStateContext(String type, BreakContext breakContext) {
		super();
		this.type = type;
		this.breakContext = breakContext;
	}

	public BreakContext getBreakContext() {
		return breakContext;
	}
	public void setBreakContext(BreakContext breakContext) {
		this.breakContext = breakContext;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
