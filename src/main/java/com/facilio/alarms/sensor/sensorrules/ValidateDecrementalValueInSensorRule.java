package com.facilio.alarms.sensor.sensorrules;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;

public class ValidateDecrementalValueInSensorRule implements SensorRuleTypeValidationInterface {

	@Override
	public List<String> getSensorRuleProps() {
		List<String> validatorProps = new ArrayList<>();
		validatorProps.add("subject");
		validatorProps.add("severity");
		return validatorProps;
	}
	
	@Override
	public JSONObject getDefaultSeverityAndSubject() {
		JSONObject defaultProps = new JSONObject();
		defaultProps.put("subject", "Decremental meter readings");
		defaultProps.put("comment", "Counter Field readings seems to be non-incremental.");
		defaultProps.put("severity", FacilioConstants.Alarm.WARNING_SEVERITY);
		return defaultProps;
	}
	
	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Object record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) throws Exception {
		
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
			ReadingContext readingToBeEvaluated = new ReadingContext(); 
			
			if(isHistorical) {
				String key = ReadingsAPI.getRDMKey(reading.getParentId(), numberField) +"_"+ getSensorRuleTypeFromValidator().getIndex();
				List<ReadingContext> completeHistoricalReadings = completeHistoricalReadingsMap.get(key);
						
				if(historicalReadings != null && !historicalReadings.isEmpty() && completeHistoricalReadings == null) {
					completeHistoricalReadings = new ArrayList<ReadingContext>();
					completeHistoricalReadingsMap.put(key, completeHistoricalReadings);
					
					ReadingContext previousReadingContext = SensorRuleUtil.fetchSingleReadingContext(numberField, reading.getParentId(), reading.getTtime());
					if(previousReadingContext != null && previousReadingContext.getTtime() < reading.getTtime()) {
						completeHistoricalReadings.add(previousReadingContext);
					}
					completeHistoricalReadings.addAll(historicalReadings);
				}
				
				if(completeHistoricalReadings != null && !completeHistoricalReadings.isEmpty()) 
				{
					completeHistoricalReadings.sort(new timeSorter());
					for(ReadingContext historyReading :completeHistoricalReadings) 
					{
						if(reading.getId() == historyReading.getId() && reading.getTtime() == historyReading.getTtime()) 
						{
							int currentIndex = completeHistoricalReadings.indexOf(historyReading);
							if(currentIndex > 0) {
								ReadingContext previousIndexReading = completeHistoricalReadings.get(currentIndex-1); //Shouldn't be zero in real
								if(previousIndexReading.getTtime() < reading.getTtime()) {
									readingToBeEvaluated = previousIndexReading;
								}		
							}
						}
					}
				}	
			}
			else {
				readingToBeEvaluated = SensorRuleUtil.fetchSingleReadingContext(numberField, reading.getParentId(), reading.getTtime());
			}	
			
			List<Double> readings = SensorRuleUtil.getReadings(Collections.singletonList(readingToBeEvaluated),numberField);
			if(readings != null && !readings.isEmpty() && readings.get(0) != null) { 
				double previousReadingValue = readings.get(0);
				if((double)currentReadingValue < previousReadingValue) { 
					return true;
				}
			}				
		}		
		return false;	
	}

	@Override
	public SensorRuleType getSensorRuleTypeFromValidator() {
		return SensorRuleType.DECREMENTAL_VALUE;
	}

	@Override
	public boolean evaluateNewSensorRule(SensorRuleContext sensorRule, Object currentValue, Map<Long, Double> readingsMap, JSONObject fieldConfig) {
		if (MapUtils.isNotEmpty(readingsMap) && readingsMap.size() > 1) {
			List<Double> readingValue = readingsMap.values().stream().filter(m -> m != currentValue).collect(Collectors.toList());
			double lastValue = CollectionUtils.isNotEmpty(readingValue) ? readingValue.get(0) : -1;
			if (lastValue > -1) {
				return lastValue > (double) currentValue;
			}
		}
		return false;
	}


	@Override
	public Object calculateTimeInterval(Map<String, Object> ruleProp) {
		return null;
	}
}

class timeSorter implements Comparator<ReadingContext> 
{
	@Override
	public int compare(ReadingContext o1, ReadingContext o2) {
		return (int) (o1.getTtime() - o2.getTtime());
	}
}
