package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;

public class GetModuleListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);

		boolean onlyCustom = false;
		if (moduleType == null || moduleType <= 0) {
			moduleType = ModuleType.BASE_ENTITY.getValue();
			onlyCustom = true;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> moduleList = modBean.getModuleList(ModuleType.valueOf(moduleType), onlyCustom);

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
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.HUDSON_YARDS)) {
				moduleList.add(modBean.getModule(FacilioConstants.ContextNames.WORKPERMIT));
			}	
			moduleList.add(modBean.getModule(ContextNames.TENANT_UNIT_SPACE));
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
				moduleList.add(modBean.getModule(ContextNames.TENANT));				
			}
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS)) {
				moduleList.add(modBean.getModule(ContextNames.PEOPLE));
			}
		}
		context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleList);
		return false;
	}

}
