package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.SensorRuleUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;

public class ValidatePermissibleLimitViolationInSensorRule implements SensorRuleTypeValidationInterface{

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
		defaultProps.put("subject", "Current reading is not within its permissible limits.");
		defaultProps.put("comment", "Current Reading doesn't lie between the limits of the reading field.");
		defaultProps.put("severity", FacilioConstants.Alarm.CRITICAL_SEVERITY);
		return null;
	}

	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Map<String,Object> record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings) {
		
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
				
				Double lowerLimit = (Double)fieldConfig.get("lowerLimit");
				Double upperLimit = (Double)fieldConfig.get("upperLimit");
				if(lowerLimit == null || upperLimit == null) {
					return false;
				}
				if((double)currentReadingValue < lowerLimit || (double)currentReadingValue > upperLimit) { 
					return true;
				}	
			}
		}
		
		return false;
	}

}
