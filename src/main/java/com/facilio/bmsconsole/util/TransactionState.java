package com.facilio.bmsconsole.util;

import lombok.Getter;
import lombok.Setter;

public enum TransactionState{
	ADDITION("Addition"),
	ISSUE("Issue"),
	RETURN("Return"),
	USE("Use"),
	IN("In"),
	OUT("Out"),
	ADJUSTMENT_INCREASE("Adjustment Increase"),
	ADJUSTMENT_DECREASE("Adjustment Decrease"),
	TRANSFERRED_FROM("Transferred From"),
	TRANSFERRED_TO("Transferred To"),
	HARD_RESERVE("Hard Reserve"),
	SOFT_RESERVE("Soft Reserve");
	@Getter@Setter
	private String label;
	TransactionState(String label){
		this.label = label;
	}
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
