package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.SurveyResponseRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AddSurveyRuleActionCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		List<ActionContext> createActions = (List<ActionContext>) context.get(FacilioConstants.Survey.EXECUTE_CREATE_ACTIONS);
		List<ActionContext> responseActions = (List<ActionContext>) context.get(FacilioConstants.Survey.EXECUTE_RESPONSE_ACTIONS);

		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		List<ActionContext> ruleAction = rule.getActions();

		if(CollectionUtils.isNotEmpty(ruleAction)){
			addSurveyRuleActions(validateBeforeAddActions(ruleAction), WorkflowRuleAPI.getWorkflowRule(rule.getId()));
		}

		SurveyResponseRuleContext surveyResponseRule = new SurveyResponseRuleContext();

		if(CollectionUtils.isNotEmpty(createActions)){

			Long createRuleId = null;
			if(context != null && context.containsKey("createRuleId")){
				createRuleId = (long) context.getOrDefault("createRuleId",null);
			}

			if(createRuleId != null && createRuleId > 0L){
				addSurveyRuleActions(validateBeforeAddActions(createActions), WorkflowRuleAPI.getWorkflowRule(createRuleId));
			}else{
					surveyResponseRule.setRuleId(rule.getId());
					surveyResponseRule.setModuleName(FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE);
					surveyResponseRule.setRuleType(WorkflowRuleContext.RuleType.SURVEY_ACTION_RULE);
					surveyResponseRule.setActivityType(EventType.CREATE);
					surveyResponseRule.setActions(createActions);
					surveyResponseRule.setName(rule.getName() + "_" + rule.getId());

					addRule(surveyResponseRule);
			}
		}

		if(CollectionUtils.isNotEmpty(responseActions)){

			Long submitRuleId = null;

			if(context != null && context.containsKey("submitRuleId")){
				submitRuleId = (long) context.getOrDefault("submitRuleId",null);
			}

			if(submitRuleId != null && submitRuleId > 0L){
				addSurveyRuleActions(validateBeforeAddActions(responseActions), WorkflowRuleAPI.getWorkflowRule(submitRuleId));
			}else{
				surveyResponseRule.setActions(responseActions);
				surveyResponseRule.setActivityType(EventType.EDIT);

				addRule(surveyResponseRule);
			}

		}

		return false;
	}

	private List<ActionContext> validateBeforeAddActions(List<ActionContext> actions) {
		List<ActionContext> actionContexts = new ArrayList<>();
		for (ActionContext action : actions) {
			ActionContext actionContext = new ActionContext();
			actionContext.setActionType(action.getActionType());
			actionContext.setTemplateJson(action.getTemplateJson());
			actionContexts.add(actionContext);
		}
		return actionContexts;
	}

	private void addSurveyRuleActions(List<ActionContext> actions , WorkflowRuleContext rule) throws Exception{
		ActionAPI.addWorkflowRuleActionRel(rule.getId(), ActionAPI.addActions(actions, rule));
	}

	private void addRule(WorkflowRuleContext workflowRule) throws Exception{

		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		FacilioContext facilioContext = chain.getContext();
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
		chain.execute();
	}
}
