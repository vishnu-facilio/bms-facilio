package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

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
//        FacilioField field = (FacilioField) context.get(FacilioConstants.ContextNames.MODULE_FIELD);
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            List<FacilioForm> formList = FormsAPI.getDBFormList(module.getName(), (List<Integer>) null, null, null, true, false, true);
            FacilioForm defaultForm = null;

            validateFieldChange(module, formList);

            if (formList != null) {
                defaultForm = formList.get(0);
            }

//            if (field == null) {
//                field = new FacilioField();
//            }
//            field.setMainField(true);
//            field.setDefault(true);
//            field.setModule(module);

            if (StringUtils.isEmpty(changeTo)) {
                throw new IllegalArgumentException("Change to value is mandatory");
            }

            FacilioField existingField;
            if (changeTo.equals("name")) {
                existingField = modBean.getField("localId", moduleName);
            }
            else {
                existingField = modBean.getField("name", moduleName);
            }

            if (existingField == null) {
                // don't do anything
                context.put(FacilioConstants.ContextNames.FIELD, modBean.getField(changeTo, moduleName));
                return false;
            }

            changeValue(existingField, changeTo);
            if (changeTo.equals("name")) {
                FacilioModule moduleLocalIdModule = ModuleFactory.getModuleLocalIdModule();
                GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                        .table(moduleLocalIdModule.getTableName())
                        .andCondition(CriteriaAPI.getCondition("MODULE_NAME", "moduleName", moduleName, StringOperators.IS));
                deleteBuilder.delete();
            }
            else {
                FacilioModule moduleLocalIdModule = ModuleFactory.getModuleLocalIdModule();
                GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                        .table(moduleLocalIdModule.getTableName())
                        .fields(FieldFactory.getModuleLocalIdFields());
                Map<String, Object> map = new HashMap<>();
                map.put("localId", 0);
                map.put("moduleName", moduleName);
                insertBuilder.insert(map);
            }

            updateField(existingField);

            context.put(FacilioConstants.ContextNames.FIELD, existingField);
        }
        return false;
    }

    private void updateField(FacilioField existingField) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getAddFieldFields());
        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(fieldMap.get("name"));
        selectFields.add(fieldMap.get("displayName"));
        selectFields.add(fieldMap.get("columnName"));
        selectFields.add(fieldMap.get("displayTypeInt"));
        selectFields.add(fieldMap.get("dataType"));
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getFieldsModule().getTableName())
                .fields(selectFields)
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(existingField.getFieldId()), NumberOperators.EQUALS));
        Map<String, Object> props = FieldUtil.getAsProperties(existingField);
        updateRecordBuilder.update(props);

        props.put("fieldId", existingField.getFieldId());
        if (existingField.getName().equals("localId")) {
            // add entry in Number Fields
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getNumberFieldModule().getTableName())
                    .fields(FieldFactory.getNumberFieldFields())
                    .addRecord(props);

            insertBuilder.save();
        }
        else {
            // remove from Number Fields
            GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getNumberFieldModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(existingField.getFieldId()), NumberOperators.EQUALS));
            deleteBuilder.delete();
        }
    }

    private void changeValue(FacilioField existingField, String changeTo) {
        if (changeTo.equals("name")) {
            existingField.setName("name");
            existingField.setDisplayName("Name");
            existingField.setColumnName("NAME");
            existingField.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
            existingField.setDataType(FieldType.STRING);
        }
        else {
            existingField.setName("localId");
            existingField.setDisplayName("Local ID");
            existingField.setColumnName("LOCAL_ID");
            existingField.setDataType(FieldType.COUNTER);
            existingField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        }
    }

    private void validateFieldChange(FacilioModule module, List<FacilioForm> formList) throws Exception {
        if (!module.isCustom()) {
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
