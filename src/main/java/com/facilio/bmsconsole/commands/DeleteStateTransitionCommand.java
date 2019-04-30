package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class DeleteStateTransitionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long stateFlowID = (long) context.get(FacilioConstants.ContextNames.STATE_FLOW_ID);
		long stateTransitionId = (long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
		if (stateFlowID > 0 && stateTransitionId > 0) {
			StateFlowRulesAPI.deleteStateTransition(stateFlowID, stateTransitionId);
		}
		return false;
	}

}
