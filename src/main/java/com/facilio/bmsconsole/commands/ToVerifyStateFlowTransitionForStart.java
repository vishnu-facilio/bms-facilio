package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.constants.FacilioConstants;

public class ToVerifyStateFlowTransitionForStart extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<WorkOrderContext> wos = (List<WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		
		Long currentTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
	
		if (currentTransitionId != null && wos != null && wos.size() == 1) {
			if (wos.get(0).getQrEnabled() != null && wos.get(0).getQrEnabled() && wos.get(0).getResource() != null) {
			StateflowTransitionContext stateflowTransition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(currentTransitionId);
			if (stateflowTransition != null && stateflowTransition.getName().equals("Start Work")) {
				context.put(FacilioConstants.ContextNames.RESOURCE_ID, wos.get(0).getResource().getId());
				context.put(FacilioConstants.ContextNames.SHOULD_VERIFY_QR, true);
			}
		}
		}
		
		return false;
	}

}
