package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateContext;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateStateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		StateContext state = (StateContext) context.get(FacilioConstants.ContextNames.STATE);
		if (state != null) {
			StateFlowRulesAPI.addOrUpdateState(state);
		}
		return false;
	}

}
