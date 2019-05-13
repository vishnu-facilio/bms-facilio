package com.facilio.bmsconsole.util;

public enum TransactionType {

	STOCK,
	WORKORDER,
	MANUAL,
	SHIPMENT_STOCK;
	
	public int getValue() {
		return ordinal() + 1;
	}
	
	public static TransactionType valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}
