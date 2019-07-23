package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteStateTransitionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long stateFlowID = (long) context.get(FacilioConstants.ContextNames.STATE_FLOW_ID);
		long stateTransitionId = (long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
		if (stateFlowID > 0 && stateTransitionId > 0) {
			StateFlowRulesAPI.deleteStateTransition(stateFlowID, stateTransitionId);
		}
		return false;
	}

}
