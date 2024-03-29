package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;

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
		CHECKIN, 
		CHECKOUT,
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

	private SourceType sourceType;
	
	public SourceType getSourceTypeEnum(){
		return sourceType;
	}
	
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}
	
	public int getSourceType() {
		if (sourceType != null) {
			return sourceType.getValue();
		}
		return -1;
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
	
	private User user;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	private String remarks;
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
