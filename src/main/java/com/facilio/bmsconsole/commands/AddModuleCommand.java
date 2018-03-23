package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddModuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		if(module != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			long moduleId = modBean.addModule(module);
			module.setModuleId(moduleId);
			
			String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
			if(parentModuleName != null && !parentModuleName.isEmpty()) {
				FacilioModule parentModule = modBean.getModule(parentModuleName);
				modBean.addSubModule(parentModule.getModuleId(), moduleId);
			}
		}
		else {
			throw new IllegalArgumentException("No Module to Add");
		}
		return false;
	}

}
