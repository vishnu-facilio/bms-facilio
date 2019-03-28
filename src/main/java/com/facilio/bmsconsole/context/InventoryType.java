package com.facilio.bmsconsole.context;

public enum InventoryType {
	ITEM,
	TOOL;
	
	public int getValue() {
		return ordinal()+1;
	}

	public static InventoryType valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}
