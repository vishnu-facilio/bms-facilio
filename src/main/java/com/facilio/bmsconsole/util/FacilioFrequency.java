package com.facilio.bmsconsole.util;

public enum FacilioFrequency {
	DO_NOT_REPEAT(-1),
	DAILY(62),
	WEEKLY(53*7),
	MONTHLY(366),
	QUARTERTLY(366),
	HALF_YEARLY(366),
	ANNUALLY(366),
	CUSTOM(-1),
	HOURLY(-1)
	;
	public int getValue() {
		return ordinal();
	}
	private int days;

	FacilioFrequency(int days) {
		this.days = days;
	}

	public int getMaxSchedulingDays() {
		return this.days;
	}
	
	public static FacilioFrequency valueOf(int value) {
		if (value >= 0 && value < values().length) {
			return values()[value];
		}
		return null;
	}
}
