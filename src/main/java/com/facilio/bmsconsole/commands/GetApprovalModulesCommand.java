package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetApprovalModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
    	
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		List<String> MODULES;
    	//approval modules handling for ATG
    	if(orgId == 406l)
    	{
    		 MODULES = Arrays.asList(new String[] {
    	                FacilioConstants.ContextNames.WORK_ORDER,
    	                "custom_supplierselectionform"
    	     });
    	}
    	else
    	{
    		 MODULES = Arrays.asList(new String[] {
    	                FacilioConstants.ContextNames.WORK_ORDER,
    	                FacilioConstants.ContextNames.WorkPermit.WORKPERMIT,
    	                FacilioConstants.ContextNames.INVENTORY_REQUEST,
    	                FacilioConstants.ContextNames.SITE,
    	                FacilioConstants.ContextNames.BUILDING,
    	                FacilioConstants.ContextNames.FLOOR,
    	                FacilioConstants.ContextNames.SPACE
    	     });
    	}

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<JSONObject> moduleList = new ArrayList<JSONObject>();

        for(String moduleName: MODULES) {
            if (AccountUtil.isModuleLicenseEnabled(moduleName)) {
                FacilioModule module = modBean.getModule(moduleName);
                moduleList.add(getModuleJson(module));
            }
        }

        context.put("modules", moduleList);

        return false;
    }

    private JSONObject getModuleJson(FacilioModule module) {
        JSONObject obj = new JSONObject();
        obj.put("name" , module.getName());
        obj.put("displayName" , module.getDisplayName());
        obj.put("moduleId" , module.getModuleId());
        return obj;
    }
}
