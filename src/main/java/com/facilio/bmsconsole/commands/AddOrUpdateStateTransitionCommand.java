package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateStateTransitionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		StateflowTransitionContext stateFlowRuleContext = (StateflowTransitionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (stateFlowRuleContext != null) {
			FacilioChain chain;
			if (stateFlowRuleContext.getId() < 0) {
				chain = TransactionChainFactory.addWorkflowRuleChain();
			} 
			else {
				chain = TransactionChainFactory.updateWorkflowRuleChain();
			}
			
			chain.execute(context);
		}
		return false;
	}

}
