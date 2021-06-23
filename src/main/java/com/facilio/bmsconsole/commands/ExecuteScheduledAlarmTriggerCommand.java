package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class ExecuteScheduledAlarmTriggerCommand extends FacilioCommand {

	private static RuleType[] ruleTypes = {RuleType.READING_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE,RuleType.ALARM_RCA_RULES,RuleType.PM_READING_TRIGGER};
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long readingRuleId = (Long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		
		ReadingRuleContext rule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingRuleId,true,true);
		
		if (rule == null || !rule.isActive()) {
			return false;
		}

		FacilioModule module = rule.getModule();
		
		Map<String, List<WorkflowRuleContext>> workflowRuleCacheMap = new HashMap<String, List<WorkflowRuleContext>>();
		Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), null, placeHolders);

		WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(Collections.singletonList(rule), module, null, null, recordPlaceHolders, (FacilioContext)context,true, workflowRuleCacheMap, false, Collections.singletonList(rule.getActivityTypeEnum()));
		return false;
	}

}
