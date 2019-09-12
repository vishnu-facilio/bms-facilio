package com.facilio.bmsconsole.context;

public class TaskErrorContext {

	Mode mode;
	FailType failType;
	
	String previousValue;
	String currentValue;

	public String getPreviousValue() {
		return previousValue;
	}

	public void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public int getFailType() {
		if(failType != null) {
			return failType.getValue();
		}
		return -1;
	}

	public void setFailType(int failType) {
		this.failType = FailType.valueOf(failType);
	}

	String message;
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMode() {
		if(mode != null) {
			return mode.getValue();
		}
		return -1;
	}

	public void setMode(int mode) {
		this.mode = Mode.valueOf(mode);
	}
	
	public enum FailType {
		
		NON_INCREMENTAL_VALUE,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		public static FailType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}

	public enum Mode {
		
		ERROR,
		SUGGESTION,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		public static Mode valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
