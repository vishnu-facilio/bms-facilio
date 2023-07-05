package com.facilio.alarms.sensor.sensorrules;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.sensorrules.SensorRuleTypeValidationInterface;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.ns.context.AggregationType;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;

public class ValidateContinuouslyReceivingSameValueInSensorRule implements SensorRuleTypeValidationInterface {

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
		defaultProps.put("subject", "No change in value ");
		defaultProps.put("comment", "Same reading is received continuously for a long time.");
		defaultProps.put("severity", FacilioConstants.Alarm.WARNING_SEVERITY);
		return defaultProps;
	}

	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Object record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) throws Exception
	{
		ReadingContext reading = (ReadingContext)record;
		FacilioField readingField = sensorRule.getSensorField();

		if(readingField instanceof NumberField && reading != null && reading.getParentId() != -1)
		{
			NumberField numberField = (NumberField) readingField;
			Object currentReadingValue = FacilioUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
			currentReadingValue = (Double) currentReadingValue;
			if(currentReadingValue == null || !SensorRuleUtil.isAllowedSensorMetric(numberField)){
				return false;
			}

			Long noOfHoursToBeFetched = (Long) calculateTimeInterval(fieldConfig);
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

	@Override
	public boolean evaluateNewSensorRule(SensorRuleContext sensorRule, Object currentValue, Map<Long, Double> readingsMap, JSONObject fieldConfig) {
		if(MapUtils.isNotEmpty(readingsMap) && readingsMap.size()>1){
			Set<Double> readingValue = readingsMap.values().stream().collect(Collectors.toSet());
			return readingValue.size() == 1;
		}
		return false;
	}

	@Override
	public Object calculateTimeInterval(Map<String, Object> ruleProp) {
		Long noOfHoursToBeFetched = Long.valueOf(String.valueOf(ruleProp.get("timeInterval")));
		return noOfHoursToBeFetched;
	}

}
