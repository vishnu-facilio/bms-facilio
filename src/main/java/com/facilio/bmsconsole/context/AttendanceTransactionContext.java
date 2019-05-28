package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.AttendanceContext.Status;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class AttendanceTransactionContext extends ModuleBaseWithCustomFields {

	private static final long serialVersionUID = 1L;

	private AttendanceContext attendance;

	public AttendanceContext getAttendance() {
		return attendance;
	}

	public void setAttendance(AttendanceContext attendance) {
		this.attendance = attendance;
	}

	private TransactionType transactionType;

	public TransactionType getTransactionTypeEnum() {
		return transactionType;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = TransactionType.valueOf(transactionType);
	}

	public enum TransactionType {
		CHECKIN, 
		CHECKOUT;

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

	private SourceType sourceType;
	
	public SourceType getSourceTypeEnum(){
		return sourceType;
	}
	public SourceType getSourceType() {
		return sourceType;
	}
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}
	public void setSourceType(int sourceType) {
		this.sourceType = SourceType.valueOf(sourceType);
	}
	
	
	public enum SourceType {
		WEB, 
		MOBILE;

		public int getValue() {
			return ordinal() + 1;
		}

		public static SourceType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private LocationContext location;
	public LocationContext getLocation() {
		return location;
	}
	public void setLocation(LocationContext location) {
		this.location = location;
	}
	
	private String ipAddress;
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	private String terminal;
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	
	private long transactionTime = -1;
	public long getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(long transactionTime) {
		this.transactionTime = transactionTime;
	}

}
