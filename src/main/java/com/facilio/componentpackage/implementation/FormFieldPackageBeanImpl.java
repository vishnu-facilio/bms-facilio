package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class FormFieldPackageBeanImpl implements PackageBean<FormField> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getFormFieldIdVsFormId();
    }

    @Override
    public Map<Long, FormField> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, FormField> formFieldIdVsFormField = new HashMap<>();
        List<FacilioField> fields = FieldFactory.getFormFieldsFields();
        FacilioModule formFieldsModule = ModuleFactory.getFormFieldsModule();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);

        List<Long> idsSubList;
        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(formFieldsModule.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(idsSubList, formFieldsModule));

            List<Map<String, Object>> propsList = selectRecordBuilder.get();

            if (CollectionUtils.isNotEmpty(propsList)) {
                List<FormField> formFieldList = FieldUtil.getAsBeanListFromMapList(propsList, FormField.class);
                formFieldList.forEach(formField -> formFieldIdVsFormField.put(formField.getId(), formField));

            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());

        }
        return formFieldIdVsFormField;
    }

    @Override
    public void convertToXMLComponent(FormField component, XMLBuilder formFieldElement) throws Exception {
        FacilioForm form = PackageBeanUtil.getFormFromId(component.getFormId());

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(form.getModuleId());

        formFieldElement.element(PackageConstants.MODULENAME).text(module.getName());
        formFieldElement.element(PackageConstants.DISPLAY_NAME).text(component.getDisplayName());
        formFieldElement.element(PackageConstants.FormXMLComponents.FORM_NAME).text(form.getName());
        formFieldElement.element(PackageConstants.FormXMLComponents.CONFIG).text(component.getConfigStr());
        formFieldElement.element(PackageConstants.FormXMLComponents.FORM_FIELD_NAME).text(component.getName());
        formFieldElement.element(PackageConstants.FormXMLComponents.SPAN).text(String.valueOf(component.getSpan()));
        formFieldElement.element(PackageConstants.FormXMLComponents.DISPLAY_TYPE).text(component.getDisplayTypeEnum().name());
        formFieldElement.element(PackageConstants.FormXMLComponents.DEFAULT_VALUE).text(component.getValue() != null ? String.valueOf(component.getValue()) : null);
        formFieldElement.element(PackageConstants.SEQUENCE_NUMBER).text(String.valueOf(component.getSequenceNumber()));
        formFieldElement.element(PackageConstants.FormXMLComponents.REQUIRED).text(String.valueOf(component.getRequired()));
        formFieldElement.element(PackageConstants.FormXMLComponents.HIDE_FIELD).text(String.valueOf(component.getHideField()));
        formFieldElement.element(PackageConstants.FormXMLComponents.IS_DISABLED).text(String.valueOf(component.getIsDisabled()));
        formFieldElement.element(PackageConstants.FormXMLComponents.ALLOW_CREATE_OPTIONS).text(String.valueOf(component.getAllowCreate()));

        if (component.getSectionId() > 0) {
            FormSection section = PackageBeanUtil.getSectionFromId(component.getSectionId());
            formFieldElement.element(PackageConstants.FormXMLComponents.SECTION_NAME).text(section.getName());
        }

        if (component.getFieldId() > 0) {
            FacilioField facilioField = moduleBean.getField(component.getFieldId());
            formFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(facilioField.getName());
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FormField formField;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder formFieldElement = idVsData.getValue();
            formField = constructFormFieldFromBuilder(formFieldElement, moduleBean);

            if (formField == null) {
                continue;
            }

            long formFieldId = PackageBeanUtil.getFormFieldId(formField);

            if (formFieldId > 0) {
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), formFieldId);
            }
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FormField formField;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder formFieldElement = idVsData.getValue();
            formField = constructFormFieldFromBuilder(formFieldElement, moduleBean);

            if (formField == null) {
                continue;
            }

            long formFieldId = checkAndAddFormField(formField);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), formFieldId);
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FormField formField;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long formFieldId = idVsData.getKey();
            XMLBuilder formFieldElement = idVsData.getValue();

            formField = constructFormFieldFromBuilder(formFieldElement, moduleBean);
            if (formField == null) {
                continue;
            }
            formField.setId(formFieldId);

            updateFormField(formField);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            FormsAPI.deleteFormFields(ids);
        }
    }

    private Map<Long, Long> getFormFieldIdVsFormId() throws Exception {
        Map<Long, Long> formFieldIdVsFormId = new HashMap<>();
        FacilioModule formFieldsModule = ModuleFactory.getFormFieldsModule();
        List<Long> applicationIds = ApplicationApi.getAllApplicationIds(true);

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(formFieldsModule));
            add(FieldFactory.getNumberField("formId", "FORMID", formFieldsModule));
        }};

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(formFieldsModule.getTableName())
                .select(selectableFields)
                ;

        if (CollectionUtils.isNotEmpty(applicationIds)) {
            Criteria appIdCriteria = PackageBeanUtil.getFormAppIdCriteria(applicationIds);
            selectRecordBuilder.innerJoin(ModuleFactory.getFormModule().getTableName())
                    .on(ModuleFactory.getFormModule().getTableName() + ".ID = " + formFieldsModule.getTableName() + ".FORMID");
            selectRecordBuilder.andCriteria(appIdCriteria);
        }

        List<Map<String, Object>> propsList = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(propsList)) {
            for (Map<String, Object> prop : propsList) {
                long formId = prop.containsKey("formId") ? (Long) prop.get("formId") : -1;
                formFieldIdVsFormId.put((Long) prop.get("id"), formId);
            }
        }
        return formFieldIdVsFormId;
    }

    private FormField constructFormFieldFromBuilder(XMLBuilder formFieldElement, ModuleBean moduleBean) throws Exception {
        String formFieldName, displayName, formName, sectionName, moduleName, facilioFieldName, displayTypeStr, defaultValue, config;
        boolean requiredBool, hideField, isDisabled, allowCreate;
        long formId, moduleId, sectionId, fieldId = -1;
        FacilioField.FieldDisplayType displayType;
        int sequenceNumber, span;

        moduleName = formFieldElement.getElement(PackageConstants.MODULENAME).getText();
        displayName = formFieldElement.getElement(PackageConstants.DISPLAY_NAME).getText();
        config = formFieldElement.getElement(PackageConstants.FormXMLComponents.CONFIG).getText();
        formName = formFieldElement.getElement(PackageConstants.FormXMLComponents.FORM_NAME).getText();
        defaultValue = formFieldElement.getElement(PackageConstants.FormXMLComponents.DEFAULT_VALUE).getText();
        span = Integer.parseInt(formFieldElement.getElement(PackageConstants.FormXMLComponents.SPAN).getText());
        displayTypeStr = formFieldElement.getElement(PackageConstants.FormXMLComponents.DISPLAY_TYPE).getText();
        formFieldName = formFieldElement.getElement(PackageConstants.FormXMLComponents.FORM_FIELD_NAME).getText();
        sequenceNumber = Integer.parseInt(formFieldElement.getElement(PackageConstants.SEQUENCE_NUMBER).getText());
        requiredBool = Boolean.parseBoolean(formFieldElement.getElement(PackageConstants.FormXMLComponents.REQUIRED).getText());
        hideField = Boolean.parseBoolean(formFieldElement.getElement(PackageConstants.FormXMLComponents.HIDE_FIELD).getText());
        isDisabled = Boolean.parseBoolean(formFieldElement.getElement(PackageConstants.FormXMLComponents.IS_DISABLED).getText());
        allowCreate = Boolean.parseBoolean(formFieldElement.getElement(PackageConstants.FormXMLComponents.ALLOW_CREATE_OPTIONS).getText());

        displayType = StringUtils.isNotEmpty(displayTypeStr) ? FacilioField.FieldDisplayType.valueOf(displayTypeStr) : null;
        FacilioModule currModule = moduleBean.getModule(moduleName);
        moduleId = currModule.getModuleId();

        formId = PackageBeanUtil.getFormIdFromName(formName, moduleId);

        if (formId < 0) {
            LOGGER.info("###Sandbox - Form not found - ModuleName - " + moduleName + " FormName - " + formName);
            return null;
        }

        XMLBuilder sectionElement = formFieldElement.getElement(PackageConstants.FormXMLComponents.SECTION_NAME);
        sectionName = sectionElement != null ? sectionElement.getText() : null;
        sectionId = StringUtils.isNotEmpty(sectionName) ? PackageBeanUtil.getSectionIdFromName(formId, sectionName) : -1;

        XMLBuilder facilioFieldElement = formFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME);
        facilioFieldName = facilioFieldElement != null ? facilioFieldElement.getText() : null;
        if (StringUtils.isNotEmpty(facilioFieldName)) {
            FacilioField field = moduleBean.getField(facilioFieldName, moduleName);
            if (field != null) {
                fieldId = field.getFieldId();
            } else {
                LOGGER.info("###Sandbox - Field not found - ModuleName - " + moduleName + " FieldName - " + facilioFieldName);
                return null;
            }
        }

        if(fieldId == -1) {
            fieldId = -99;
        }
        FormField formField = new FormField(fieldId, formFieldName, displayType, displayName, null, sequenceNumber, span, hideField);
        formField.setConfig(FacilioUtil.parseJson(config));
        formField.setAllowCreate(allowCreate);
        formField.setRequired(requiredBool);
        formField.setIsDisabled(isDisabled);
        formField.setSectionId(sectionId);
        formField.setValue(defaultValue);
        formField.setSectionId(sectionId);
        formField.setFormId(formId);

        return formField;
    }

    private long checkAndAddFormField(FormField formField) throws Exception {
        long formFieldId = PackageBeanUtil.getFormFieldId(formField);

        if (formFieldId < 0) {
            formFieldId = addFormField(formField);
        } else {
            formField.setId(formFieldId);
            updateFormField(formField);
        }

        return formFieldId;
    }

    private long addFormField(FormField formField) throws Exception {
        FacilioModule module = ModuleFactory.getFormFieldsModule();
        Map<String, Object> props = FieldUtil.getAsProperties(formField);

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getFormFieldsFields());

        return builder.insert(props);
    }

    private void updateFormField(FormField formField) throws Exception {
        FacilioModule module = ModuleFactory.getFormFieldsModule();
        Map<String, Object> props = FieldUtil.getAsProperties(formField);

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getFormFieldsFields())
                .andCondition(CriteriaAPI.getIdCondition(formField.getId(), module));

        builder.update(props);
    }
}
