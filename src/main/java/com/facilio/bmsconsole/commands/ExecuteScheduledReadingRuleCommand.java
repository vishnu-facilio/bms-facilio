package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ExecuteScheduledReadingRuleCommand implements Command {

	private RuleType[] ruleTypes = {RuleType.READING_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE};
	@Override
	public boolean execute(Context context) throws Exception {
		
		Long readingRuleId = (Long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		
		ReadingRuleContext rule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingRuleId,true,true,true);
		
		WorkflowEventContext event = rule.getEvent();
		
		FacilioModule module = event.getModule();
		
		Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), null, placeHolders);

		List<WorkflowRuleContext> currentWorkflows = Collections.singletonList(rule);
		while (currentWorkflows != null && !currentWorkflows.isEmpty()) {
			Criteria childCriteria = WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(currentWorkflows, module.getName(), null, null, null, recordPlaceHolders, (FacilioContext)context,true);
			if (childCriteria == null) {
				break;
			}
			currentWorkflows = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module.getModuleId(), Collections.singletonList(event.getActivityTypeEnum()), childCriteria, ruleTypes);
		}
		
		return false;
	}

}
