package com.facilio.bmsconsole.context.sensor;

import com.facilio.bmsconsole.context.sensor.SensorRuleTypeValidationInterface;
import com.facilio.bmsconsole.context.sensor.ValidateContinuouslyReceivingSameValueInSensorRule;
import com.facilio.bmsconsole.context.sensor.ValidateContinuouslyReceivingZeroInSensorRule;
import com.facilio.bmsconsole.context.sensor.ValidateDecrementalValueInSensorRule;
import com.facilio.bmsconsole.context.sensor.ValidateMeanVariationInSensorRule;
import com.facilio.bmsconsole.context.sensor.ValidateNegativeValueInSensorRule;
import com.facilio.bmsconsole.context.sensor.ValidatePermissibleLimitViolationInSensorRule;
import com.facilio.bmsconsole.context.sensor.ValidateSameValueWithZeroDeltaInSensorRule;
import com.facilio.modules.FacilioEnum;

public enum SensorRuleType implements FacilioEnum{
	
	CONTINUOUSLY_RECEIVING_SAME_VALUE(new ValidateContinuouslyReceivingSameValueInSensorRule(), false),
	CONTINUOUSLY_RECEIVING_ZERO(new ValidateContinuouslyReceivingZeroInSensorRule(), false),
	PERMISSIBLE_LIMIT_VIOLATION(new ValidatePermissibleLimitViolationInSensorRule(), false, true),
	NEGATIVE_VALUE(new ValidateNegativeValueInSensorRule(), true, true),
	DECREMENTAL_VALUE(new ValidateDecrementalValueInSensorRule(), true),
	SAME_VALUE_WITH_ZERO_DELTA(new ValidateSameValueWithZeroDeltaInSensorRule(),true),
	MEAN_VARIATION(new ValidateMeanVariationInSensorRule(),true),
	;
	
	public int getIndex() {
		return ordinal()+1;
	}
	
	public String getValue() {
        return name();
    }

	public static SensorRuleType valuOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
	
	private SensorRuleTypeValidationInterface sensorRuleValidationType;
	private boolean isCounterFieldType = false;
	private boolean dependsOnCurrentValue = false;

	private SensorRuleType(){
	}
	
	private SensorRuleType(SensorRuleTypeValidationInterface sensorRuleValidationTypeClass){
		this.sensorRuleValidationType = sensorRuleValidationTypeClass;
	}
	
	private SensorRuleType(SensorRuleTypeValidationInterface sensorRuleValidationType, boolean isCounterFieldType) {
		this.sensorRuleValidationType = sensorRuleValidationType;
		this.isCounterFieldType = isCounterFieldType;
	}
	
	private SensorRuleType(SensorRuleTypeValidationInterface sensorRuleValidationType, boolean isCounterFieldType, boolean dependsOnCurrentValue) {
		this.sensorRuleValidationType = sensorRuleValidationType;
		this.isCounterFieldType = isCounterFieldType;
		this.dependsOnCurrentValue = dependsOnCurrentValue;
	}
	
	public boolean isCounterFieldType() {
		return isCounterFieldType;
	}
	
	public boolean isCurrentValueDependent() {
		return dependsOnCurrentValue;
	}
	
    public SensorRuleTypeValidationInterface getSensorRuleValidationType() {
        return sensorRuleValidationType;
    }
}
