package com.facilio.bmsconsole.context;

public enum UserWorkHourReading {
	START,
	PAUSE,
	RESUME,
	CLOSE
	;
	
	public int getValue() {
		return ordinal() + 1;
	}
	
	public static UserWorkHourReading valueOf (int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}
