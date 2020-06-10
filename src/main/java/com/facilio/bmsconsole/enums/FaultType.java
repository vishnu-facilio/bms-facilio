package com.facilio.bmsconsole.enums;

import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ThresholdType;
import com.facilio.modules.FacilioEnum;

public enum FaultType implements FacilioEnum{
	
	CONTROL_FAULTS,
	EQUIPMENT_FAULTS,
	OPERATIONAL_FAULTS,
	;
	public int getIndex() {
		return ordinal() + 1;
	}

	@Override
	public String getValue() {
		return name();
	}
	
	public static FaultType valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}
