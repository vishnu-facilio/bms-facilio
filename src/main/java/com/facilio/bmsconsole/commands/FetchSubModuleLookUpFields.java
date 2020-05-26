package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class FetchSubModuleLookUpFields extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule parentModule = modBean.getModule(moduleName);
		if(parentModule == null) {
			throw new IllegalArgumentException("Please provide a valid parent Module for the rollUpField.");
		}

		List<FacilioModule> childSubModules = modBean.getAllSubModules(parentModule.getModuleId());
		for(FacilioModule childSubModule:childSubModules) {
			if(childSubModule.getTypeEnum().isReadingType()) {
				childSubModules.remove(childSubModule);
			}
		}
		
		context.put(FacilioConstants.ContextNames.SUB_MODULES, childSubModules);
		
		return false;
	}

}
