package com.facilio.bmsconsole.util;

public enum FacilioFrequency {
	DO_NOT_REPEAT(-1, "Do Not Repeat"),
	DAILY(21, "Daily"),
	WEEKLY(8 * 2, "Weekly"),
	MONTHLY(366, "Monthly"),
	QUARTERTLY(366, "Quarterly"),
	HALF_YEARLY(366, "Half Yearly"),
	ANNUALLY(366, "Annually"),
	CUSTOM(-1, "Custom"),
	HOURLY(-1, "Hourly"),
	FIFTEEN_MINUTES(-1, "Quarter Hour"),
	TEN_MINUTES(-1, "One-Sixth Hour")
	;
	public int getValue() {
		return ordinal();
	}
	private int days;
	private String name;

	FacilioFrequency(int days, String name) {
		this.days = days;
		this.name = name;
	}

	public int getMaxSchedulingDays() {
		return this.days;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static FacilioFrequency valueOf(int value) {
		if (value >= 0 && value < values().length) {
			return values()[value];
		}
		return null;
	}
}
