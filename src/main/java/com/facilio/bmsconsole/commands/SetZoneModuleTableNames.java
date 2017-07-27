package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class SetZoneModuleTableNames implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "zone");
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Zone");
		
		return false;
	}

}
