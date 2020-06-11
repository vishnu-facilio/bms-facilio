package com.facilio.bmsconsole.context.sensor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;

public class ValidateSameValueWithZeroDeltaInSensorRule implements SensorRuleTypeValidationInterface{

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
		defaultProps.put("subject", "Current reading seems to have a zero consumption");
		defaultProps.put("comment", "Counter Field readings seems to have equal readings.");
		defaultProps.put("severity", FacilioConstants.Alarm.CRITICAL_SEVERITY);
		return null;
	}
	
	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Map<String,Object> record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) throws Exception {
		
		ReadingContext reading = (ReadingContext)record;
		FacilioField readingField = sensorRule.getReadingField();

		if(readingField instanceof NumberField && reading != null && reading.getParent() instanceof AssetContext)
		{
			AssetContext asset = (AssetContext)reading.getParent();
			NumberField numberField = (NumberField) readingField;
			if(asset != null && asset.getCategory().getId() == sensorRule.getAssetCategoryId()) 
			{		
				Object currentReadingValue = FacilioUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
				if(currentReadingValue == null || !SensorRuleUtil.isAllowedSensorMetric(numberField) || !numberField.isCounterField()){
					return false;
				}
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField deltaField = modBean.getField(numberField.getName()+"Delta", numberField.getModule().getName());
				Double currentDeltaValue = (Double)reading.getReading(deltaField.getName());
				if(currentDeltaValue == null || !currentDeltaValue.equals(0.0)) {
					return false;
				}
				
				Integer noOfHoursToBeFetched = (Integer)fieldConfig.get("timeInterval");
				if(noOfHoursToBeFetched == null) {
					noOfHoursToBeFetched = 12;
				}
				List<Double> readings =  SensorRuleUtil.getLiveOrHistoryReadingsToBeEvaluated((NumberField)deltaField, asset.getId(), reading.getTtime(), noOfHoursToBeFetched.intValue(), isHistorical, historicalReadings, completeHistoricalReadingsMap, getSensorRuleTypeFromValidator());
				if(readings != null && !readings.isEmpty()) 
				{ 
					LinkedHashSet<Double> readingSet = new LinkedHashSet<Double>();
					readingSet.addAll(readings);
					if(readingSet != null && readingSet.size() == 1)
					{
						for(Double readingSetValue :readingSet) {
							if(readingSetValue != null && readingSetValue.equals(currentDeltaValue)) {
								return true;
							}	
						}
					}	
				}									
			}
		}
		return false;	
	}

	@Override
	public SensorRuleType getSensorRuleTypeFromValidator() {
		return SensorRuleType.SAME_VALUE_WITH_ZERO_DELTA;
	}
}
