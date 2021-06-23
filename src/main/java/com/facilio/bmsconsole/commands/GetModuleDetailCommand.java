package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class GetModuleDetailCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            List<FacilioField> customFields = allFields.stream().filter(f -> f.getDefault() != null && f.getDefault() == false).collect(Collectors.toList());

            context.put(FacilioConstants.ContextNames.MODULE, module);
            context.put(FacilioConstants.ContextNames.MODULE_FIELD_COUNT, customFields.size());
        }
        return false;
    }
}
