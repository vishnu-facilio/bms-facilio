package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

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
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.HUDSON_YARDS)) {
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.WORKPERMIT));
       }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.TENANT));
       }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
    	    
       }

        
        List<FacilioModule> customModules = new ArrayList<>();

        customModules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));
        
        Map<String, List<FacilioModule>> modules = new HashMap<String, List<FacilioModule>>();
        modules.put("systemModules", sysytemModules);
        modules.put("customModules", customModules);

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        
		return false;
	}

	

}
