package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.bmsconsole.util.GatePassAPI;
import com.facilio.constants.FacilioConstants;

public class SetGatePassLineItemsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<GatePassContext> records = (List<GatePassContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (records != null && !records.isEmpty()) {
			for(GatePassContext gatepass : records) {
				GatePassAPI.setLineItems(gatepass);
			}
		}
		return false;
	}

}
