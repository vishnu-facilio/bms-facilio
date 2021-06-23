package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.RentalLeaseContractContext;
import com.facilio.constants.FacilioConstants;

public class DuplicateRentalLeaseContractCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		RentalLeaseContractContext originalContext = (RentalLeaseContractContext)context.get(FacilioConstants.ContextNames.RECORD);
		RentalLeaseContractContext duplicatedContext = originalContext.clone();
		duplicatedContext.setRevisionNumber(0);
		context.put(FacilioConstants.ContextNames.RECORD, duplicatedContext);
		return false;
	}

}
