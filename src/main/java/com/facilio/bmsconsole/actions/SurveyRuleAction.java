package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SurveyRuleAction extends FacilioAction{


	private List<ActionContext> executeCreateActions;
	private List<ActionContext> executeResponseActions;
	private WorkflowRuleContext rule;
	private List<Long> ruleIds;
	private Long workFlowRuleId;

	public String addSurveyRule() throws Exception
	{
		FacilioChain addRule = TransactionChainFactory.addSurveyRuleChain();
		FacilioContext context = addRule.getContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put("executeCreateActions",executeCreateActions);
		context.put("executeResponseActions",executeResponseActions);

		addRule.execute();

		setResult("rule", rule);

		return SUCCESS;
	}

	public String fetchSurveyRule() throws Exception {

		FacilioChain c = ReadOnlyChainFactory.getSurveyRulesChain();
		FacilioContext context = c.getContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID,workFlowRuleId);
		c.execute();

		setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE));
		setResult(FacilioConstants.Survey.EXECUTE_CREATE_ACTIONS,context.get(FacilioConstants.Survey.EXECUTE_CREATE_ACTIONS));
		setResult(FacilioConstants.Survey.EXECUTE_RESPONSE_ACTIONS,context.get(FacilioConstants.Survey.EXECUTE_RESPONSE_ACTIONS));

		return SUCCESS;
	}

	public String updateSurveyRule() throws Exception {

		FacilioChain updateRule = TransactionChainFactory.updateSurveyRuleChain();
		FacilioContext context = updateRule.getContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put(FacilioConstants.Survey.EXECUTE_CREATE_ACTIONS,executeCreateActions);
		context.put(FacilioConstants.Survey.EXECUTE_RESPONSE_ACTIONS,executeResponseActions);

		updateRule.execute();

		setResult("rule", rule);

		return SUCCESS;
	}

	public String deleteSurveyRule() throws Exception {

		FacilioChain deleteRule = TransactionChainFactory.deleteSurveyRuleChain();
		FacilioContext context = deleteRule.getContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID, ruleIds);

		deleteRule.execute();

		return SUCCESS;
	}
}
