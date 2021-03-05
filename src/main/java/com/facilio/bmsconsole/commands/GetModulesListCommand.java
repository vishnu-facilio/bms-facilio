package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetModulesListCommand extends FacilioCommand {

	private static final List<String> MODULES = Arrays.asList(new String[] {
			ContextNames.WORK_ORDER,
			ContextNames.ASSET,
			ContextNames.VENDORS,
			ContextNames.BASE_VISIT,
			ContextNames.VISITOR_LOG,
			ContextNames.INVITE_VISITOR,
			ContextNames.PURCHASE_CONTRACTS,
			ContextNames.LABOUR_CONTRACTS,
			ContextNames.RENTAL_LEASE_CONTRACTS,
			ContextNames.WARRANTY_CONTRACTS,
			ContextNames.SERVICE,
			ContextNames.TENANT,
			ContextNames.TENANT_UNIT_SPACE,
			ContextNames.PURCHASE_REQUEST,
			ContextNames.PURCHASE_ORDER,
			ContextNames.SERVICE_REQUEST,
			ContextNames.QUOTE,
			ContextNames.WorkPermit.WORKPERMIT,
		});
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> sytemModules = new ArrayList<>();
		for(String moduleName: MODULES) {
			if (AccountUtil.isModuleLicenseEnabled(moduleName)) {
				sytemModules.add(modBean.getModule(moduleName));
			}
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
