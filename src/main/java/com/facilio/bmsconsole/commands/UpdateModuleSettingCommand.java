package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingConfigUtil;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class UpdateModuleSettingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<ModuleSettingContext> moduleSetting = (List<ModuleSettingContext>) context.get(FacilioConstants.ContextNames.MODULE_SETTING);

        if (StringUtils.isNotEmpty(moduleName)) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module while getting module summary");

            if (CollectionUtils.isNotEmpty(moduleSetting)) {
                FacilioModule m = new FacilioModule();
                m.setModuleId(module.getModuleId());

                for (ModuleSettingContext setting : moduleSetting) {
                    String configuration = setting.getConfigurationName();
                    if(StringUtils.isEmpty(configuration)){
                        configuration = setting.getName();
                        setting.setConfigurationName(configuration);
                    }
                    setting.setName(setting.getConfigurationName());

                    if (configuration.equals(FacilioConstants.SettingConfigurationContextNames.STATE_FLOW)) {
                        m.setStateFlowEnabled(setting.isStatus());
                        modBean.updateModule(m);
                    }else {
                        ModuleSettingConfigUtil.updateModuleConfigurationStatus(module.getModuleId(),setting);
                    }

                }
                context.put(FacilioConstants.ContextNames.MODULE, m);
            }
        }
        return false;
    }

}
