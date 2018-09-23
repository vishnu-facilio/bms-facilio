package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;

public class GetWorkFlowOfRuleTypeCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		RuleType ruleType = (RuleType) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_TYPE);
		
		Boolean fetchEvent = (Boolean) context.get(FacilioConstants.ContextNames.WORKFLOW_FETCH_EVENT);
		if (fetchEvent == null) {
			fetchEvent = true;
		}
		
		Boolean fetchChildren = (Boolean) context.get(FacilioConstants.ContextNames.WORKFLOW_FETCH_CHILDREN);
		if (fetchChildren == null) {
			fetchChildren = true;
		}
		
		if(ruleType != null){
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, WorkflowRuleAPI.getWorkflowRulesOfType(ruleType, fetchEvent, fetchChildren));
		}
		return false;
	}
	
}