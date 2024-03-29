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
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long stopTime = -1;
	public long getStopTime() {
		return stopTime;
	}
	public void setStopTime(long stopTime) {
		this.stopTime = stopTime;
	}
	
	private long timeTaken = -1;
	public long getTimeTaken() {
		return timeTaken;
	}
	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}
	
	private TransactionType transactionType;

	public TransactionType getTransactionTypeEnum() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public int getTransactionType() {
		if (transactionType != null) {
			return transactionType.getValue();
		}
		return -1;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = TransactionType.valueOf(transactionType);
	}
	
	
	public enum TransactionType {
		BREAKSTART,
		BREAKSTOP;

		public int getValue() {
			return ordinal() + 1;
		}

		public static TransactionType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	private int sourceType;
	public int getSourceType() {
		return sourceType;
	}
	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
}
