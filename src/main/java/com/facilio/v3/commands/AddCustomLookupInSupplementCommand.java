package com.facilio.v3.commands;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

@AllArgsConstructor
public class AddCustomLookupInSupplementCommand extends FacilioCommand {

    private boolean isSummary;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isV4 = Constants.isV4(context);
        if (isV4) {
            return false;
        }
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Boolean isSubFormRecord = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_SUB_FORM_RECORD,false);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        List<SupplementRecord> customLookupFields = new ArrayList<>();
        for (FacilioField f : allFields) {
            if (
                    // We are adding all custom lookup/ multi enum custom fields for list/ summary. This needs to be changed later to get from Column factory or somethnig
                    (!f.isDefault() &&
                        (f.getDataTypeEnum() == FieldType.LOOKUP || f.getDataTypeEnum() == FieldType.MULTI_LOOKUP || f.getDataTypeEnum() == FieldType.MULTI_ENUM)
                    )
                    || ((isSubFormRecord || isSummary) && f.getDataTypeEnum().isRelRecordField()) // Adding multi record fields for summary alone. Custom or otherwise
                ) {
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
