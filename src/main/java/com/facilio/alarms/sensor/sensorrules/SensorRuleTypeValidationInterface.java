package com.facilio.alarms.sensor.sensorrules;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ReadingContext;


public interface SensorRuleTypeValidationInterface {
	
	List<String> getSensorRuleProps();
	
	JSONObject getDefaultSeverityAndSubject();
	
	boolean evaluateSensorRule(SensorRuleContext sensorRule, Object record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) throws Exception;
	
	 SensorRuleType getSensorRuleTypeFromValidator();

	 boolean evaluateNewSensorRule(SensorRuleContext sensorRule, Object currentValue, Map<Long, Double> readingsMap,JSONObject fieldConfig);

	 Object calculateTimeInterval(Map<String,Object> ruleProp);
}
