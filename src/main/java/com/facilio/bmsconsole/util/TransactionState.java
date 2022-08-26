package com.facilio.bmsconsole.util;

public enum TransactionState{
	ADDITION,
	ISSUE,
	RETURN,
	USE,
	IN,
	OUT,
	ADJUSTMENT_INCREASE,
	ADJUSTMENT_DECREASE,
	TRANSFERRED_FROM,
	TRANSFERRED_TO,
	HARD_RESERVE,
	SOFT_RESERVE;


	public static TransactionState valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}

	public int getValue() {
		return ordinal() + 1;
	}
}
