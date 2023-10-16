package com.facilio.alarms.sensor.sensorrules;

import java.util.*;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.sensorrules.SensorRuleTypeValidationInterface;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;

public class ValidateContinuouslyReceivingZeroInSensorRule implements SensorRuleTypeValidationInterface {

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
		defaultProps.put("subject", "Same reading is received continuously with an absolute zero value");
		defaultProps.put("comment", "Same Zero-Valued reading is received continuously for a long time.");
		defaultProps.put("severity", FacilioConstants.Alarm.CRITICAL_SEVERITY);
		return null;
	}

	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Object record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) throws Exception {

		ReadingContext reading = (ReadingContext)record;
		FacilioField readingField = sensorRule.getSensorField();

		if(readingField instanceof NumberField && reading != null && reading.getParent() instanceof AssetContext)
		{
			AssetContext asset = (AssetContext)reading.getParent();
			NumberField numberField = (NumberField) readingField;
			if(asset != null && asset.getCategory().getId() == sensorRule.getCategoryId())
			{
				Object currentReadingValue = FacilioUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
				currentReadingValue = (Double) currentReadingValue;
				if(currentReadingValue == null || !currentReadingValue.equals(0.0) || !SensorRuleUtil.isAllowedSensorMetric(numberField)){
					return false;
				}

				Integer noOfHoursToBeFetched = (Integer)fieldConfig.get("timeInterval");
				if(noOfHoursToBeFetched == null) {
					noOfHoursToBeFetched = 6;
				}

				List<Double> readings =  SensorRuleUtil.getLiveOrHistoryReadingsToBeEvaluated(numberField, asset.getId(), reading.getTtime(), noOfHoursToBeFetched.intValue(), isHistorical, historicalReadings, completeHistoricalReadingsMap, getSensorRuleTypeFromValidator());
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

	@Override
	public SensorRuleType getSensorRuleTypeFromValidator() {
		return null;
	}

	@Override
	public boolean evaluateNewSensorRule(SensorRuleContext sensorRule, Object currentValue, Map<Long, Double> readingsMap, JSONObject fieldConfig) {
		throw new RuntimeException("Not implemented !!!");
	}

	@Override
	public Object calculateTimeInterval(Map<String, Object> ruleProp) {
		return null;
	}

}
