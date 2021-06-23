package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class DeleteRentalLeaseContractCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
//		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CONTRACTS);
		context.put(FacilioConstants.ContextNames.PREFERENCE_NAMES, "expireDateNotification,paymentNotification");
		
		return false;
	}

}
