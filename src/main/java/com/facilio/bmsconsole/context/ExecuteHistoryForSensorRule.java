package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.collections.MapUtils;

import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.time.DateRange;

public class ExecuteHistoryForSensorRule implements ExecuteHistoricalRuleInterface{
	
	private static final Logger LOGGER = Logger.getLogger(ExecuteHistoryForSensorRule.class.getName());

	@Override
	public List<BaseEventContext> executeRuleAndGenerateEvents(String messageKey, DateRange dateRange, HashMap<String, Boolean> jobStatesMap, long jobId) throws Exception {
		List<BaseEventContext> baseEvents = new ArrayList<BaseEventContext>();
		
		long processStartTime = System.currentTimeMillis();
		long startTime = dateRange.getStartTime();
		long endTime = dateRange.getEndTime();
		
        String[] keySeparated = messageKey.split("_");
    	long ruleId = Long.parseLong(keySeparated[0].toString());
    	long resourceId = Long.parseLong(keySeparated[1].toString());
    	Integer ruleJobType = Integer.parseInt(keySeparated[2].toString());
		RuleJobType ruleJobTypeEnum = RuleJobType.valueOf(ruleJobType);

//		SensorRuleContext sensorRule = SensorRuleUtil
		ResourceContext currentResourceContext = ResourceAPI.getResource(resourceId);
		
		if(currentResourceContext == null || jobStatesMap == null || MapUtils.isEmpty(jobStatesMap) || jobId == -1 || dateRange == null || ruleJobTypeEnum != RuleJobType.SENSOR_ALARM) {
			throw new Exception("Invalid params to execute daily " +ruleJobTypeEnum.getValue()+ " event job: "+jobId);				
		}
//		sensorRule.setMatchedResourceIds(Collections.singletonList(currentResourceContext.getId()));

		boolean isFirstIntervalJob = Boolean.TRUE.equals((Boolean) jobStatesMap.get("isFirstIntervalJob"));
		boolean isLastIntervalJob = Boolean.TRUE.equals((Boolean) jobStatesMap.get("isLastIntervalJob"));
		Boolean isManualFailed = (Boolean) jobStatesMap.get("isManualFailed");
		
		return baseEvents;
	}
	
	@Override
	public HashMap<Long, List<Long>> getRuleAndResourceIds(long ruleId, boolean isInclude, List<Long> selectedResourceIds) throws Exception 
	{
		HashMap<Long, List<Long>> ruleVsResourceIds = new HashMap<Long, List<Long>>();
//		SensorRuleContext sensorRule = SensorRuleUtil.getSensorRuleById(ruleId);
//		if (sensorRule == null) {
//			throw new IllegalArgumentException("Invalid sensor rule id to run through historical data.");
//		}
//		
//		List<Long> finalResourceIds = new ArrayList<Long>();
//		if(selectedResourceIds == null || selectedResourceIds.isEmpty())
//		{
//			finalResourceIds = sensorRule.getMatchedResourcesIds();
//		}
//		else if (selectedResourceIds!=null && !selectedResourceIds.isEmpty() && isInclude)
//		{
//			List<Long> matchedResources = sensorRule.getMatchedResourcesIds();
//			for(Long resourceId: selectedResourceIds)
//			{
//				if(matchedResources.contains(resourceId)) {
//					finalResourceIds.add(resourceId);
//				}
//			}
//		}
//		else if (selectedResourceIds!=null && !selectedResourceIds.isEmpty() && !isInclude)
//		{
//			List<Long> matchedResources = sensorRule.getMatchedResourcesIds();
//			for(Long matchedResourceId: matchedResources)
//			{
//				if(!selectedResourceIds.contains(matchedResourceId)) {
//					finalResourceIds.add(matchedResourceId);
//				}
//			}
//		}
//		if(finalResourceIds == null || finalResourceIds.isEmpty()) {
//			throw new IllegalArgumentException("Not a valid Inclusion/Exclusion of Resources for the given sensor rule.");
//		}
//		
//		ruleVsResourceIds.put(ruleId, finalResourceIds);
		return ruleVsResourceIds;
	}

}
