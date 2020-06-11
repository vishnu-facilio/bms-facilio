package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetModulesListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> sysytemModules = new ArrayList<>();
        sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.ASSET));
        sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VISITOR)) {
        	sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
        }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONTRACT)) {
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS));
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS));
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS));
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS));
        }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.TENANT));
       }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
       }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SERVICE_REQUEST)) {
            sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
            sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.QUOTATION));
        }

            sysytemModules.add(modBean.getModule(ContextNames.TENANT_UNIT_SPACE));
        
        List<FacilioModule> customModules = new ArrayList<>();

        customModules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));
        
        Map<String, List<FacilioModule>> modules = new HashMap<String, List<FacilioModule>>();
        modules.put("systemModules", sysytemModules);
        modules.put("customModules", customModules);

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        
		return false;
	}

	

}
