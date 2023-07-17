package com.facilio.fields.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.util.FieldConfigUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FilterFieldsByPermissionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long moduleId = (Long) context.get(FacilioConstants.ContextNames.MODULE_ID);
        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if((moduleId == null || moduleId <= 0)) {
            if(module != null) {
                moduleId = module.getModuleId();
            }else {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                module = modBean.getModule(moduleName);
                moduleId = module != null ? module.getModuleId() : -1;
            }
        }

        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);
        if(moduleId > 0 && CollectionUtils.isNotEmpty(fields)) {
            fields = FieldConfigUtil.filterFieldsByPermission(moduleId, fields);
        }

        context.put(FacilioConstants.ContextNames.FIELDS, fields);
        return false;
    }
}
