package com.facilio.bmsconsole.context.sensor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;

public class ValidateMeanVariationInSensorRule implements SensorRuleTypeValidationInterface {

	@Override
	public List<String> getSensorRuleProps() {
		List<String> validatorProps = new ArrayList<String>();
		validatorProps.add("averageBoundPercentage");
		validatorProps.add("timeInteval");
		validatorProps.add("subject");
		validatorProps.add("severity");
		return validatorProps;
	}

	@Override
	public JSONObject getDefaultSeverityAndSubject() {
		JSONObject defaultProps = new JSONObject();
		defaultProps.put("subject", "Current reading's consumption deviates from the average consumption by 100 times");
		defaultProps.put("comment", "Counter Field readings seems to high delta difference.");
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
				if(currentDeltaValue == null) {
					return false;
				}

				Integer noOfHoursToBeFetched = (Integer)fieldConfig.get("timeInterval");
				if(noOfHoursToBeFetched == null) {
					noOfHoursToBeFetched = 7*24;
				}
				Integer averageBoundPercentage = (Integer)fieldConfig.get("averageBoundPercentage");
				if(averageBoundPercentage == null) {
					averageBoundPercentage = 100;
				}
				List<Double> readings =  SensorRuleUtil.getLiveOrHistoryReadingsToBeEvaluated((NumberField)deltaField, asset.getId(), reading.getTtime(), noOfHoursToBeFetched.intValue(), isHistorical, historicalReadings, completeHistoricalReadingsMap, getSensorRuleTypeFromValidator());
				if(readings != null && !readings.isEmpty()) 
				{ 	
					Double averageValue = getAverage(readings);
					if(averageValue != null && averageValue > 0) 
					{
						Double averageLowerLimit = averageValue - (averageValue * averageBoundPercentage/100);
						Double averageHigherLimit = averageValue + (averageValue * averageBoundPercentage/100);

						if(currentDeltaValue < averageLowerLimit || currentDeltaValue > averageHigherLimit) {	
							return true;
						}
					}
				}	
			}
		}
		
		return false;
	}
	
	private Double getAverage(List<Double> readings) {
		
		if(readings != null && !readings.isEmpty()) 
		{ 			
			double sum=0, avg=0, n=0;
			for(Double reading: readings)
			{	
				sum+=(double)reading;
				n++;				
			}
			if(n>2)
			{
				avg = sum/n;
				return avg;
			}
		}
		return null;
	}

	@Override
	public SensorRuleType getSensorRuleTypeFromValidator() {
		return SensorRuleType.MEAN_VARIATION;
	}
}
