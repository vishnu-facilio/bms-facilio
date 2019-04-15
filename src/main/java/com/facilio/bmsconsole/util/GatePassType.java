package com.facilio.bmsconsole.util;

public enum GatePassType {
	INWARD,
	OUTWARD;
	
	public int getValue() {
		return ordinal() + 1;
	}
	
	public static GatePassType valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}
