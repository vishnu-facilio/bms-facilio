package com.facilio.bmsconsole.util;

public enum FacilioFrequency {
	DO_NOT_REPEAT,
	DAILY,
	WEEKLY,
	MONTHLY,
	QUARTERTLY,
	HALF_YEARLY,
	ANNUALLY,
	CUSTOM,
	HOURLY
	;
	public int getValue() {
		return ordinal();
	}
	
	public static FacilioFrequency valueOf(int value) {
		if (value >= 0 && value < values().length) {
			return values()[value];
		}
		return null;
	}
}
