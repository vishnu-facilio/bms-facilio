package com.facilio.moduleBuilder.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetSubModuleListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<String> subModulesToFetch = (List<String>) context.get(FacilioConstants.ModuleListConfig.SUBMODULES_TO_FETCH);

        if(CollectionUtils.isNotEmpty(subModulesToFetch)) {
            List<FacilioModule> subModules = new ArrayList<>();

            for(String moduleName : subModulesToFetch) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                if(module != null) {
                    subModules.add(module);
                }
            }

            context.put(FacilioConstants.ModuleListConfig.SUB_MODULES, subModules);
        }
        return false;
    }
}
