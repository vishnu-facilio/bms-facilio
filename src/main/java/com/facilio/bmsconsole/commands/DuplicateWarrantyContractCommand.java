package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WarrantyContractContext;
import com.facilio.constants.FacilioConstants;

public class DuplicateWarrantyContractCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WarrantyContractContext originalContext = (WarrantyContractContext)context.get(FacilioConstants.ContextNames.RECORD);
		WarrantyContractContext duplicatedContext = originalContext.clone();
		duplicatedContext.setRevisionNumber(0);
		context.put(FacilioConstants.ContextNames.RECORD, duplicatedContext);
		return false;
	}

}
