package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;

public class GetStateTransitionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long transitionId = (Long) context.get(FacilioConstants.ContextNames.ID);
		if (transitionId != null && transitionId > 0) {
			AbstractStateTransitionRuleContext abstractStateTransitionContext = (AbstractStateTransitionRuleContext) WorkflowRuleAPI.getWorkflowRule(transitionId);
			if (abstractStateTransitionContext == null) {
				throw new IllegalArgumentException("Invalid id");
			}
			abstractStateTransitionContext.setActions(ActionAPI.getActiveActionsFromWorkflowRule(abstractStateTransitionContext.getId()));
			((StateflowTransitionContext) abstractStateTransitionContext).setStateFlowTransitionSequence(WorkflowRuleAPI.getTransitionActionSequence(transitionId));
			context.put(FacilioConstants.ContextNames.RECORD, abstractStateTransitionContext);
		}
		else {
			throw new IllegalArgumentException("Invalid id");
		}
		return false;
	}

}
