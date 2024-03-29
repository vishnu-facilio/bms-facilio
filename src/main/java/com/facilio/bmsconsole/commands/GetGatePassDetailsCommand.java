package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.bmsconsole.util.GatePassAPI;
import com.facilio.constants.FacilioConstants;

public class GetGatePassDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		GatePassContext gatePassContext = (GatePassContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(gatePassContext!=null) {
			GatePassAPI.setLineItems(gatePassContext);
			context.put(FacilioConstants.ContextNames.GATE_PASS, gatePassContext);
		}
		return false;
	}

}
