package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetModuleSettingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module while getting module summary");

            List<Map<String, Object>> moduleSetting = new ArrayList<>();

            Map<String, Object> setting = new HashMap<>();
            setting.put("name", "stateflow");
            setting.put("status", module.isStateFlowEnabled());
            moduleSetting.add(setting);

            context.put(FacilioConstants.ContextNames.MODULE_SETTING, moduleSetting);
        }
        return false;
    }
}
