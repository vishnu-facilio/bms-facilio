package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class SetTaskModuleTableNames implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		context.put(FacilioConstants.ContextNames.MODULE_OBJECTS_TABLE_NAME, "Tasks_Objects");
		context.put(FacilioConstants.ContextNames.MODULE_FIELDS_TABLE_NAME, "Tasks_Fields");
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Tasks_Data");
		
		return false;
	}

}
