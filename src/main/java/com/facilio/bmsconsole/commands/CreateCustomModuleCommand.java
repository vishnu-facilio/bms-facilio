package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class CreateCustomModuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName != null && !moduleName.isEmpty()) {
			FacilioModule module = new FacilioModule();
			module.setName(moduleName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			module.setDisplayName(moduleName);
			module.setTableName("CustomModuleData");
			
			context.put(FacilioConstants.ContextNames.MODULE, module);
		}
		else {
			throw new IllegalArgumentException("Invalid Module Name during addition of Module");
		}
		return false;
	}

}
