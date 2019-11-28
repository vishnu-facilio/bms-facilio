package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetSubFormModulesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            List<FacilioModule> subModules = new ArrayList<>();
            getSystemDefinedSubModules(modBean, module);
            List<FacilioModule> configuredSubModules = modBean.getSubModules(module.getModuleId(), FacilioModule.ModuleType.BASE_ENTITY, FacilioModule.ModuleType.CUSTOM);
            if (CollectionUtils.isNotEmpty(configuredSubModules)) {
                subModules.addAll(configuredSubModules);
            }

            context.put(FacilioConstants.ContextNames.MODULE_LIST, subModules);
        }
        return false;
    }

    private List<FacilioModule> getSystemDefinedSubModules(ModuleBean modBean, FacilioModule module) throws Exception{
        switch (module.getName()) {
//            case FacilioConstants.ContextNames.WORK_ORDER:
//                return Arrays.asList(modBean.getModule());
        
        case FacilioConstants.ContextNames.VENDORS:
          return Arrays.asList(modBean.getModule(FacilioConstants.ContextNames.INSURANCE));
        }
        return null;
    }
}
