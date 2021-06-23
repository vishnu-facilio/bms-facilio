package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PurchaseContractContext;
import com.facilio.constants.FacilioConstants;

public class DuplicatePurchaseContractCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		PurchaseContractContext originalContext = (PurchaseContractContext)context.get(FacilioConstants.ContextNames.RECORD);
		PurchaseContractContext duplicatedContext = originalContext.clone();
		duplicatedContext.setRevisionNumber(0);
		context.put(FacilioConstants.ContextNames.RECORD, duplicatedContext);
		return false;
	}

}
