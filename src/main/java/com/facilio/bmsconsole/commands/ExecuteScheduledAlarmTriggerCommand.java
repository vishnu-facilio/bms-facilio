package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.Map;

public class ExecuteScheduledAlarmTriggerCommand implements Command {

	private static RuleType[] ruleTypes = {RuleType.READING_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE,RuleType.ALARM_RCA_RULES,RuleType.PM_READING_TRIGGER};
	@Override
	public boolean execute(Context context) throws Exception {
		
		Long readingRuleId = (Long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		
		ReadingRuleContext rule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingRuleId,true,true,true);
		
		if (rule == null || !rule.isActive()) {
			return false;
		}
		
		WorkflowEventContext event = rule.getEvent();
		
		FacilioModule module = event.getModule();
		
		Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), null, placeHolders);
		
		WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(Collections.singletonList(rule), module, null, null, null, recordPlaceHolders, (FacilioContext)context,true, Collections.singletonList(rule.getEvent().getActivityTypeEnum()));
		return false;
	}

}
