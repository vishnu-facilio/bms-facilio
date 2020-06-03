package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.RuleRollupCommand.RollupType;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.time.DateRange;

public class ExecuteHistoryForRuleRollUp extends ExecuteHistoricalRule {

	private static final Logger LOGGER = Logger.getLogger(ExecuteHistoryForRuleRollUp.class.getName());

	public List<String> getExecutionLoggerInfoProps() { //same will be applied as fieldName for occurrence and event-processing criteria
		List<String> defaultLoggerInfoPropList = new ArrayList<String>();
		defaultLoggerInfoPropList.add("rule");
		return defaultLoggerInfoPropList;
	}
	
	@Override
	public List<BaseEventContext> executeRuleAndGenerateEvents(JSONObject loggerInfo, DateRange dateRange, HashMap<String, Boolean> jobStatesMap, long jobId) throws Exception{
		List<BaseEventContext> baseEvents = new ArrayList<BaseEventContext>();
		
		long processStartTime = System.currentTimeMillis();
		long startTime = dateRange.getStartTime();
		long endTime = dateRange.getEndTime();
		
		Long ruleId = (Long) loggerInfo.get("rule");
    	Integer ruleJobType = (Integer) loggerInfo.get("ruleJobType");
    	RuleJobType ruleJobTypeEnum = RuleJobType.valueOf(ruleJobType);

		ReadingRuleContext readingRule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(ruleId, false, true);
		if(readingRule == null || jobStatesMap == null || MapUtils.isEmpty(jobStatesMap) || jobId == -1 || dateRange == null || ruleJobTypeEnum != RuleJobType.RULE_ROLLUP_ALARM) {
			throw new Exception("Invalid params to execute daily ruleRollUp event job: "+jobId);				
		}

		boolean isFirstIntervalJob = Boolean.TRUE.equals((Boolean) jobStatesMap.get("isFirstIntervalJob"));
		boolean isLastIntervalJob = Boolean.TRUE.equals((Boolean) jobStatesMap.get("isLastIntervalJob"));
		Boolean isManualFailed = (Boolean) jobStatesMap.get("isManualFailed");
		
		FacilioChain ruleRollUpChain = TransactionChainFactory.getRuleRollupChain();
		ruleRollUpChain.getContext().put(FacilioConstants.ContextNames.ID, ruleId);
		ruleRollUpChain.getContext().put(FacilioConstants.ContextNames.START_TIME, startTime);
		ruleRollUpChain.getContext().put(FacilioConstants.ContextNames.END_TIME, endTime);
		ruleRollUpChain.getContext().put(FacilioConstants.ContextNames.ROLL_UP_TYPE, RollupType.RULE);
		ruleRollUpChain.getContext().put(FacilioConstants.ContextNames.IS_HISTORICAL, true);
		ruleRollUpChain.execute();
		
		LOGGER.info("Time taken for RuleRollUp Historical Run for jobId: "+jobId+" Reading Rule : "+ruleId+" between "+startTime+" and "+endTime+" is -- " +(System.currentTimeMillis() - processStartTime));
		baseEvents = (List<BaseEventContext>) ruleRollUpChain.getContext().get(EventConstants.EventContextNames.EVENT_LIST);
		return baseEvents;
	}
}
