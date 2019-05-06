package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class UpdateStateForWorkorderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<WorkOrderContext> oldWos = (List<WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		Long currentTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

		// there is no transition info
		if (currentTransitionId == null) {
			return false;
		}
		
		if (oldWos != null && !oldWos.isEmpty() && workOrder != null) {
			StateflowTransitionContext stateflowTransition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(currentTransitionId, true);
			for (WorkOrderContext wo : oldWos) {
				Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, wo, WorkflowRuleAPI.getOrgPlaceHolders());
				boolean shouldChangeState = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateflowTransition, moduleName, wo, StateFlowRulesAPI.getDefaultFieldChangeSet(moduleName, wo.getId()), recordPlaceHolders, (FacilioContext) context, false);
				if (shouldChangeState) {
					TicketStatusContext newState = StateFlowRulesAPI.getStateContext(stateflowTransition.getToStateId());
					if (newState == null) {
						throw new Exception("Invalid state");
					}
					stateflowTransition.executeTrueActions(wo, context, recordPlaceHolders);
				} 
			}
		}
		return false;
	}

}
