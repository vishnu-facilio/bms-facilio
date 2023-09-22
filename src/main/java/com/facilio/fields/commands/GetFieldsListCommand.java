package com.facilio.fields.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GetFieldsListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (module == null && StringUtils.isNotEmpty(moduleName)) {
            module = modBean.getModule(moduleName);
        }
        Objects.requireNonNull(module, "Module can't be null for fetching fields");

        List<FacilioField> fields = modBean.getAllFields(moduleName);

        context.put(FacilioConstants.ContextNames.FIELDS, fields);
        context.put(FacilioConstants.ContextNames.MODULE, module);

        return false;
    }
}
