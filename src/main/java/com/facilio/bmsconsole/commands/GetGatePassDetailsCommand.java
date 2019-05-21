package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.constants.FacilioConstants;

public class GetGatePassDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		GatePassContext gatePassContext = (GatePassContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(gatePassContext!=null) {
			GatePassAPI.setLineItems(gatePassContext);
			context.put(FacilioConstants.ContextNames.GATE_PASS, gatePassContext);
		}
		return false;
	}

}
