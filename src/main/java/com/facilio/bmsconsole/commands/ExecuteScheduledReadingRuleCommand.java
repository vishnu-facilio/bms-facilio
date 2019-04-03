package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.Map;

public class ExecuteScheduledReadingRuleCommand implements Command {

	private static RuleType[] ruleTypes = {RuleType.READING_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE,RuleType.ALARM_RCA_RULES,RuleType.PM_READING_TRIGGER};
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		Long ruleId = (Long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(ruleId,true,true,true);
		if (rule.isActive()) {
			long executionTime = (long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
			WorkflowRuleAPI.executeScheduledRule(rule, executionTime, (FacilioContext) context);
		}
		return false;
	}

}
