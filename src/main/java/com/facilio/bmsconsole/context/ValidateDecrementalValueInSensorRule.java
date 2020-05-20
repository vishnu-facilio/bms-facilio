package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.SensorRuleUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;

public class ValidateDecrementalValueInSensorRule implements SensorRuleTypeValidationInterface{

	LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap = new LinkedHashMap<String, List<ReadingContext>>();
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
		defaultProps.put("subject", "Latest reading is less than the previous reading");
		defaultProps.put("comment", "Counter Field readings seems to be non-incremental.");
		defaultProps.put("severity", FacilioConstants.Alarm.CRITICAL_SEVERITY);
		return null;
	}
	
	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Map<String,Object> record, JSONObject fieldConfig,  boolean isHistorical, List<ReadingContext> historicalReadings) throws Exception {
		
		ReadingContext reading = (ReadingContext)record;
		FacilioField readingField = sensorRule.getReadingField();

		if(readingField instanceof NumberField && reading != null && reading.getParent() instanceof AssetContext)
		{
			AssetContext asset = (AssetContext)reading.getParent();
			NumberField numberField = (NumberField) readingField;
			if(asset != null && asset.getCategory().getId() == sensorRule.getAssetCategoryId()) 
			{		
				Object currentReadingValue = FacilioUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
				currentReadingValue = (Double) currentReadingValue;
				if(currentReadingValue == null || !SensorRuleUtil.isAllowedSensorMetric(numberField) || !numberField.isCounterField()){
					return false;
				}
				ReadingContext readingToBeEvaluated = new ReadingContext(); 
				
				if(isHistorical) {
					String key = ReadingsAPI.getRDMKey(asset.getId(), numberField);
					List<ReadingContext> completeHistoricalReadings = completeHistoricalReadingsMap.get(key);
							
					if(historicalReadings != null && !historicalReadings.isEmpty() && completeHistoricalReadings == null) {
						completeHistoricalReadings = new ArrayList<ReadingContext>();
						completeHistoricalReadingsMap.put(key, completeHistoricalReadings);
						
						ReadingContext previousReadingContext = SensorRuleUtil.fetchSingleReadingContext(numberField, asset.getId(), reading.getTtime());
						if(previousReadingContext != null && previousReadingContext.getTtime() < reading.getTtime()) {
							completeHistoricalReadings.add(previousReadingContext);
						}
						completeHistoricalReadings.addAll(historicalReadings);
					}
					
					if(completeHistoricalReadings != null && !completeHistoricalReadings.isEmpty()) 
					{
						for(ReadingContext historyReading :completeHistoricalReadings) 
						{
							if(reading.getId() == historyReading.getId() && reading.getTtime() == historyReading.getTtime()) 
							{
								int currentIndex = completeHistoricalReadings.indexOf(historyReading);
								ReadingContext previousIndexReading = completeHistoricalReadings.get(currentIndex-1); //Shouldn't be zero at any case
								if(previousIndexReading.getTtime() < reading.getTtime()) {
									readingToBeEvaluated = previousIndexReading;
								}
							}
						}
					}	
				}
				else {
					readingToBeEvaluated = SensorRuleUtil.fetchSingleReadingContext(numberField, asset.getId(), reading.getTtime());
				}	
				
				List<Double> readings = SensorRuleUtil.getReadings(Collections.singletonList(readingToBeEvaluated),numberField);
				if(readings != null && !readings.isEmpty() && readings.get(0) != null) { 
					double previousReadingValue = readings.get(0);
					if((double)currentReadingValue < previousReadingValue) { 
						return true;
					}
				}				
			}
		}		
		return false;	
	}



	

}
