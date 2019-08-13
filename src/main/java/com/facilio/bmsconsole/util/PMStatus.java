package com.facilio.bmsconsole.util;

public enum PMStatus {
	INACTIVE(0, "inactive"),
	ACTIVE(1, "active"),
	SCHEDULING(2, "scheduling"),
	DELETED(3, "deleted");
	public int getValue() {
		return ordinal();
	}
	private int value;
	private String name;

	PMStatus(int value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static PMStatus valueOf(int value) {
		if (value >= 0 && value < values().length) {
			return values()[value];
		}
		return null;
	}
}
