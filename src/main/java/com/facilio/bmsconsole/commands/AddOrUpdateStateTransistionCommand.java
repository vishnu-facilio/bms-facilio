package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.StateflowTransistionContext;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateStateTransistionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		StateflowTransistionContext stateFlowRuleContext = (StateflowTransistionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (stateFlowRuleContext != null) {
			Chain chain;
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
