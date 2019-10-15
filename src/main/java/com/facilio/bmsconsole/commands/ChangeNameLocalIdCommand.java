package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Change from Name field to LocalID field alone.
 *
 * It wouldn't update existing field to new configuration
 */
public class ChangeNameLocalIdCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String changeTo = (String) context.get(FacilioConstants.ContextNames.MODULE_CHANGE_TO);
        FacilioField field = (FacilioField) context.get(FacilioConstants.ContextNames.MODULE_FIELD);
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            List<FacilioForm> formList = FormsAPI.getDBFormList(module.getName(), null, null, null, true);
            FacilioForm defaultForm = null;

            validateFieldChange(module, formList);

            if (formList != null) {
                defaultForm = formList.get(0);
            }

            if (field == null) {
                field = new FacilioField();
            }
            field.setMainField(true);
            field.setDefault(true);
            field.setModule(module);

            FacilioField nameField = modBean.getField("name", moduleName);
            FacilioField localIdField = modBean.getField("localId", moduleName);

            FormField formField = null;
            if (changeTo.equals("name")) {
                if (nameField == null) {
                    field.setName("name");
                    field.setDisplayName("Name");
                    field.setColumnName("NAME");
                    field.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
                    field.setDataType(FieldType.STRING);

                    modBean.addField(field);
                    if (localIdField != null) {
                        formField = deleteFormField(defaultForm, localIdField);
                        modBean.deleteField(localIdField.getFieldId());

                        FacilioModule moduleLocalIdModule = ModuleFactory.getModuleLocalIdModule();
                        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                                .table(moduleLocalIdModule.getTableName())
                                .andCondition(CriteriaAPI.getCondition("MODULE_NAME", "moduleName", moduleName, StringOperators.IS));
                        deleteBuilder.delete();
                    }
                }
            }
            else {
                if (localIdField == null) {
                    field.setName("localId");
                    field.setDisplayName("Local ID");
                    field.setColumnName("LOCAL_ID");
                    field.setDataType(FieldType.COUNTER);
                    field.setDisplayType(FacilioField.FieldDisplayType.NUMBER);

                    modBean.addField(field);
                    if (nameField != null) {
                        formField = deleteFormField(defaultForm, nameField);
                        modBean.deleteField(nameField.getFieldId());

                        FacilioModule moduleLocalIdModule = ModuleFactory.getModuleLocalIdModule();
                        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                                .table(moduleLocalIdModule.getTableName())
                                .fields(FieldFactory.getModuleLocalIdFields());
                        Map<String, Object> map = new HashMap<>();
                        map.put("localId", 0);
                        map.put("moduleName", moduleName);
                        insertBuilder.insert(map);
                    }
                }
            }

            if (defaultForm != null && formField != null) {
                formField.setFieldId(field.getFieldId());
                formField.setField(field);
                formField.setName(field.getName());
                formField.setDisplayName(field.getDisplayName());

                FacilioModule formFieldsModule = ModuleFactory.getFormFieldsModule();
                GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                        .table(formFieldsModule.getTableName())
                        .fields(FieldFactory.getFormFieldsFields())
                        ;
                Map<String, Object> prop = FieldUtil.getAsProperties(formField);
                insertRecordBuilder.insert(prop);
                if (formField != null) {
                    formField.setField(field);
                    formField.setFieldId(field.getFieldId());
                    FormsAPI.updateFormFields(Collections.singletonList(formField), Collections.singletonList("fieldId"));
                }
            }
        }
        return false;
    }

    private void validateFieldChange(FacilioModule module, List<FacilioForm> formList) throws Exception {
        if (module.getTypeEnum() != FacilioModule.ModuleType.CUSTOM) {
            throw new IllegalArgumentException("Cannot change system modules");
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<CustomModuleData> builder = new SelectRecordsBuilder<CustomModuleData>()
                .module(module)
                .select(modBean.getAllFields(module.getName()))
                .beanClass(CustomModuleData.class)
                ;
        CustomModuleData data = builder.fetchFirst();
        if (data != null) {
            throw new IllegalArgumentException("Cannot change field as it has data already");
        }

        if (CollectionUtils.isNotEmpty(formList) && formList.size() > 1) {
            throw new IllegalArgumentException("Cannot change field");
        }
    }

    private FormField deleteFormField(FacilioForm defaultForm, FacilioField toDeleteField) throws Exception {
        if (defaultForm == null) {
            return null;
        }
        List<FormField> fields = defaultForm.getFields();
        FormField formField = null;
        for (FormField f : fields) {
            if (f.getFieldId() == toDeleteField.getFieldId()) {
                formField = f;
                FacilioModule module = ModuleFactory.getFormFieldsModule();
                Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormFieldsFields());
                GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
                        .table(module.getTableName())
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), String.valueOf(defaultForm.getId()), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), String.valueOf(formField.getFieldId()), NumberOperators.EQUALS))
                        ;
                delete.delete();

                break;
            }
        }
        return formField;
    }
}
