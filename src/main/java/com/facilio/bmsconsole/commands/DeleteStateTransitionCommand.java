package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

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
