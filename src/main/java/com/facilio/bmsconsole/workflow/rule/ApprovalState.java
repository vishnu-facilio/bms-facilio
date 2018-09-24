package com.facilio.bmsconsole.workflow.rule;

public enum ApprovalState {
	YET_TO_BE_REQUESTED,
	REQUESTED,
	APPROVED,
	REJECTED
	;
	
	public int getValue() {
		return ordinal() + 1;
	}
	
	public static ApprovalState valueOf (int value) {
		if (value > 0 && value <= values().length) {
			return values() [value - 1];
		}
		return null;
	}
}
