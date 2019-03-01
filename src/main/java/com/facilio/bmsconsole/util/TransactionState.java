package com.facilio.bmsconsole.util;

public enum TransactionState {
	ADDITION,
	ISSUE,
	RETURN;
	
	public int getValue() {
		return ordinal() + 1;
	}
	
	public static TransactionState valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}
