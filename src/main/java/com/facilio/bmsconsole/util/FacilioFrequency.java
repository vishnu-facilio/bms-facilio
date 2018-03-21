package com.facilio.bmsconsole.util;

public enum FacilioFrequency {
	DAILY,
	WEEKLY,
	MONTHLY,
	QUARTERTLY,
	HALF_YEARLY,
	ANNUALLY,
	CUSTOM
	;
	public int getValue() {
		return ordinal()+1;
	}
	
	public static FacilioFrequency valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}
