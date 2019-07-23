package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.constants.FacilioConstants;

public class GetStateTransitionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long stateFlowId = (Long) context.get(FacilioConstants.ContextNames.ID);
		if (stateFlowId != null && stateFlowId > 0) {
			StateflowTransitionContext stateFlowRuleContext = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(stateFlowId);
			if (stateFlowRuleContext == null) {
				throw new IllegalArgumentException("Invalid id");
			}
			context.put(FacilioConstants.ContextNames.RECORD, stateFlowRuleContext);
		}
		else {
			throw new IllegalArgumentException("Invalid id");
		}
		return false;
	}

}
