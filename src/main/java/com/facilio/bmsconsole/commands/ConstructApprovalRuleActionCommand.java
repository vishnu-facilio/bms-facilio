package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;

public class ConstructApprovalRuleActionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ApprovalRuleContext approvalRule = (ApprovalRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (approvalRule != null) {
			ActionContext action = new ActionContext();
			action.setActionType(ActionType.FIELD_CHANGE);
			action.setTemplate(getTemplate(approvalRule));
			context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, Collections.singletonList(action));
		}
		return false;
	}
	
	private JSONTemplate getTemplate(ApprovalRuleContext rule) {
		JSONTemplate template = new JSONTemplate();
		
		JSONObject json = new JSONObject();
		json.put(FacilioConstants.ApprovalRule.APPROVAL_RULE_ID_FIELD_NAME, "${rule.id}");
		json.put(FacilioConstants.ApprovalRule.APPROVAL_STATE_FIELD_NAME, ApprovalState.REQUESTED.getValue());
		
		WorkflowContext workflow = new WorkflowContext();
		ParameterContext param = new ParameterContext();
		param.setName("rule.id");
		param.setTypeString("String");
		workflow.addParamater(param);
		
		ExpressionContext expression = new ExpressionContext();
		expression.setConstant("${rule.id}");
		workflow.addWorkflowExpression(expression);
		template.setWorkflow(workflow);
		template.setContent(json.toJSONString());
		template.setName(rule.getName()+"_Template");
		
		return template;
	}

}
