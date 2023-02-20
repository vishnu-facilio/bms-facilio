package com.facilio.bmsconsole.ModuleSettingConfig.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingConfigUtil;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GetModuleSettingConfigDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule facilioModule = (FacilioModule) context.getOrDefault(FacilioConstants.ContextNames.MODULE,null);
        List<ModuleSettingContext> moduleSetting = (List<ModuleSettingContext>) context.get(FacilioConstants.ContextNames.MODULE_SETTING) ;

        if (StringUtils.isNotEmpty(moduleName)) {

            if(facilioModule == null){
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                facilioModule = modBean.getModule(moduleName);
            }

            FacilioUtil.throwIllegalArgumentException(facilioModule == null, "Invalid module while getting module summary");

            List<ModuleSettingContext> tempModuleSetting = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(moduleSetting)){
                tempModuleSetting = moduleSetting;
            }
            tempModuleSetting.removeIf(moduleSettingContext -> Objects.equals(moduleSettingContext.getName(), FacilioConstants.ContextNames.GLIMPSE));
            if(CollectionUtils.isNotEmpty(tempModuleSetting)){
                for(ModuleSettingContext setting : tempModuleSetting) {

                    Object result = null;
                    if (setting.isStatusDependent() && !(boolean) setting.isStatus()) {
                        continue;
                    } else {
                        result = ModuleSettingConfigUtil.getModuleConfigurationDetails(setting.getConfigurationName(), facilioModule);
                    }

                    setting.setConfigurationDetails(result);
                }
            }

            context.put(FacilioConstants.ContextNames.MODULE_SETTING, tempModuleSetting);
        }

        return false;
    }

}
