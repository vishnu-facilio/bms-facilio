package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddSurveyRuleCommand extends FacilioCommand {
	@Override
	public boolean executeCommand (Context context) throws Exception {

		WorkflowRuleContext workflowRule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		workflowRule.setRuleType(WorkflowRuleContext.RuleType.SATISFACTION_SURVEY_RULE);
		SurveyUtil.addRule(workflowRule);

		SurveyUtil.addSurveyRuleActions(workflowRule, (List<ActionContext>) context.get("executeCreateActions"), (List<ActionContext>) context.get("executeResponseActions"));

		return false;
	}
}
