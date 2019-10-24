package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ControllerType {
	MISC(0, "Misc", true),

	BACNET_IP(1,"BACnet Ip", true),

	BACNET_MSTP(2,"BACnet MSTP", true),

	NIAGARA(3, "Niagara", true),

	MODBUS_IP(4,"Modbus_Ip"),

	MODBUS_RTU(5,"Modbus_Rtu"),

	OPC_DA (6,"Opc DA"),

	OPC_UA (7,"Opc UA"),
	
	LON_WORKS (8,"Lon Works", true),
	
	KNX (9, "Knx", true),
	
	CUSTOM(10, "Custom"),
	
	REST(11, "Rest")
	;

	private int key;
	private String label;
	private boolean configurable;

	ControllerType(int key, String label) {
		this(key, label, false);
	}
	ControllerType(int key, String label, boolean configurable) {
		this.key = key;
		this.label = label;
		this.configurable = configurable;
	}

	public  int getKey(){
		return key;
	}

	public  String getLabel() {
		return label;
	}
	
	public  boolean isConfigurable() {
		return configurable;
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
