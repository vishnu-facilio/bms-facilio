package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetFieldsByAccessType extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long accessType = (Long) context.get(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE);
        Long moduleId = (Long) context.get(FacilioConstants.ContextNames.MODULE_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (moduleId != null && moduleId > 0) {
            FacilioModule module = modBean.getModule(moduleId);
            moduleName = module.getName();
        }

        if (moduleName == null || moduleName.isEmpty()) {
            throw new IllegalArgumentException("Module is mandatory");
        }

        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        if (accessType == null || accessType <= 0) {
            throw new IllegalArgumentException("Invalid access type.");
        }
        
        List<FacilioField> selectedFields = new ArrayList<>();
        for (FacilioField field: allFields) {
            long fieldAccessType = field.getAccessType();
            if (fieldAccessType < 0) {
                fieldAccessType = 0;
            }

            if ((fieldAccessType & accessType) == accessType) {
                selectedFields.add(field);
            }
        }

        context.put(FacilioConstants.ContextNames.FIELDS, selectedFields);
        return false;
    }
}
