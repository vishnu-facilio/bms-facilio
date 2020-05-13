package com.facilio.bmsconsole.context;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;


public interface SensorRuleTypeValidationInterface {
	
	public List<String> getSensorRuleProps();
	
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Map<String,Object> record, JSONObject fieldConfig) throws Exception;
	
	public JSONObject addDefaultSeverityAndSubject();

}
