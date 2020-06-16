package com.facilio.bmsconsole.context.sensor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;

public class ValidateNegativeValueInSensorRule implements SensorRuleTypeValidationInterface{

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
		defaultProps.put("subject", "Current reading seems to be a non-positive reading");
		defaultProps.put("comment", "Counter Field readings seems to have negative readings.");
		defaultProps.put("severity", FacilioConstants.Alarm.CRITICAL_SEVERITY);
		return defaultProps;
	}

	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Object record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) {
		
		ReadingContext reading = (ReadingContext)record;
		FacilioField readingField = sensorRule.getReadingField();

		if(readingField instanceof NumberField && reading != null && reading.getParentId() != -1)
		{
			NumberField numberField = (NumberField) readingField;
			Object currentReadingValue = FacilioUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
			currentReadingValue = (Double) currentReadingValue;
			if(currentReadingValue == null || !SensorRuleUtil.isAllowedSensorMetric(numberField) || !numberField.isCounterField()){
				return false;
			}
			if((double)currentReadingValue < 0.0){ 
				return true;
			}	
			
		}
		return false;
	}

	@Override
	public SensorRuleType getSensorRuleTypeFromValidator() {
		return SensorRuleType.NEGATIVE_VALUE;
	}

}
