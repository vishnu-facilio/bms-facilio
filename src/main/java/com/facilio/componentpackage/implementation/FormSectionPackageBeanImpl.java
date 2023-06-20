package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FormSectionPackageBeanImpl implements PackageBean<FormSection> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getSectionIdVsFormId();
    }

    @Override
    public Map<Long, FormSection> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, FormSection> formSectionIdVsSection = new HashMap<>();
        List<FacilioField> fields = FieldFactory.getFormSectionFields();
        FacilioModule sectionModule = ModuleFactory.getFormSectionModule();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);

        List<Long> idsSubList;
        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(sectionModule.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(idsSubList, sectionModule));

            List<Map<String, Object>> propsList = selectRecordBuilder.get();

            if (CollectionUtils.isNotEmpty(propsList)) {
                List<FormSection> formSectionList = FieldUtil.getAsBeanListFromMapList(propsList, FormSection.class);
                formSectionList.forEach(section -> formSectionIdVsSection.put(section.getId(), section));

            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());

        }
        return formSectionIdVsSection;
    }

    @Override
    public void convertToXMLComponent(FormSection component, XMLBuilder sectionElement) throws Exception {
        FacilioForm form = PackageBeanUtil.getFormFromId(component.getFormId());

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(form.getModuleId());

        sectionElement.element(PackageConstants.MODULENAME).text(module.getName());
        sectionElement.element(PackageConstants.FormXMLComponents.FORM_NAME).text(form.getName());
        sectionElement.element(PackageConstants.FormXMLComponents.SECTION_NAME).text(component.getName());
        sectionElement.element(PackageConstants.SEQUENCE_NUMBER).text(String.valueOf(component.getSequenceNumber()));
        sectionElement.element(PackageConstants.FormXMLComponents.SECTION_TYPE).text(component.getSectionTypeEnum() != null ? component.getSectionTypeEnum().name() : null);
        sectionElement.element(PackageConstants.FormXMLComponents.SUB_FORM_DEFAULT_VALUE).text(component.getSubFormValueStr());
        sectionElement.element(PackageConstants.FormXMLComponents.SHOW_LABEL).text(String.valueOf(component.getShowLabel()));
        sectionElement.element(PackageConstants.FormXMLComponents.NUMBER_OF_SUB_FORM_RECORDS).text(String.valueOf(component.getNumberOfSubFormRecords()));

        if (component.getSubFormId() > 0) {
            FacilioForm subForm = PackageBeanUtil.getFormFromId(component.getSubFormId());
            FacilioModule subFormModule = moduleBean.getModule(subForm.getModuleId());
            FacilioField lookupField = moduleBean.getField(component.getLookupFieldId());
            XMLBuilder subFormElement = sectionElement.element(PackageConstants.FormXMLComponents.SUB_FORM);
            subFormElement.element(PackageConstants.FormXMLComponents.SUB_FORM_NAME).text(subForm.getName());
            subFormElement.element(PackageConstants.FormXMLComponents.LOOKUP_FIELD_NAME).text(lookupField.getName());
            subFormElement.element(PackageConstants.FormXMLComponents.LOOKUP_MODULE_NAME).text(subFormModule.getName());
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
        FormSection formSection;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder formElement = idVsData.getValue();
            formSection = constructSectionFromBuilder(formElement, moduleBean);

            long sectionId = PackageBeanUtil.getSectionIdFromName(formSection.getFormId(), formSection.getName());

            if (sectionId > 0) {
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), sectionId);
            }
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FormSection formSection;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder formElement = idVsData.getValue();
            formSection = constructSectionFromBuilder(formElement, moduleBean);

            long sectionId = checkAndAddSection(formSection);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), sectionId);
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FormSection formSection;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long sectionId = idVsData.getKey();
            XMLBuilder formElement = idVsData.getValue();

            formSection = constructSectionFromBuilder(formElement, moduleBean);
            formSection.setId(sectionId);

            updateFormSection(formSection);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            FormsAPI.deleteFormSections(ids);
        }
    }

    private Map<Long, Long> getSectionIdVsFormId() throws Exception {
        Map<Long, Long> sectionIdVsFormId = new HashMap<>();
        FacilioModule sectionModule = ModuleFactory.getFormSectionModule();

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getNumberField("formId", "FORMID", sectionModule));
            add(FieldFactory.getIdField(sectionModule));
        }};

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(sectionModule.getTableName())
                .select(selectableFields)
                ;

        List<Map<String, Object>> propsList = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(propsList)) {
            for (Map<String, Object> prop : propsList) {
                long formId = prop.containsKey("formId") ? (Long) prop.get("formId") : -1;
                sectionIdVsFormId.put((Long) prop.get("id"), formId);
            }
        }
        return sectionIdVsFormId;
    }

    private FormSection constructSectionFromBuilder(XMLBuilder sectionElement, ModuleBean moduleBean) throws Exception {
        String moduleName, sectionName, formName, sectionTypeStr, subFormDefaultValue;
        long formId, moduleId, lookupFieldId = -1, subFormId = -1;
        int sequenceNumber, noOfSubFormRecords;
        FormSection.SectionType sectionType;
        boolean showLabel;

        moduleName = sectionElement.getElement(PackageConstants.MODULENAME).getText();
        formName = sectionElement.getElement(PackageConstants.FormXMLComponents.FORM_NAME).getText();
        sectionName = sectionElement.getElement(PackageConstants.FormXMLComponents.SECTION_NAME).getText();
        sectionTypeStr = sectionElement.getElement(PackageConstants.FormXMLComponents.SECTION_TYPE).getText();
        sequenceNumber = Integer.parseInt(sectionElement.getElement(PackageConstants.SEQUENCE_NUMBER).getText());
        subFormDefaultValue = sectionElement.getElement(PackageConstants.FormXMLComponents.SUB_FORM_DEFAULT_VALUE).getText();
        showLabel = Boolean.parseBoolean(sectionElement.getElement(PackageConstants.FormXMLComponents.SHOW_LABEL).getText());
        noOfSubFormRecords = Integer.parseInt(sectionElement.getElement(PackageConstants.FormXMLComponents.NUMBER_OF_SUB_FORM_RECORDS).getText());

        sectionType = StringUtils.isNotEmpty(sectionTypeStr) ? FormSection.SectionType.valueOf(sectionTypeStr) : null;
        FacilioModule currModule = moduleBean.getModule(moduleName);
        moduleId = currModule.getModuleId();

        formId = PackageBeanUtil.getFormIdFromName(formName, moduleId);

        XMLBuilder subFormElement = sectionElement.getElement(PackageConstants.FormXMLComponents.SUB_FORM);
        if (subFormElement != null) {
            String subFormName, lookupFieldName, lookupModuleName;
            subFormName = subFormElement.getElement(PackageConstants.FormXMLComponents.SUB_FORM_NAME).getText();
            lookupFieldName = subFormElement.getElement(PackageConstants.FormXMLComponents.LOOKUP_FIELD_NAME).getText();
            lookupModuleName = subFormElement.getElement(PackageConstants.FormXMLComponents.LOOKUP_MODULE_NAME).getText();

            FacilioModule lookupModule = moduleBean.getModule(lookupModuleName);
            FacilioField lookupField = moduleBean.getField(lookupFieldName, lookupModuleName);

            lookupFieldId = lookupField.getFieldId();
            subFormId = PackageBeanUtil.getFormIdFromName(subFormName, lookupModule.getModuleId());
        }

        FormSection formSection = new FormSection(sectionName, formId, sectionType, sequenceNumber, subFormDefaultValue, showLabel, noOfSubFormRecords);
        formSection.setLookupFieldId(lookupFieldId);
        formSection.setSubFormId(subFormId);

        return formSection;
    }

    private long checkAndAddSection(FormSection section) throws Exception {
        long formId = section.getFormId();
        String sectionName = section.getName();
        long sectionId = PackageBeanUtil.getSectionIdFromName(formId, sectionName);

        if (sectionId < 0) {
            sectionId = addFormSection(section);
        } else {
            section.setId(sectionId);
            updateFormSection(section);
        }

        return sectionId;
    }

    private long addFormSection(FormSection section) throws Exception {
        FacilioModule module = ModuleFactory.getFormSectionModule();
        Map<String, Object> props = FieldUtil.getAsProperties(section);

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getFormSectionFields());

        return builder.insert(props);
    }

    private void updateFormSection(FormSection section) throws Exception {
        FacilioModule module = ModuleFactory.getFormSectionModule();
        Map<String, Object> props = FieldUtil.getAsProperties(section);

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getFormSectionFields())
                .andCondition(CriteriaAPI.getIdCondition(section.getId(), module));

        builder.update(props);
    }
}
