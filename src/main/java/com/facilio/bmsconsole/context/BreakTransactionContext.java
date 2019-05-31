package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class BreakTransactionContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;
	
	private AttendanceContext attendance;
	public AttendanceContext getAttendance() {
		return attendance;
	}
	public void setAttendance(AttendanceContext attendance) {
		this.attendance = attendance;
	}
	
	private BreakContext breakId;
	public BreakContext getBreakId() {
		return breakId;
	}
	public void setBreakId(BreakContext breakId) {
		this.breakId = breakId;
	}
	
	private AttendanceTransactionContext attendanceTransaction;
	public AttendanceTransactionContext getAttendanceTransaction() {
		return attendanceTransaction;
	}
	public void setAttendanceTransaction(AttendanceTransactionContext attendanceTransaction) {
		this.attendanceTransaction = attendanceTransaction;
	}
	
	private long breakStartTime = -1;
	public long getBreakStartTime() {
		return breakStartTime;
	}
	public void setBreakStartTime(long breakStartTime) {
		this.breakStartTime = breakStartTime;
	}
	
	private long breakStopTime = -1;
	public long getBreakStopTime() {
		return breakStopTime;
	}
	public void setBreakStopTime(long breakStopTime) {
		this.breakStopTime = breakStopTime;
	}
	
	private long timeTaken = -1;
	public long getTimeTaken() {
		return timeTaken;
	}
	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}
	

}
