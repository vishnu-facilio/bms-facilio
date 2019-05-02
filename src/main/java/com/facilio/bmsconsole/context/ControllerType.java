package com.facilio.bmsconsole.context;

public enum ControllerType {
	MISC(0, "Misc"),

	NIAGARA(1,"Niagara"),

	BACNET_IP(2,"BACnet Ip"),

	BACNET_MSTP(3,"BACnet MSTP"),

	MODBUS(4,"Modbus"),

	OPC_DA (5,"Opc DA"),

	OPC_UA (6,"Opc UA");

	private String value;
	private int key;

	ControllerType(int key, String value) {
		this.value = value;
		this.key = key;
	}

	public  int getKey(){
		return key;
	}

	public  String getValue() {
		return value;
	}

	public static ControllerType valueOf (int value) {
		if (value > 0 && value <= values().length) {
			return values() [value - 1];
		}
		return null;
	}

}
