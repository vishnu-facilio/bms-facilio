package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ExecuteScheduledReadingRuleCommand implements Command {

	private static RuleType[] ruleTypes = {RuleType.READING_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE,RuleType.ALARM_RCA_RULES};
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		Long readingRuleId = (Long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		
		ReadingRuleContext rule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingRuleId,true,true,true);
		
		WorkflowEventContext event = rule.getEvent();
		
		FacilioModule module = event.getModule();
		
		Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), null, placeHolders);
		recordPlaceHolders.put("executionTime", (long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME));
		WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(Collections.singletonList(rule), module, null, null, null, recordPlaceHolders, (FacilioContext)context,true);
		
		return false;
	}

}
