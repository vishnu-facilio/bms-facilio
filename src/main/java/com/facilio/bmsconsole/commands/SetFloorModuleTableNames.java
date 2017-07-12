package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class SetFloorModuleTableNames implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "Floor");
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Floor");
		
		return false;
	}

}
