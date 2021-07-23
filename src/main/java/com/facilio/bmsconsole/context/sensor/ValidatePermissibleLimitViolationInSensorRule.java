package com.facilio.bmsconsole.context.sensor;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ValidatePermissibleLimitViolationInSensorRule implements SensorRuleTypeValidationInterface{
	private static Logger LOGGER = Logger.getLogger(ValidatePermissibleLimitViolationInSensorRule.class.getName());
	@Override
	public List<String> getSensorRuleProps() {
		List<String> validatorProps = new ArrayList<String>();
		validatorProps.add("lowerLimit");
		validatorProps.add("upperLimit");
		validatorProps.add("subject");
		validatorProps.add("severity");
		return validatorProps;
	}
	
	@Override
	public JSONObject getDefaultSeverityAndSubject() {
		JSONObject defaultProps = new JSONObject();
		defaultProps.put("subject", "Out of range");
		defaultProps.put("comment", "Current Reading doesn't lie between the limits of the reading field.");
		defaultProps.put("severity", FacilioConstants.Alarm.WARNING_SEVERITY);
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
			if(currentReadingValue == null || !SensorRuleUtil.isAllowedSensorMetric(numberField) || fieldConfig == null){
				return false;
			}

			Object lowerLim = fieldConfig.get("lowerLimit");
			Object upperLim = fieldConfig.get("upperLimit");
			if (lowerLim == null || upperLim == null) {
				//TODO: Need to track invalid input values to separate db entry.
				LOGGER.error("Upper limit and lower limit cannot be empty. Upperlimit : " + upperLim + ", Lowerlimit : " + lowerLim);
				return false;
			}

			Double lowerLimit = Double.valueOf(String.valueOf(lowerLim));
			Double upperLimit = Double.valueOf(String.valueOf(lowerLim));

			if((double)currentReadingValue < lowerLimit || (double)currentReadingValue > upperLimit) { 
				return true;
			}			
		}
		
		return false;
	}

	@Override
	public SensorRuleType getSensorRuleTypeFromValidator() {
		return SensorRuleType.PERMISSIBLE_LIMIT_VIOLATION;
	}
}
