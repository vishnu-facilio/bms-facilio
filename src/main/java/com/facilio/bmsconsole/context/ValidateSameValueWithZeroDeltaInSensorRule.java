package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SensorRuleUtil;
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
	public JSONObject addDefaultSeverityAndSubject() {
		JSONObject defaultProps = new JSONObject();
		defaultProps.put("subject", "Current reading seems to have a zero consumption");
		defaultProps.put("comment", "Counter Field readings seems to have equal readings.");
		defaultProps.put("severity", FacilioConstants.Alarm.CRITICAL_SEVERITY);
		return null;
	}
	
	@Override
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Map<String,Object> record, JSONObject fieldConfig) throws Exception {
		
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
				Object currentDeltaValue = (Long)reading.getReading(deltaField.getName());
				if(currentDeltaValue == null || !currentDeltaValue.equals(0l)) {
					return false;
				}
				
				Integer noOfHoursToBeFetched = (Integer)fieldConfig.get("timeInterval");
				if(noOfHoursToBeFetched == null) {
					noOfHoursToBeFetched = 12;
				}
				
				List<Long> readings = SensorRuleUtil.getReadingsBtwDayTimeInterval((NumberField)deltaField, asset.getId(), reading.getTtime(), noOfHoursToBeFetched);
				if(readings != null && !readings.isEmpty()) 
				{ 
					LinkedHashSet<Long> readingSet = new LinkedHashSet<Long>();
					readingSet.addAll(readings);
					if(readingSet != null && readingSet.size() == 1)
					{
						for(Long readingSetValue :readingSet) {
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

}
