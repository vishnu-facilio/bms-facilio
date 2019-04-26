package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;

public class CreateCustomModuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);

		if(moduleName != null && !moduleName.isEmpty()) {
			FacilioModule module = new FacilioModule();
			module.setName(moduleName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			module.setDisplayName(moduleName);
			module.setTableName("CustomModuleData");
			if (moduleType != null) {
				moduleType = ModuleType.BASE_ENTITY.getValue();
			}
			module.setType(moduleType);

			context.put("should_suppress_exception", true);

			context.put(FacilioConstants.ContextNames.MODULE, module);
		}
		else {
			throw new IllegalArgumentException("Invalid Module Name during addition of Module");
		}
		return false;
	}

}
