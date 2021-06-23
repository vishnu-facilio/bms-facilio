package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.*;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateStateTransitionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		StateflowTransitionContext stateTransition = (StateflowTransitionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (stateTransition != null) {
			FacilioChain chain;
			if (stateTransition.getId() < 0) {
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
