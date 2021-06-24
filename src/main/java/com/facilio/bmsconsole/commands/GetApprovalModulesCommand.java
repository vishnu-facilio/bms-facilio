package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetApprovalModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<String> MODULES = Arrays.asList(new String[] {
                FacilioConstants.ContextNames.WORK_ORDER,
                FacilioConstants.ContextNames.WorkPermit.WORKPERMIT,
                FacilioConstants.ContextNames.INVENTORY_REQUEST,
        });

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
