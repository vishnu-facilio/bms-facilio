package com.facilio.moduleBuilder.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.moduleBuilder.util.ResponseFormatUtil;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GetSubModulesForTransactionRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ModuleListConfig.MODULES);
        Map<String, Object> resultMap = (Map<String, Object>) context.getOrDefault(FacilioConstants.ContextNames.RESULT, new HashMap<>());

        Map<String, Object> subModulesMap = new HashMap<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(CollectionUtils.isNotEmpty(modules)) {
            for(FacilioModule module : modules) {
                    subModulesMap.put(module.getName(), getSubModules(modBean, module));
            }
        }
        resultMap.put("subModules", subModulesMap);

        context.put(FacilioConstants.ContextNames.RESULT, resultMap);
        return false;
    }

    private Map<String, Object> getSubModules(ModuleBean modBean, FacilioModule module) throws Exception {
        List<FacilioModule> otherSubModules = new ArrayList<>();

        if(module.getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {
            otherSubModules.add(modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS));
            otherSubModules.add(modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS));
            otherSubModules.add(modBean.getModule(FacilioConstants.ContextNames.WO_LABOUR));
            otherSubModules.add(modBean.getModule(FacilioConstants.ContextNames.WO_SERVICE));
            otherSubModules.add(modBean.getModule(FacilioConstants.ContextNames.WORKORDER_COST));
        }

        List<FacilioModule> subModules = modBean.getSubModules(module.getName());
        subModules = subModules != null ? subModules : new ArrayList<>();
        if(CollectionUtils.isNotEmpty(otherSubModules)) {
            subModules.addAll(otherSubModules);
        }

        Map<String, Object> subModuleMap = new HashMap<>();
        for (FacilioModule subModule : subModules) {
            subModuleMap.put(subModule.getName(),
                    ResponseFormatUtil.formatModuleBasedOnResponseFields(subModule, Arrays.asList("moduleId", "name", "displayName"), false));
        }

        return subModuleMap;

    }
}
