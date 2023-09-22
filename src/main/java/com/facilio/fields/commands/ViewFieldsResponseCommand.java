package com.facilio.fields.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.context.ModuleViewField;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ViewFieldsResponseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        List<FacilioField> fieldsList = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);
        fieldsList = CollectionUtils.isNotEmpty(fieldsList) ? fieldsList : new ArrayList<>();

        Map<String, List<String>> typeSpecificFieldsMap = (Map<String, List<String>>) context.get(FacilioConstants.FieldsConfig.TYPE_SPECIFIC_FIELDS_MAP);

        // siteId Field
        if (Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER, FacilioConstants.ContextNames.TENANT, FacilioConstants.ContextNames.ASSET, FacilioConstants.ContextNames.SERVICE_REQUEST, FacilioConstants.ContextNames.WorkPermit.WORKPERMIT).contains(module.getName())) {
            fieldsList.add(FieldFactory.getSiteIdField());
        }

        List<String> fixedFieldNames = null, fixedSelectableFieldNames = null;
        if(typeSpecificFieldsMap != null) {
            fixedFieldNames = typeSpecificFieldsMap.get(FacilioConstants.FieldsConfig.FIXED_FIELD_NAMES);
            fixedSelectableFieldNames = typeSpecificFieldsMap.get(FacilioConstants.FieldsConfig.FIXED_SELECTABLE_FIELD_NAMES);
        }

        // localId Field
        if (ModuleLocalIdUtil.MODULES_WITH_LOCAL_ID.contains(moduleName)) {
            fieldsList.add(FieldFactory.getNumberField("localId", null, module));
        }

        List<ModuleViewField> viewFieldList = new ArrayList<>();
        for (FacilioField field : fieldsList) {
            ModuleViewField viewField = new ModuleViewField(field);
            if (CollectionUtils.isNotEmpty(fixedFieldNames) && fixedFieldNames.contains(field.getName())) {
                viewField.setFixedColumn(true);
            } else if (CollectionUtils.isNotEmpty(fixedSelectableFieldNames) && fixedSelectableFieldNames.contains(field.getName())) {
                viewField.setFixedSelectableColumn(true);
            }
            viewFieldList.add(viewField);
        }

        context.put(FacilioConstants.ContextNames.FIELDS, viewFieldList);

        return false;
    }

}
