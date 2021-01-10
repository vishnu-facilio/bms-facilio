package com.facilio.v3.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

public class AddCustomLookupInSupplementCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        List<SupplementRecord> customLookupFields = new ArrayList<>();
        for (FacilioField f : allFields) {
            if (!f.isDefault() && (f.getDataTypeEnum() == FieldType.LOOKUP || f.getDataTypeEnum() == FieldType.MULTI_LOOKUP || f.getDataTypeEnum() == FieldType.MULTI_ENUM)) {
                customLookupFields.add((SupplementRecord) f);
            }
        }

        if (CollectionUtils.isNotEmpty(customLookupFields)) {
            List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
            if (supplementFields == null) {
                supplementFields = new ArrayList<>();
                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementFields);
            }
            supplementFields.addAll(customLookupFields);
        }

        return false;
    }
}
