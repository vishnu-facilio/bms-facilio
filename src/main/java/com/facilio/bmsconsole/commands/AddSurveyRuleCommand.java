package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.SurveyResponseRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class AddSurveyRuleCommand extends FacilioCommand {
	@Override
	public boolean executeCommand (Context context) throws Exception {

		WorkflowRuleContext workflowRule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		workflowRule.setRuleType(WorkflowRuleContext.RuleType.SATISFACTION_SURVEY_RULE);

		addRule(workflowRule);

		long workFlowRuleId = workflowRule.getId();

		// OnCreate rule for Survey
		List<ActionContext> executeOnCreateActions = (List<ActionContext>) context.get("executeCreateActions");
		SurveyResponseRuleContext surveyResponseRule = new SurveyResponseRuleContext();

		if(workFlowRuleId > 0 && CollectionUtils.isNotEmpty(executeOnCreateActions)){
			surveyResponseRule.setRuleId(workFlowRuleId);
			surveyResponseRule.setModuleName(FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE);
			surveyResponseRule.setRuleType(WorkflowRuleContext.RuleType.SURVEY_ACTION_RULE);
			surveyResponseRule.setActivityType(EventType.CREATE);
			surveyResponseRule.setActions(executeOnCreateActions);
			surveyResponseRule.setName(workflowRule.getName()+"_"+workFlowRuleId);

			addRule(surveyResponseRule);
		}

		// OnSubmit rule for survey
		List<ActionContext> executeOnSubmitActions = (List<ActionContext>) context.get("executeResponseActions");

		if(surveyResponseRule.getExecuteCreateRuleId() > 0 && CollectionUtils.isNotEmpty(executeOnSubmitActions)){
			surveyResponseRule.setActions(executeOnSubmitActions);
			surveyResponseRule.setActivityType(EventType.EDIT);

			addRule(surveyResponseRule);
		}

		return false;
	}

	private void addRule(WorkflowRuleContext workflowRule) throws Exception{

		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		FacilioContext facilioContext = chain.getContext();
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
		chain.execute();
	}
}
