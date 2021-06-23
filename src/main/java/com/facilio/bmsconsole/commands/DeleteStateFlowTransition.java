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
		
		if (stateFlowId != null && transitionId != null) {
			WorkflowRuleContext stateTransition = StateFlowRulesAPI.getStateTransition(stateFlowId, transitionId);
			WorkflowRuleAPI.deleteWorkflowRule(stateTransition.getId());
		}
		
		return false;
	}

}
