package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.SurveyResponseRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
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

		List<SurveyResponseRuleContext> responseRule = SurveyUtil.fetchChildRuleId(Collections.singletonList(rule.getId()));

		boolean onCreateFlag = false;
		boolean onSubmitFlag = false;

		if(CollectionUtils.isNotEmpty(responseRule)){
			for(SurveyResponseRuleContext ruleContext : responseRule){
				switch(ruleContext.getActionType()){
					case ON_CREATE:
						addSurveyRuleActions(validateBeforeAddActions(createActions), WorkflowRuleAPI.getWorkflowRule(ruleContext.getId()));
						onCreateFlag = true;
						break;
					case ON_SUBMIT:
						addSurveyRuleActions(validateBeforeAddActions(responseActions), WorkflowRuleAPI.getWorkflowRule(ruleContext.getId()));
						onSubmitFlag = true;
						break;
				}
			}
		}
		if(!onCreateFlag ){
			SurveyUtil.addSurveyRuleActions(rule, createActions, new ArrayList<>());
		}

		if(!onSubmitFlag){
			SurveyUtil.addSurveyRuleActions(rule, new ArrayList<>(),responseActions);
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

}
