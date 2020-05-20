package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.SensorRuleUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;

public class ValidateContinuouslyReceivingSameValueInSensorRule implements SensorRuleTypeValidationInterface{

	LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap = new LinkedHashMap<String, List<ReadingContext>>();
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
		return null;
	}

	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Map<String,Object> record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings) throws Exception
	{
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
				if(currentReadingValue == null || !SensorRuleUtil.isAllowedSensorMetric(numberField)){
					return false;
				}
				
				Integer noOfHoursToBeFetched = (Integer)fieldConfig.get("timeInterval");
				if(noOfHoursToBeFetched == null) {
					noOfHoursToBeFetched = 6;
				}
				
				List<Double> readings = SensorRuleUtil.getLiveOrHistoryReadingsToBeEvaluated(numberField, asset.getId(), reading.getTtime(), noOfHoursToBeFetched, isHistorical, historicalReadings, completeHistoricalReadingsMap);						
				if(readings != null && !readings.isEmpty()) 
				{ 	
					LinkedHashSet<Double> readingSet = new LinkedHashSet<Double>();
					readingSet.addAll(readings);
					if(readingSet != null && readingSet.size() == 1)
					{
						for(Double readingSetValue :readingSet) {
							if(readingSetValue != null && readingSetValue.equals((double)currentReadingValue)) {
								return true;
							}	
						}
					}	
				}	
			}
		}
		return false;	
	}

}
