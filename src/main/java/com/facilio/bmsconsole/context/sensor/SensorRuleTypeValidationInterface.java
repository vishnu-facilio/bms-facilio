package com.facilio.bmsconsole.context.sensor;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ReadingContext;


public interface SensorRuleTypeValidationInterface {
	
	public List<String> getSensorRuleProps();
	
	public JSONObject getDefaultSeverityAndSubject();
	
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Map<String,Object> record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings) throws Exception;
	
}
