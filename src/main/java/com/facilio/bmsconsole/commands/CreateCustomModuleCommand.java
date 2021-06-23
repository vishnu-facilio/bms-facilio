package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;

public class CreateCustomModuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String displayName = (String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME);
		Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);
		String description = (String) context.get(FacilioConstants.ContextNames.MODULE_DESCRIPTION);
		Boolean stateFlowEnabled = (Boolean) context.get(FacilioConstants.ContextNames.STATE_FLOW_ENABLED);

		if(displayName != null && !displayName.isEmpty()) {
			FacilioModule module = new FacilioModule();
			module.setName("custom_" + displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			module.setDisplayName(displayName);
			module.setTableName("CustomModuleData");
			module.setStateFlowEnabled(stateFlowEnabled);
			module.setDescription(description);
			if (moduleType != null) {
				moduleType = ModuleType.BASE_ENTITY.getValue();
			}
			module.setCustom(true);
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
