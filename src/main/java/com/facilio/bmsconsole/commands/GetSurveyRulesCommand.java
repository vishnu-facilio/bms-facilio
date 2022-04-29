package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.SurveyResponseRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetSurveyRulesCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		Long ruleId = (Long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		List<ActionContext> executeCreateActions = new ArrayList<>();
		List<ActionContext> executeSubmitActions = new ArrayList<>();
		WorkflowRuleContext workflowRule = null;

		if(ruleId > 0){
			workflowRule = WorkflowRuleAPI.getWorkflowRule(ruleId);
			long workFlowRuleId = workflowRule.getId();
			if(workflowRule != null && workFlowRuleId > 0){
				SurveyResponseRuleContext surveyResponseRule = SurveyUtil.fetchChildRuleId(Collections.singletonList(workFlowRuleId));
				if(surveyResponseRule != null){
					long createRuleId = surveyResponseRule.getExecuteCreateRuleId();
					long submitRuleId = surveyResponseRule.getExecuteSubmitRuleId();

					if(createRuleId > 0){
						executeCreateActions.add((ActionContext) ActionAPI.getActiveActionsFromWorkflowRule(createRuleId));
					}
					if(submitRuleId > 0){
						executeSubmitActions.add((ActionContext) ActionAPI.getActiveActionsFromWorkflowRule(submitRuleId));
					}
				}
			}
		}

		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
		context.put(FacilioConstants.Survey.EXECUTE_RESPONSE_ACTIONS, executeSubmitActions);
		context.put(FacilioConstants.Survey.EXECUTE_CREATE_ACTIONS, executeCreateActions);

		return false;
	}

}
