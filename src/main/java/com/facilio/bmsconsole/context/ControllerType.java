package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ControllerType {
	MISC(0, "Misc"),

	BACNET_IP(1,"BACnet Ip"),

	BACNET_MSTP(2,"BACnet MSTP"),

	NIAGARA(3, "Niagara"),

	MODBUS_IP(4,"Modbus_Ip"),

	MODBUS_RTU(5,"Modbus_Rtu"),

	OPC_DA (6,"Opc DA"),

	OPC_UA (7,"Opc UA"),
	
	LON_WORKS (8,"Lon Works"),
	
	KNX (9, "Knx")
	;

	private int key;
	private String label;

	ControllerType(int key, String label) {
		this.key = key;
		this.label = label;
	}

	public  int getKey(){
		return key;
	}

	public  String getLabel() {
		return label;
	}
	
	public static ControllerType valueOf(int value) {
		return TYPE_MAP.get(value);
	}
	
	private static final Map<Integer, ControllerType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, ControllerType> initTypeMap() {
		Map<Integer, ControllerType> typeMap = new HashMap<>();
		for(ControllerType type : values()) {
			typeMap.put(type.getKey(), type);
		}
		return typeMap;
	}

}
