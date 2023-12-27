package com.facilio.fields.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.module.GetSortableFieldsCommand;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.context.SortableField;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortableFieldsResponseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        List<FacilioField> fieldsList = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);
        fieldsList = CollectionUtils.isNotEmpty(fieldsList) ? fieldsList : new ArrayList<>();
        addIdField(module, fieldsList); // add id field
        removeNonPickListLookupFields(fieldsList); // filter lookup Fields other than that of moduleType - PICK_LIST

        List<SortableField> sortableFields = fieldsList.stream().map(SortableField::new).collect(Collectors.toList());

        sortableFields.sort(Comparator.comparing(SortableField::isMainField).reversed()
                .thenComparing(SortableField::getDisplayName));

        context.put(FacilioConstants.ContextNames.FIELDS, sortableFields);

        return false;
    }

    private static void addIdField(FacilioModule module, List<FacilioField> fields) {
        FacilioField idField = FieldFactory.getIdField(module);
        idField.setDisplayName(FieldUtil.getRecordIdFieldName(module));
        fields.add(idField);
    }
    private static void removeNonPickListLookupFields(List<FacilioField> fields) {
        if (CollectionUtils.isNotEmpty(fields)) {
            boolean isNMDP = (AccountUtil.getCurrentOrg().getOrgId() == 429 && AccountUtil.getCurrentOrg().getDomain().equals("nmdp"))
                    || (AccountUtil.getCurrentOrg().getOrgId() == 1544 && AccountUtil.getCurrentOrg().getDomain().equals("nmdpsandbox2"));

            if (isNMDP) {
                fields.removeIf(field -> field.getDataTypeEnum() == FieldType.LOOKUP &&
                        (((LookupField) field).getLookupModule().getTypeEnum() != FacilioModule.ModuleType.PICK_LIST) &&
                        !((LookupField) field).getLookupModule().getName().equals(FacilioConstants.ContextNames.USERS));
            } else {
                fields.removeIf(field -> field.getDataTypeEnum() == FieldType.LOOKUP &&
                        ((LookupField) field).getLookupModule().getTypeEnum() != FacilioModule.ModuleType.PICK_LIST);
            }
        }
    }
}
