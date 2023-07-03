package com.facilio.bmsconsoleV3.commands.pivot;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetPivotModulesListCommand extends FacilioCommand {

    private List<String> fault_modules = Arrays.asList(FacilioConstants.ContextNames.ALARM_OCCURRENCE, FacilioConstants.ContextNames.BMS_ALARM_OCCURRENCE);
    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        Long webTabId = (Long) context.get("webTabId");
        List<JSONObject> system_modules = new ArrayList<>();
        List<JSONObject> custom_modules = new ArrayList<>();
        if(webTabId != null && webTabId > 0){
            FacilioChain chain = ReadOnlyChainFactory.geAllModulesChain();
            FacilioContext module_context = chain.getContext();
            chain.execute();
            system_modules = (ArrayList) module_context.get("systemModules");
            custom_modules = (ArrayList) module_context.get("customModules");
        }
        else
        {
            FacilioChain chain = ReadOnlyChainFactory.getAutomationModules();
            chain.execute();
            FacilioContext module_context = chain.getContext();
            List<FacilioModule> modules = (ArrayList) module_context.get(FacilioConstants.ContextNames.MODULE_LIST);
            if(modules != null && modules.size() > 0) {
                for (FacilioModule module : modules) {
                    if (module != null) {
                        if(module.isCustom()){
                            custom_modules.add(getModuleJson(module));
                        }else{
                            system_modules.add(getModuleJson(module));
                        }
                    }
                }
            }
        }
        fetchModules(system_modules);

        context.put("systemModules", system_modules);
        context.put("customModules", custom_modules);
        return false;
    }

    private JSONObject getModuleJson(FacilioModule module) {
        JSONObject obj = new JSONObject();
        obj.put("name" , module.getName());
        obj.put("displayName" , module.getDisplayName());
        obj.put("moduleId" , module.getModuleId());
        return obj;
    }


    private void fetchModules(List<JSONObject> systemModules)throws Exception
    {
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS))
        {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            for(String module_name : fault_modules)
            {
                FacilioModule fdd_module = modBean.getModule(module_name);
                if(fdd_module != null){
                    systemModules.add(getModuleJson(fdd_module));
                }
            }
        }
    }
}
