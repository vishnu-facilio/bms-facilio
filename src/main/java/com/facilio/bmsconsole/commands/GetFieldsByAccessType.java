package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;

public class GetFieldsByAccessType extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long accessType = (Long) context.get(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE);
        Long moduleId = (Long) context.get(FacilioConstants.ContextNames.MODULE_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = null;
        if (moduleId != null && moduleId > 0) {
            module = modBean.getModule(moduleId);
        }
        else {
            FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "Invalid module name/ID while fetching fields");
            module = modBean.getModule(moduleName);
        }
        FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module name/ID while fetching fields");
        moduleName = module.getName();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        List<FacilioField> selectedFields = FieldUtil.getFieldsByAccessType(accessType, moduleName);

        // TODO Should be removed when all system fields are migrated
//        if (ChainUtil.getV3Config(moduleName) == null) {
        if (FieldUtil.isSiteIdFieldPresent(module, true) && selectedFields.stream().noneMatch(field -> field.getName().equals("siteId"))) {
            selectedFields.add(FieldFactory.getSiteIdField(module));
        }
//        }

        context.put(FacilioConstants.ContextNames.FIELDS, selectedFields);
        return false;
    }

}
