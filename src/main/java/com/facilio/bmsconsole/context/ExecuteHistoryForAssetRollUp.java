package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.collections.MapUtils;

import com.facilio.bmsconsole.commands.RuleRollupCommand.RollupType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.time.DateRange;

public class ExecuteHistoryForAssetRollUp implements ExecuteHistoricalRuleInterface {
	
	private static final Logger LOGGER = Logger.getLogger(ExecuteHistoryForAssetRollUp.class.getName());

	@Override
	public List<BaseEventContext> executeRuleAndGenerateEvents(String messageKey, DateRange dateRange, HashMap<String, Boolean> jobStatesMap, long jobId) throws Exception {
		List<BaseEventContext> baseEvents = new ArrayList<BaseEventContext>();
		
		long processStartTime = System.currentTimeMillis();
		long startTime = dateRange.getStartTime();
		long endTime = dateRange.getEndTime();
		
        String[] keySeparated = messageKey.split("_");
    	String assetRollUp = keySeparated[0].toString();
    	long resourceId = Long.parseLong(keySeparated[1].toString());
    	Integer ruleJobType = Integer.parseInt(keySeparated[2].toString());
		RuleJobType ruleJobTypeEnum = RuleJobType.valueOf(ruleJobType);

		ResourceContext currentResourceContext = ResourceAPI.getResource(resourceId);
		if(currentResourceContext == null || jobStatesMap == null || MapUtils.isEmpty(jobStatesMap) || jobId == -1 || dateRange == null || ruleJobTypeEnum != RuleJobType.ASSET_ROLLUP_ALARM) {
			throw new Exception("Invalid params to execute daily assetRollUp event job: "+jobId);				
		}
		
		if(!(currentResourceContext instanceof AssetContext)) {
			throw new Exception("Invalid asset id to execute daily assetRollUp event job: " +jobId+ " with resourceId: " +resourceId);				
		}
		
		boolean isFirstIntervalJob = Boolean.TRUE.equals((Boolean) jobStatesMap.get("isFirstIntervalJob"));
		boolean isLastIntervalJob = Boolean.TRUE.equals((Boolean) jobStatesMap.get("isLastIntervalJob"));
		Boolean isManualFailed = (Boolean) jobStatesMap.get("isManualFailed");
		
		FacilioChain ruleRollUpChain = TransactionChainFactory.getRuleRollupChain();
		ruleRollUpChain.getContext().put(FacilioConstants.ContextNames.ID, resourceId);
		ruleRollUpChain.getContext().put(FacilioConstants.ContextNames.START_TIME, startTime);
		ruleRollUpChain.getContext().put(FacilioConstants.ContextNames.END_TIME, endTime);
		ruleRollUpChain.getContext().put(FacilioConstants.ContextNames.ROLL_UP_TYPE, RollupType.ASSET);
		ruleRollUpChain.getContext().put(FacilioConstants.ContextNames.IS_HISTORICAL, true);
		ruleRollUpChain.execute();
		
		LOGGER.info("Time taken for AssetRollUp Historical Run for jobId: "+jobId+" Asset : "+resourceId+" between "+startTime+" and "+endTime+" is -- " +(System.currentTimeMillis() - processStartTime));	
		baseEvents = (List<BaseEventContext>) ruleRollUpChain.getContext().get(EventConstants.EventContextNames.EVENT_LIST);
		return baseEvents;
	}
	
	@Override
	public HashMap<Long, List<Long>> getRuleAndResourceIds(long resourceId, boolean isInclude, List<Long> selectedResourceIds) throws Exception {
		HashMap<Long, List<Long>> ruleVsResourceIds = new HashMap<Long, List<Long>>();
		ResourceContext currentResourceContext = ResourceAPI.getResource(resourceId);
		
		if (currentResourceContext == null || !(currentResourceContext instanceof AssetContext)) {
			throw new IllegalArgumentException("Invalid resourceId to run through AssetRollUp historical data.");
		}
		if(selectedResourceIds != null && !selectedResourceIds.isEmpty()) {
			throw new IllegalArgumentException("Secondary param is invalid to run through AssetRollUp historical data.");
		}
		
		ruleVsResourceIds.put(resourceId, Collections.singletonList(resourceId));
		return ruleVsResourceIds;	
	}
}

