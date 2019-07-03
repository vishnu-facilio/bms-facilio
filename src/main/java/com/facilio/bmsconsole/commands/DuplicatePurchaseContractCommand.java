package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PurchaseContractContext;
import com.facilio.constants.FacilioConstants;

public class DuplicatePurchaseContractCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PurchaseContractContext originalContext = (PurchaseContractContext)context.get(FacilioConstants.ContextNames.RECORD);
		PurchaseContractContext duplicatedContext = originalContext.clone();
		duplicatedContext.setRevisionNumber(0);
		context.put(FacilioConstants.ContextNames.RECORD, duplicatedContext);
		return false;
	}

}
