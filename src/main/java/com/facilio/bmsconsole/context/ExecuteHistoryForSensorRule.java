package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.sensor.SensorRollUpEventContext;
import com.facilio.bmsconsole.context.sensor.SensorRuleContext;
import com.facilio.bmsconsole.context.sensor.SensorRuleUtil;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalAlarmsAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class ExecuteHistoryForSensorRule extends ExecuteHistoricalRule{
	
	private static final Logger LOGGER = Logger.getLogger(ExecuteHistoryForSensorRule.class.getName());

	public List<String> getExecutionLoggerInfoProps() { //same will be applied as fieldName for occurrence and event-processing criteria
		List<String> defaultLoggerInfoPropList = new ArrayList<String>();
//		defaultLoggerInfoPropList.add("sensorRule");
		defaultLoggerInfoPropList.add("resource"); 
		defaultLoggerInfoPropList.add("readingFieldId"); 
		return defaultLoggerInfoPropList;
	}
	
	@Override
	public List<BaseEventContext> executeRuleAndGenerateEvents(JSONObject loggerInfo, DateRange dateRange, HashMap<String, Boolean> jobStatesMap, long jobId) throws Exception{
		List<BaseEventContext> baseEvents = new ArrayList<BaseEventContext>();
		
		long processStartTime = System.currentTimeMillis();
    	Long assetId = (Long) loggerInfo.get("resource");
    	Integer ruleJobType = (Integer) loggerInfo.get("ruleJobType");
    	RuleJobType ruleJobTypeEnum = RuleJobType.valueOf(ruleJobType);
    	Long assetCategoryId = (Long) loggerInfo.get("assetCategoryId");
    	String readingFieldIds = (String) loggerInfo.get("readingFieldId");

//		AssetContext currentAssetContext = AssetsAPI.getAssetInfo(assetId);
//		Long assetCategoryId = currentAssetContext.getCategory().getId();
   
		if(assetCategoryId == null || jobStatesMap == null || MapUtils.isEmpty(jobStatesMap) || jobId == -1 || dateRange == null || ruleJobTypeEnum != RuleJobType.SENSOR_ROLLUP_ALARM) {
			throw new Exception("Invalid params to execute daily " +ruleJobTypeEnum.getValue()+ " event job: "+jobId);				
		}

		boolean isFirstIntervalJob = Boolean.TRUE.equals((Boolean) jobStatesMap.get("isFirstIntervalJob"));
		boolean isLastIntervalJob = Boolean.TRUE.equals((Boolean) jobStatesMap.get("isLastIntervalJob"));
		Boolean isManualFailed = (Boolean) jobStatesMap.get("isManualFailed");
		
		List<SensorRuleContext> sensorRules = SensorRuleUtil.getSensorRuleByCategoryId(assetCategoryId, readingFieldIds, true);
		List<ReadingContext> readings = new ArrayList<ReadingContext>();
		List<SensorRollUpEventContext> sensorMeterRollUpEvents = new ArrayList<SensorRollUpEventContext>();
		if(sensorRules != null && !sensorRules.isEmpty()) {	
			Set<FacilioField> sensorRuleFields = sensorRules.stream().map(sensorRule -> sensorRule.getReadingField()).collect(Collectors.toSet());
			LinkedHashMap<FacilioModule, List<FacilioField>> sensorRuleModuleVsFieldsMap = SensorRuleUtil.groupSensorRuleFieldsByModule(sensorRuleFields);
			for(FacilioModule module: sensorRuleModuleVsFieldsMap.keySet()) 
			{
				List<FacilioField> sensorRuleModuleFields = sensorRuleModuleVsFieldsMap.get(module);
				List<ReadingContext> fieldReadings = SensorRuleUtil.fetchReadingsForSensorRuleField(module, sensorRuleModuleFields, Collections.singletonList(assetId), dateRange.getStartTime(), dateRange.getEndTime());
				if(fieldReadings != null && !fieldReadings.isEmpty()) {
					readings.addAll(fieldReadings);
				}	
			}
			baseEvents = SensorRuleUtil.executeSensorRules(sensorRules,readings, true, sensorMeterRollUpEvents);
		}	
		LOGGER.info("Time taken for SensorRule HistoricalRun for jobId: " +jobId+ " is: " + (System.currentTimeMillis() - processStartTime));
		return baseEvents;
	}
	
	@Override
	public List<Long> getMatchedSecondaryParamIds(JSONObject loggerInfo, Boolean isInclude) throws Exception 
	{
		String assetCategoryKeyName = fetchPrimaryLoggerKey();
		Long assetCategoryId = (Long)loggerInfo.get(assetCategoryKeyName);
		List<Long> selectedResourceIds = new ArrayList<Long>();
		
		if(assetCategoryId == null) {
			List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(assetCategoryId);
			if(assets != null && !assets.isEmpty()) {
				selectedResourceIds = assets.stream().map(asset -> asset.getId()).collect(Collectors.toList());
			}
		}
		else {
			selectedResourceIds = (List<Long>) loggerInfo.get("resource");
		}
//		SensorRuleContext sensorRule = SensorRuleUtil.getSensorRuleById(ruleId);
//		if (sensorRule == null) {
//			throw new IllegalArgumentException("Invalid sensor rule id to run through historical data.");
//		}
//		List<Long> matchedResourceIds = sensorRule.getMatchedResourcesIds();

//		List<Long> finalResourceIds = WorkflowRuleHistoricalAlarmsAPI.getMatchedFinalSecondaryIds(selectedResourceIds, matchedResourceIds, isInclude);
		return selectedResourceIds;
	}
	
	@Override
	public String fetchPrimaryLoggerKey() {
		return "assetCategoryId";	
	}
	
	@Override
	public String fetchSecondaryLoggerKey() {
		return "resource";	
	}
}
