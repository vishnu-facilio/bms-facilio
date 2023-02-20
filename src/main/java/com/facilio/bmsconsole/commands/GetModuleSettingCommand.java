package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingConfigUtil;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingEnum;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetModuleSettingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module while getting module summary");

            List<String> configurationList = ModuleSettingConfigUtil.COMMON_CONFIGURATION_LIST;
            List<String> availableConfigurationName = new ArrayList<>();
            List<ModuleSettingContext> moduleSettingConfigurations = new ArrayList<>();
            List<ModuleSettingContext> availableModuleConfigurations = ModuleSettingConfigUtil.getModuleSettingConfigurations(module);

            if(CollectionUtils.isNotEmpty(availableModuleConfigurations)){
                availableConfigurationName = availableModuleConfigurations.stream().map(ModuleSettingContext::getConfigurationName).collect(Collectors.toList());
            }

            for(String configurationName : configurationList){
                if(!availableConfigurationName.contains(configurationName) ){

                    ModuleSettingContext setting = new ModuleSettingContext();
                    setting.setName(configurationName);
                    setting.setConfigurationName(configurationName);
                    setting.setModuleId(module.getModuleId());

                    if(Objects.equals(configurationName, FacilioConstants.SettingConfigurationContextNames.STATE_FLOW)){
                        setting.setStatus(module.isStateFlowEnabled());
                    }else {
                        setting.setStatus(false);
                    }

                    moduleSettingConfigurations.add(setting);

                }
            }
            moduleSettingConfigurations.addAll(availableModuleConfigurations);
            moduleSettingConfigurations.forEach(moduleSettingContext -> {

                ModuleSettingEnum moduleSettingEnum = ModuleSettingEnum.moduleConfigMap.get(moduleSettingContext.getConfigurationName());
                if(moduleSettingEnum != null) {
                    moduleSettingContext.setName(moduleSettingContext.getConfigurationName());
                    moduleSettingContext.setDescription(moduleSettingEnum.getDescription());
                    moduleSettingContext.setDisplayName(moduleSettingEnum.getDisplayName());
                    moduleSettingContext.setStatusDependent(moduleSettingEnum.isStatusDependent());
                }
            });

            context.put(FacilioConstants.ContextNames.MODULE,module);
            context.put(FacilioConstants.ContextNames.MODULE_SETTING, moduleSettingConfigurations);

        }
        return false;
    }
}
