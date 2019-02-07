package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ExecuteScheduledAlarmTriggerCommand implements Command {

	private static RuleType[] ruleTypes = {RuleType.READING_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE};
	@Override
	public boolean execute(Context context) throws Exception {
		
		Long readingRuleId = (Long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		
		ReadingRuleContext rule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingRuleId,true,true,true);
		
		WorkflowEventContext event = rule.getEvent();
		
		FacilioModule module = event.getModule();
		
		List<WorkflowRuleContext> currentWorkflows = Collections.singletonList(rule);

		Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), null, placeHolders);
		
		while (currentWorkflows != null && !currentWorkflows.isEmpty()) {
			Criteria criteria = WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(currentWorkflows, module.getName(), null, null, null, recordPlaceHolders, (FacilioContext)context,true);
			if (criteria == null || criteria.isEmpty()) {
				break;
			}
			currentWorkflows = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module.getModuleId(), Collections.singletonList(event.getActivityTypeEnum()), criteria, ruleTypes);
		}
		return false;
	}

}
