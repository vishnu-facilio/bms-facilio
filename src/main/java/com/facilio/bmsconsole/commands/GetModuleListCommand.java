package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;

public class GetModuleListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);
		if (moduleType == null || moduleType <= 0) {
			moduleType = ModuleType.CUSTOM.getValue();
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> moduleList = modBean.getModuleList(ModuleType.valueOf(moduleType));

		Boolean fetchDefaultModules = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_DEFAULT_MODULES);
		if (fetchDefaultModules != null && fetchDefaultModules) {
			moduleList.add(modBean.getModule("workorder"));
			moduleList.add(modBean.getModule("users"));
			moduleList.add(modBean.getModule("asset"));
			moduleList.add(modBean.getModule("site"));
			moduleList.add(modBean.getModule("building"));
			moduleList.add(modBean.getModule("space"));
			moduleList.add(modBean.getModule("alarm"));
			moduleList.add(modBean.getModule("vendors"));
		}
		context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleList);
		return false;
	}

}
