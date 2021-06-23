package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class UpdateModuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String displayName = (String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME);
		String description = (String) context.get(FacilioConstants.ContextNames.MODULE_DESCRIPTION);
		Boolean stateFlowEnabled = (Boolean) context.get(FacilioConstants.ContextNames.STATE_FLOW_ENABLED);

		if (StringUtils.isNotEmpty(moduleName) && StringUtils.isNotEmpty(displayName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			FacilioModule m = new FacilioModule();
			m.setModuleId(module.getModuleId());
			m.setDisplayName(displayName);
			m.setDescription(description);
			m.setStateFlowEnabled(stateFlowEnabled);
			modBean.updateModule(m);
			
			module.setDisplayName(displayName);
			
			context.put(FacilioConstants.ContextNames.MODULE, m);
		}
		return false;
	}

}
