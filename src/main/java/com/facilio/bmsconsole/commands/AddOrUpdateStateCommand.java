package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class AddOrUpdateStateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
//		StateContext state = (StateContext) context.get(FacilioConstants.ContextNames.STATE);
//		if (state != null) {
//			StateFlowRulesAPI.addOrUpdateState(state);
//		}
		return false;
	}

}
