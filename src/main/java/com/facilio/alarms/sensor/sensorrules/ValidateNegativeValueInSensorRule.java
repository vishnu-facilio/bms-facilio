package com.facilio.alarms.sensor.sensorrules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.sensorrules.SensorRuleTypeValidationInterface;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.ns.context.AggregationType;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;

public class ValidateNegativeValueInSensorRule implements SensorRuleTypeValidationInterface {

	@Override
	public List<String> getSensorRuleProps() {
		List<String> validatorProps = new ArrayList<String>();
		validatorProps.add("subject");
		validatorProps.add("severity");
		return validatorProps;
	}
	
	@Override
	public JSONObject getDefaultSeverityAndSubject() {
		JSONObject defaultProps = new JSONObject();
		defaultProps.put("subject", "Negative meter readings");
		defaultProps.put("comment", "Counter Field readings seems to have negative readings.");
		defaultProps.put("severity", FacilioConstants.Alarm.WARNING_SEVERITY);
		return defaultProps;
	}

	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Object record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) {
		
		ReadingContext reading = (ReadingContext)record;
		FacilioField readingField = sensorRule.getSensorField();

		if(readingField instanceof NumberField && reading != null && reading.getParentId() != -1)
		{
			NumberField numberField = (NumberField) readingField;
			Object currentReadingValue = FacilioUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
			currentReadingValue = (Double) currentReadingValue;
			if(currentReadingValue == null || !SensorRuleUtil.isAllowedSensorMetric(numberField) || !SensorRuleUtil.isCounterField(numberField)){
				return false;
			}
			return evaluateNewSensorRule(sensorRule,currentReadingValue,null,fieldConfig);
		}
		return false;
	}

	@Override
	public SensorRuleType getSensorRuleTypeFromValidator() {
		return SensorRuleType.NEGATIVE_VALUE;
	}

	@Override
	public boolean evaluateNewSensorRule(SensorRuleContext sensorRule, Object currentValue, Map<Long, Double> readingsMap, JSONObject fieldConfig) {
		return (double)currentValue < 0.0;
	}

	@Override
	public Object calculateTimeInterval(Map<String, Object> ruleProp) {
		return null;
	}

}
