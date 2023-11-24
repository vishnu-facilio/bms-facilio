package com.facilio.moduleBuilder.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetModuleListFromBuilderCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<String> modulesToFetch = (List<String>) context.get(FacilioConstants.ModuleListConfig.MODULES_TO_FETCH);
        boolean fetchCustomModules = (boolean) context.getOrDefault(FacilioConstants.ModuleListConfig.FETCH_CUSTOM_MODULES, false);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> modules = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(modulesToFetch)) {

            for (String moduleName : modulesToFetch) {
                if (AccountUtil.isModuleLicenseEnabled(moduleName)) {
                    FacilioModule module = modBean.getModule(moduleName);
                    if (module != null) {
                        modules.add(module);
                    }
                }

            }
        }
        if(fetchCustomModules) {
            modules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));
        }
        context.put(FacilioConstants.ModuleListConfig.MODULES, modules);
        return false;
    }
}
