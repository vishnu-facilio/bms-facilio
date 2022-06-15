package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class DeleteStateFlowTransition extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long stateFlowId = (Long) context.get(FacilioConstants.ContextNames.STATE_FLOW_ID);
		Long transitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);

		WorkflowRuleContext stateTransition = null;
		if (stateFlowId != null && transitionId != null) {
			stateTransition = StateFlowRulesAPI.getStateTransition(stateFlowId, transitionId);
			if (stateTransition == null) {
				throw new IllegalArgumentException("Invalid state transition");
			}
			WorkflowRuleAPI.deleteWorkflowRule(stateTransition.getId());
		}
		else {
			throw new IllegalArgumentException("stateFlowId and transitionId are mandatory");
		}
		context.put("workFlowRuleContext",stateTransition);
		return false;
	}

}
