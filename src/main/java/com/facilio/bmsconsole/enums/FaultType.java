package com.facilio.bmsconsole.enums;

import com.facilio.modules.FacilioIntEnum;

public enum FaultType implements FacilioIntEnum {
	
	CONTROL_FAULT("Control Fault"),
	EQUIPMENT_FAULT("Equipment Fault"),
	OPERATIONAL_FAULT("Operational Fault"),
	SENSOR_FAULT("Sensor Fault"),
	MECHANICAL_FAULT("Mechanical Fault"),
	;
	
	private String name;

	FaultType() {
    }
	
	FaultType(String name) {
        this.name = name;
    }

	@Override
	public String getValue() {
		return name;
	}
	
    @Override
	public Integer getIndex() {
		return ordinal() + 1;
	}
	
	public static FaultType valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}
