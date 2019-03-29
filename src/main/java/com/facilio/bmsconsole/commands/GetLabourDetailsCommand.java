package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class GetLabourDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			LabourContext labour = (LabourContext) context.get(FacilioConstants.ContextNames.RECORD);
			context.put(FacilioConstants.ContextNames.LABOUR, labour);
		}
		return false;
	}

}