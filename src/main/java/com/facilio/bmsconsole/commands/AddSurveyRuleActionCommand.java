package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
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

		if(CollectionUtils.isNotEmpty(createActions)){
			long createRuleId = (long) context.get("createRuleId");
			addSurveyRuleActions(validateBeforeAddActions(createActions), WorkflowRuleAPI.getWorkflowRule(createRuleId));
		}

		if(CollectionUtils.isNotEmpty(responseActions)){
			long submitRuleId = (long) context.get("submitRuleId");
			addSurveyRuleActions(validateBeforeAddActions(responseActions),WorkflowRuleAPI.getWorkflowRule(submitRuleId));
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
		ActionAPI.addActions(actions,rule);
	}
}
