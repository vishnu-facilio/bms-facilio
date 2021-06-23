package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;

public class GetServiceRequestSubModules extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioModule> subModules = modBean.getSubModules(moduleName, ModuleType.BASE_ENTITY);
		context.put(FacilioConstants.ContextNames.SUB_MODULES, subModules);
		return false;
	}

}
