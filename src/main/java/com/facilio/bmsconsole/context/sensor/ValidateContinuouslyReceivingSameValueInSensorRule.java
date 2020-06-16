package com.facilio.bmsconsole.context.sensor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.sensor.SensorRuleTypeValidationInterface;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;

public class ValidateContinuouslyReceivingSameValueInSensorRule implements SensorRuleTypeValidationInterface{

	@Override
	public List<String> getSensorRuleProps() {
		List<String> validatorProps = new ArrayList<String>();
		validatorProps.add("timeInterval");
		validatorProps.add("subject");
		validatorProps.add("severity");
		return validatorProps;
	}
	
	@Override
	public JSONObject getDefaultSeverityAndSubject() {
		JSONObject defaultProps = new JSONObject();
		defaultProps.put("subject", "Same reading is received continuously");
		defaultProps.put("comment", "Same reading is received continuously for a long time.");
		defaultProps.put("severity", FacilioConstants.Alarm.CRITICAL_SEVERITY);
		return defaultProps;
	}

	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Object record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) throws Exception
	{
		ReadingContext reading = (ReadingContext)record;
		FacilioField readingField = sensorRule.getReadingField();

		if(readingField instanceof NumberField && reading != null && reading.getParentId() != -1)
		{
			NumberField numberField = (NumberField) readingField;
			Object currentReadingValue = FacilioUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
			currentReadingValue = (Double) currentReadingValue;
			if(currentReadingValue == null || !SensorRuleUtil.isAllowedSensorMetric(numberField)){
				return false;
			}
			
			Long noOfHoursToBeFetched = (Long)fieldConfig.get("timeInterval");
			if(noOfHoursToBeFetched == null) {
				noOfHoursToBeFetched = 6l;
			}
			
			List<Double> readings = SensorRuleUtil.getLiveOrHistoryReadingsToBeEvaluated(numberField, reading.getParentId(), reading.getTtime(), noOfHoursToBeFetched.intValue(), isHistorical, historicalReadings, completeHistoricalReadingsMap, getSensorRuleTypeFromValidator());						
			if(readings != null && !readings.isEmpty()) 
			{ 	
				LinkedHashSet<Double> readingSet = new LinkedHashSet<Double>();
				readingSet.addAll(readings);
				if(readingSet != null && readingSet.size() == 1 && readings.size() > 1)
				{
					for(Double readingSetValue :readingSet) {
						if(readingSetValue != null && readingSetValue.equals((double)currentReadingValue)) {
							return true;
						}	
					}
				}	
			}	
		}
		return false;	
	}

	@Override
	public SensorRuleType getSensorRuleTypeFromValidator() {
		return SensorRuleType.CONTINUOUSLY_RECEIVING_SAME_VALUE;
	}

}
