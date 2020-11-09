package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
		if(CollectionUtils.isNotEmpty(moduleList) && onlyCustom && (AccountUtil.getCurrentOrg().getOrgId() == 321l || AccountUtil.getCurrentOrg().getOrgId() == 173l)){
			List<FacilioModule> splModules = moduleList.stream()
					.filter(mod->mod.getName().equals("custom_tenantcontract") || mod.getName().equals("custom_timesheetmanagement") || mod.getName().equals("custom_servicebilllineitems"))
					.collect(Collectors.toList());

			if(CollectionUtils.isNotEmpty(splModules)){
				splModules.sort(new Comparator<FacilioModule>() {
					@Override
					public int compare(FacilioModule o1, FacilioModule o2) {
						return Long.compare(o2.getModuleId(),o1.getModuleId());
					}
				});
				moduleList.removeAll(splModules);
				moduleList.addAll(0,splModules);
			}
		}

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
				moduleList.add(modBean.getModule(ContextNames.WorkPermit.WORKPERMIT));
			}
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONTRACT)) {
				moduleList.add(modBean.getModule(ContextNames.SERVICE));
			}
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
				moduleList.add(modBean.getModule(ContextNames.TENANT));
				moduleList.add(modBean.getModule(ContextNames.TENANT_UNIT_SPACE));
			}
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS)) {
				moduleList.add(modBean.getModule(ContextNames.PEOPLE));
			}
			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLIENT)) {
				moduleList.add(modBean.getModule(ContextNames.CLIENT));
			}
		}
		context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleList);
		return false;
	}

}
