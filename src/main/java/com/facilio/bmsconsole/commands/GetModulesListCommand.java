package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetModulesListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> sytemModules = new ArrayList<>();
		sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.ASSET));
		sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VISITOR)) {
			sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONTRACT)) {
			sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS));
			sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS));
			sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS));
			sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS));
			sytemModules.add(modBean.getModule(ContextNames.SERVICE));
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
			sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.TENANT));
			sytemModules.add(modBean.getModule(ContextNames.TENANT_UNIT_SPACE));
		}

		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
			sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
			sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SERVICE_REQUEST)) {
			sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
			sytemModules.add(modBean.getModule(FacilioConstants.ContextNames.QUOTE));
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.HUDSON_YARDS)) {
			sytemModules.add(modBean.getModule(ContextNames.WorkPermit.WORKPERMIT));
		}


		List<FacilioModule> customModules = new ArrayList<>();

		customModules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));
		
		
		if(CollectionUtils.isNotEmpty(customModules) && (AccountUtil.getCurrentOrg().getOrgId() == 321l || AccountUtil.getCurrentOrg().getOrgId() == 173l)){
			List<FacilioModule> splModules = customModules.stream()
					.filter(mod->mod.getName().equals("custom_tenantcontract") || mod.getName().equals("custom_timesheetmanagement") || mod.getName().equals("custom_servicebilllineitems"))
					.collect(Collectors.toList());

			if(CollectionUtils.isNotEmpty(splModules)){
				splModules.sort(new Comparator<FacilioModule>() {
					@Override
					public int compare(FacilioModule o1, FacilioModule o2) {
						return Long.compare(o2.getModuleId(),o1.getModuleId());
					}
				});
				customModules.removeAll(splModules);
				customModules.addAll(0,splModules);
			}
		}
		
		

		Map<String, List<FacilioModule>> modules = new HashMap<String, List<FacilioModule>>();
		modules.put("systemModules", sytemModules);
		modules.put("customModules", customModules);

		context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        
		return false;
	}

	

}
