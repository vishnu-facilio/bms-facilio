package com.facilio.bmsconsole.context.sensor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ReadingContext;


public interface SensorRuleTypeValidationInterface {
	
	public List<String> getSensorRuleProps();
	
	public JSONObject getDefaultSeverityAndSubject();
	
	public boolean evaluateSensorRule(SensorRuleContext sensorRule, Object record, JSONObject fieldConfig, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap) throws Exception;
	
	public SensorRuleType getSensorRuleTypeFromValidator();
}
