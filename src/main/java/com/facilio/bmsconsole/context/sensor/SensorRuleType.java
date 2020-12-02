package com.facilio.bmsconsole.context.sensor;

import com.facilio.modules.FacilioEnum;

public enum SensorRuleType implements FacilioEnum{
	
	CONTINUOUSLY_RECEIVING_SAME_VALUE("Same value received continiously for {timeInterval} hours",new ValidateContinuouslyReceivingSameValueInSensorRule(),false, false),
	PERMISSIBLE_LIMIT_VIOLATION("Range is outside of {lowerLimit} and {upperLimit}",new ValidatePermissibleLimitViolationInSensorRule(), false, false, true),
	NEGATIVE_VALUE("Meter reads negative value",new ValidateNegativeValueInSensorRule(), true, true, true),
	DECREMENTAL_VALUE("Meter has decremental readings",new ValidateDecrementalValueInSensorRule(), true, true),
	SAME_VALUE_WITH_ZERO_DELTA("Same meter reading for {timeInterval} occurrences",new ValidateSameValueWithZeroDeltaInSensorRule(), true, true),
	MEAN_VARIATION("Energy consumption is greater than {averageBoundPercentage}% compared to past {timeInterval} days",new ValidateMeanVariationInSensorRule(), true, true),
	;
	
	public int getIndex() {
		return ordinal()+1;
	}
	
	public String getValue() {
        return name();
    }

	public String getValueString() {
		return valueString;
	}

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

	public static SensorRuleType valuOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
	
	private SensorRuleTypeValidationInterface sensorRuleValidationType;
	private String valueString;
	private boolean isCounterFieldType = false;
	private boolean dependsOnCurrentValue = false;
	private boolean isMeterRollUp = false;

	private SensorRuleType(){
	}
	
	private SensorRuleType(String valueString,SensorRuleTypeValidationInterface sensorRuleValidationTypeClass){
		this.sensorRuleValidationType = sensorRuleValidationTypeClass;
		this.valueString = valueString;
	}
	
	private SensorRuleType(String valueString,SensorRuleTypeValidationInterface sensorRuleValidationType, boolean isMeterRollUp, boolean isCounterFieldType) {
		this.sensorRuleValidationType = sensorRuleValidationType;
		this.isMeterRollUp = isMeterRollUp;
		this.isCounterFieldType = isCounterFieldType;
		this.valueString = valueString;
	}
	
	private SensorRuleType(String valueString,SensorRuleTypeValidationInterface sensorRuleValidationType, boolean isMeterRollUp, boolean isCounterFieldType, boolean dependsOnCurrentValue) {
		this.sensorRuleValidationType = sensorRuleValidationType;
		this.isMeterRollUp = isMeterRollUp;
		this.isCounterFieldType = isCounterFieldType;
		this.dependsOnCurrentValue = dependsOnCurrentValue;
		this.valueString = valueString;
	}
	
	public boolean isCounterFieldType() {
		return isCounterFieldType;
	}
	
	public boolean isCurrentValueDependent() {
		return dependsOnCurrentValue;
	}
	
	public boolean isMeterRollUp() {
		return isMeterRollUp;
	}
	
    public SensorRuleTypeValidationInterface getSensorRuleValidationType() {
        return sensorRuleValidationType;
    }
}
