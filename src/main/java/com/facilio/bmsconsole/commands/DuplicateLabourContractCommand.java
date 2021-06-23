package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.LabourContractContext;
import com.facilio.constants.FacilioConstants;

public class DuplicateLabourContractCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		LabourContractContext originalContext = (LabourContractContext)context.get(FacilioConstants.ContextNames.RECORD);
		LabourContractContext duplicatedContext = originalContext.clone();
		duplicatedContext.setRevisionNumber(0);
		context.put(FacilioConstants.ContextNames.RECORD, duplicatedContext);
		return false;
	}

}
