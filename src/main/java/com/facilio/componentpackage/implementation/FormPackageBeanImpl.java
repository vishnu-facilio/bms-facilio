package com.facilio.componentpackage.implementation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.bmsconsole.context.ApplicationContext;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.ModuleFactory;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.v3.context.Constants;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.FieldUtil;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class FormPackageBeanImpl implements PackageBean<FacilioForm> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getFormIdVsModuleId(true);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getFormIdVsModuleId(false);
    }

    @Override
    public Map<Long, FacilioForm> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, FacilioForm> formIdVsForm = new HashMap<>();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);

        List<Long> idsSubList;
        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);

            Criteria formIdCriteria = new Criteria();
            formIdCriteria.addAndCondition(CriteriaAPI.getIdCondition(idsSubList, ModuleFactory.getFormModule()));
            List<FacilioForm> dbFormList = FormsAPI.getDBFormList(null, formIdCriteria, null, false, false,true, -1L, true);
            Map<Long, SharingContext<SingleSharingContext>> sharingMap = SharingAPI.getSharing(idsSubList, ModuleFactory.getFormSharingModule(),
                    SingleSharingContext.class, FieldFactory.getFormSharingFields());

            dbFormList.forEach(form -> formIdVsForm.put(form.getId(), form));
            if (MapUtils.isNotEmpty(sharingMap)) {
                sharingMap.keySet().forEach(formId -> formIdVsForm.get(formId).setFormSharing(sharingMap.get(formId)));
            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());
        }

        return formIdVsForm;
    }

    @Override
    public void convertToXMLComponent(FacilioForm component, XMLBuilder formElement) throws Exception {
        // TODO - Handle STATE_FLOW_ID, SITE_ID (Form - Site Relation), FORM_TYPE
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule currModule = moduleBean.getModule(component.getModuleId());

        formElement.element(PackageConstants.MODULENAME).text(currModule.getName());
        formElement.element(PackageConstants.DESCRIPTION).text(component.getDescription());
        formElement.element(PackageConstants.DISPLAY_NAME).text(component.getDisplayName());
        formElement.element(PackageConstants.FormXMLComponents.FORM_NAME).text(component.getName());
        formElement.element(PackageConstants.FormXMLComponents.TYPE).text(component.getTypeEnum() != null ? component.getTypeEnum().name() : null);
        if (component.getAppId() > 0) {
            ApplicationContext application = ApplicationApi.getApplicationForId(component.getAppId());
            formElement.element(PackageConstants.AppXMLConstants.APPLICATION_NAME).text(application.getLinkName());
        } else {
            formElement.element(PackageConstants.AppXMLConstants.APPLICATION_NAME).text(null);
        }
        formElement.element(PackageConstants.FormXMLComponents.LABEL_POSITION).text(component.getLabelPositionEnum().name());
        formElement.element(PackageConstants.FormXMLComponents.IS_LOCKED).text(String.valueOf(component.isLocked()));
        formElement.element(PackageConstants.FormXMLComponents.SHOW_IN_WEB).text(String.valueOf(component.getShowInWeb()));
        formElement.element(PackageConstants.FormXMLComponents.HIDE_IN_LIST).text(String.valueOf(component.getHideInList()));
        formElement.element(PackageConstants.FormXMLComponents.PRIMARY_FORM).text(String.valueOf(component.getPrimaryForm()));
        formElement.element(PackageConstants.FormXMLComponents.SHOW_IN_MOBILE).text(String.valueOf(component.getShowInMobile()));
        formElement.element(PackageConstants.FormXMLComponents.IS_SYSTEM_FORM).text(String.valueOf(component.getIsSystemForm()));

        // Form Sharing
        if (CollectionUtils.isNotEmpty(component.getFormSharing())) {
            PackageBeanUtil.constructBuilderFromSharingContext(component.getFormSharing(), formElement.element(PackageConstants.FormXMLComponents.FORM_SHARING));
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        Map<String, Map<String, String>> moduleNameVsFormNameVsDisplayNameMap = new HashMap<>();
        Map<String, List<String>> moduleNameVsFormNamesMap = new HashMap<>();
        Map<String, String> formNameVsErrorMessage = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();

        for (XMLBuilder formElement : components) {
            String moduleName = formElement.getElement(PackageConstants.MODULENAME).getText();
            String displayName = formElement.getElement(PackageConstants.DISPLAY_NAME).getText();
            String formName = formElement.getElement(PackageConstants.FormXMLComponents.FORM_NAME).getText();

            if (!moduleNameVsFormNamesMap.containsKey(moduleName)) {
                moduleNameVsFormNamesMap.put(moduleName, new ArrayList<>());
            }
            moduleNameVsFormNamesMap.get(moduleName).add(formName);
            if (!moduleNameVsFormNameVsDisplayNameMap.containsKey(moduleName)) {
                moduleNameVsFormNameVsDisplayNameMap.put(moduleName, new HashMap<>());
            }
            moduleNameVsFormNameVsDisplayNameMap.get(moduleName).put(formName, displayName);
        }

        for (Map.Entry<String, List<String>> entry : moduleNameVsFormNamesMap.entrySet()) {
            String moduleName = entry.getKey();
            List<String> formNames = entry.getValue();
            FacilioModule module = moduleBean.getModule(moduleName);
            Map<String, Long> formIdsFromNames = PackageBeanUtil.getFormIdsFromNames(formNames, module.getModuleId());
            Map<String, String> formNameVsDisplayName = moduleNameVsFormNameVsDisplayNameMap.get(moduleName);

            if (MapUtils.isNotEmpty(formIdsFromNames)) {
                for (String formName : formIdsFromNames.keySet()) {
                    String displayName = formNameVsDisplayName.get(formName);
                    formNameVsErrorMessage.put(displayName + " - " + moduleName, PackageConstants.FormXMLComponents.DUPLICATE_FORM_ERROR);
                }
            }
        }

        return formNameVsErrorMessage;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, List<String>> moduleNameVsFormNamesMap = new HashMap<>();
        Map<String, Map<String, String>> moduleNameVsformNameVsUniqueIdentifierMap = new HashMap<>();
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder formElement = idVsData.getValue();
            String moduleName = formElement.getElement(PackageConstants.MODULENAME).getText();
            String formName = formElement.getElement(PackageConstants.FormXMLComponents.FORM_NAME).getText();
            boolean isSystemForm = Boolean.parseBoolean(formElement.getElement(PackageConstants.FormXMLComponents.IS_SYSTEM_FORM).getText());

            if (isSystemForm) {
                if (!moduleNameVsFormNamesMap.containsKey(moduleName)) {
                    moduleNameVsFormNamesMap.put(moduleName, new ArrayList<>());
                }
                moduleNameVsFormNamesMap.get(moduleName).add(formName);
                if (!moduleNameVsformNameVsUniqueIdentifierMap.containsKey(moduleName)) {
                    moduleNameVsformNameVsUniqueIdentifierMap.put(moduleName, new HashMap<>());
                }
                moduleNameVsformNameVsUniqueIdentifierMap.get(moduleName).put(formName, uniqueIdentifier);
            }
        }

        for (Map.Entry<String, List<String>> entry : moduleNameVsFormNamesMap.entrySet()) {
            String moduleName = entry.getKey();
            List<String> formNames = entry.getValue();
            FacilioModule module = moduleBean.getModule(moduleName);
            Map<String, Long> formIdsFromNames = PackageBeanUtil.getFormIdsFromNames(formNames, module.getModuleId());
            Map<String, String> formNameVsUniqueIdentifier = moduleNameVsformNameVsUniqueIdentifierMap.get(moduleName);

            if (MapUtils.isNotEmpty(formIdsFromNames)) {
                for (String formName : formIdsFromNames.keySet()) {
                    long formId = formIdsFromNames.get(formName);
                    String uniqueIdentifier = formNameVsUniqueIdentifier.get(formName);

                    uniqueIdentifierVsComponentId.put(uniqueIdentifier, formId);
                }
            }
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<Long, Map<String, Long>> moduleIdVsFormNameVsFormId = PackageBeanUtil.getFormDetailsFromDB();
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioForm facilioForm;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder formElement = idVsData.getValue();
            facilioForm = constructFormFromXMLBuilder(formElement, appNameVsAppId, moduleBean);

            String formName = facilioForm.getName();
            long moduleId = facilioForm.getModuleId();
            long formId = (MapUtils.isNotEmpty(moduleIdVsFormNameVsFormId) && moduleIdVsFormNameVsFormId.containsKey(moduleId)) ?
                    moduleIdVsFormNameVsFormId.get(moduleId).getOrDefault(formName, -1L) : -1;

            if (formId < 0) {
                formId = FormsAPI.createForm(facilioForm, facilioForm.getModule());
            } else {
                facilioForm.setId(formId);
                updateForm(facilioForm);
            }

            facilioForm.setId(formId);

            if (CollectionUtils.isNotEmpty(facilioForm.getFormSharing())) {
                addFormSharing(facilioForm);
            }

            uniqueIdentifierVsComponentId.put(idVsData.getKey(), formId);
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioForm facilioForm;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long formId = idVsData.getKey();
            if (formId != null && formId > 0) {
                XMLBuilder formElement = idVsData.getValue();

                facilioForm = constructFormFromXMLBuilder(formElement, appNameVsAppId, moduleBean);
                facilioForm.setId(formId);
                updateForm(facilioForm);

                if (CollectionUtils.isNotEmpty(facilioForm.getFormSharing())) {
                    addFormSharing(facilioForm);
                }
            }
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        List<FormSection> sectionsForFormIds = FormsAPI.getAllSectionsForFormIds(idVsXMLComponents.keySet());
        List<FormField> formFieldsForFormIds = FormsAPI.getAllFormFieldsForFormIds(idVsXMLComponents.keySet());

        Map<String, Long> sectionsUIdVsIdsFromPackage = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.FORM_SECTION);
        Map<String, Long> fieldsUIdVsIdsFromPackage = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.FORM_FIELDS);

        if(PackageUtil.isInstallThread()) {
            if (CollectionUtils.isNotEmpty(sectionsForFormIds) && MapUtils.isNotEmpty(sectionsUIdVsIdsFromPackage)) {
                List<Long> allSectionIds = sectionsForFormIds.stream().map(FormSection::getId).collect(Collectors.toList());
                allSectionIds.removeAll(sectionsUIdVsIdsFromPackage.values());
                if (CollectionUtils.isNotEmpty(allSectionIds)) {
                    FormsAPI.deleteFormSections(allSectionIds);
                }
            }

            if (CollectionUtils.isNotEmpty(formFieldsForFormIds) && MapUtils.isNotEmpty(fieldsUIdVsIdsFromPackage)) {
                List<Long> allFieldsIds = formFieldsForFormIds.stream().map(FormField::getId).collect(Collectors.toList());
                allFieldsIds.removeAll(fieldsUIdVsIdsFromPackage.values());
                if (CollectionUtils.isNotEmpty(allFieldsIds)) {
                    FormsAPI.deleteFormFields(allFieldsIds);
                }
            }
        } else {
            // BulkUpdate Form Sections
            if (CollectionUtils.isNotEmpty(sectionsForFormIds) && MapUtils.isNotEmpty(sectionsUIdVsIdsFromPackage)) {
                List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

                Map<Long, List<FormSection>> formIdVsSections = new HashMap<>();
                for (FormSection section : sectionsForFormIds) {
                    formIdVsSections.computeIfAbsent(section.getFormId(), k -> new ArrayList<>());
                    formIdVsSections.get(section.getFormId()).add(section);
                }

                for (long formId : formIdVsSections.keySet()) {
                    Map<Long, List<FormSection>> sequenceNumberVsFormSections = new HashMap<>();
                    for (FormSection section : formIdVsSections.get(formId)) {
                        sequenceNumberVsFormSections.computeIfAbsent(section.getSequenceNumber(), k -> new ArrayList<>());
                        sequenceNumberVsFormSections.get(section.getSequenceNumber()).add(section);
                    }

                    Map<Long, Double> currFormPropsToUpdate = new HashMap<>();
                    for (Long sequenceNumber : sequenceNumberVsFormSections.keySet()) {
                        for (FormSection formSection : sequenceNumberVsFormSections.get(sequenceNumber)) {
                            long id = formSection.getId();
                            double seqNumber = formSection.getSequenceNumber();
                            seqNumber = sectionsUIdVsIdsFromPackage.containsValue(id) ? seqNumber + 0.000001 : seqNumber;

                            currFormPropsToUpdate.put(id, seqNumber);
                        }
                    }

                    bulkUpdateProps(currFormPropsToUpdate, batchUpdateList, ModuleFactory.getFormSectionModule());
                }
            }

            // BulkUpdate Form Fields
            if (CollectionUtils.isNotEmpty(formFieldsForFormIds) && MapUtils.isNotEmpty(fieldsUIdVsIdsFromPackage)) {
                List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

                Map<Long, List<FormField>> formIdVsFormFIelds = new HashMap<>();
                for (FormField field : formFieldsForFormIds) {
                    formIdVsFormFIelds.computeIfAbsent(field.getFormId(), k -> new ArrayList<>());
                    formIdVsFormFIelds.get(field.getFormId()).add(field);
                }

                for (long formId : formIdVsFormFIelds.keySet()) {
                    Map<Integer, List<FormField>> sequenceNumberVsFormFields = new HashMap<>();
                    for (FormField field : formIdVsFormFIelds.get(formId)) {
                        sequenceNumberVsFormFields.computeIfAbsent(field.getSequenceNumber(), k -> new ArrayList<>());
                        sequenceNumberVsFormFields.get(field.getSequenceNumber()).add(field);
                    }

                    Map<Long, Double> currFormPropsToUpdate = new HashMap<>();
                    for (Integer sequenceNumber : sequenceNumberVsFormFields.keySet()) {
                        for (FormField field : sequenceNumberVsFormFields.get(sequenceNumber)) {
                            long id = field.getId();
                            double seqNumber = field.getSequenceNumber();
                            seqNumber = fieldsUIdVsIdsFromPackage.containsValue(id) ? seqNumber + 0.000001 : seqNumber;

                            currFormPropsToUpdate.put(id, seqNumber);
                        }
                    }

                    bulkUpdateProps(currFormPropsToUpdate, batchUpdateList, ModuleFactory.getFormFieldsModule());
                }
            }
        }
    }

    public static void bulkUpdateProps(Map<Long, Double> currFormPropsToUpdate, List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList, FacilioModule module) throws SQLException {
        Map<Long, Double> sortedMap = currFormPropsToUpdate.entrySet()
                .stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        long number = 1;
        for (Long id : sortedMap.keySet()) {
            GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
            updateVal.addUpdateValue("sequenceNumber", number);
            updateVal.addWhereValue("id", id);
            batchUpdateList.add(updateVal);
            number += 1;
        }

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(Collections.singletonList(FieldFactory.getNumberField("sequenceNumber", "SEQUENCE_NUMBER", module)));

        updateRecordBuilder.batchUpdate(Collections.singletonList(FieldFactory.getIdField(module)), batchUpdateList);
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            FormsAPI.deleteForms(ids);
        }
    }

    private Map<Long, Long> getFormIdVsModuleId (boolean fetchSystem) throws Exception {
        Map<Long, Long> formIdVsModuleId = new HashMap<>();
        FacilioModule formsModule = ModuleFactory.getFormModule();
        List<Long> applicationIds = ApplicationApi.getAllApplicationIds(true);

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getModuleIdField(formsModule));
            add(FieldFactory.getIdField(formsModule));
        }};

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(formsModule.getTableName())
                .select(selectableFields)
                .andCondition(CriteriaAPI.getCondition("IS_SYSTEM_FORM", "isSystemForm", String.valueOf(fetchSystem), BooleanOperators.IS));

        if (CollectionUtils.isNotEmpty(applicationIds)) {
            Criteria appIdCriteria = PackageBeanUtil.getFormAppIdCriteria(applicationIds);
            selectRecordBuilder.andCriteria(appIdCriteria);
        }

        List<Map<String, Object>> propsList = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(propsList)) {
            for (Map<String, Object> prop : propsList) {
                long moduleId = prop.containsKey("moduleId") ? (Long) prop.get("moduleId") : -1;
                formIdVsModuleId.put((Long) prop.get("id"), moduleId);
            }
        }
        return formIdVsModuleId;
    }

    private FacilioForm constructFormFromXMLBuilder(XMLBuilder formElement, Map<String, Long> appNameVsAppId, ModuleBean moduleBean) throws Exception {
        String formName, moduleName, displayName, description, appLinkName, typeString, labelPositionStr;
        boolean isLocked, showInWeb, hideInList, primaryForm, showInMobile, isSystemForm;
        FacilioForm.LabelPosition labelPosition;
        long applicationId, moduleId;
        FacilioModule module;
        FacilioForm.Type type;

        moduleName = formElement.getElement(PackageConstants.MODULENAME).getText();
        description = formElement.getElement(PackageConstants.DESCRIPTION).getText();
        displayName = formElement.getElement(PackageConstants.DISPLAY_NAME).getText();
        typeString = formElement.getElement(PackageConstants.FormXMLComponents.TYPE).getText();
        formName = formElement.getElement(PackageConstants.FormXMLComponents.FORM_NAME).getText();
        appLinkName = formElement.getElement(PackageConstants.AppXMLConstants.APPLICATION_NAME).getText();
        labelPositionStr = formElement.getElement(PackageConstants.FormXMLComponents.LABEL_POSITION).getText();
        isLocked = Boolean.parseBoolean(formElement.getElement(PackageConstants.FormXMLComponents.IS_LOCKED).getText());
        showInWeb = Boolean.parseBoolean(formElement.getElement(PackageConstants.FormXMLComponents.SHOW_IN_WEB).getText());
        hideInList = Boolean.parseBoolean(formElement.getElement(PackageConstants.FormXMLComponents.HIDE_IN_LIST).getText());
        primaryForm = Boolean.parseBoolean(formElement.getElement(PackageConstants.FormXMLComponents.PRIMARY_FORM).getText());
        showInMobile = Boolean.parseBoolean(formElement.getElement(PackageConstants.FormXMLComponents.SHOW_IN_MOBILE).getText());
        isSystemForm = Boolean.parseBoolean(formElement.getElement(PackageConstants.FormXMLComponents.IS_SYSTEM_FORM).getText());

        labelPosition = StringUtils.isNotEmpty(labelPositionStr) ? FacilioForm.LabelPosition.valueOf(labelPositionStr) : null;
        applicationId = appNameVsAppId.containsKey(appLinkName) ? appNameVsAppId.get(appLinkName) : -1;
        type = StringUtils.isNotEmpty(typeString) ? FacilioForm.Type.valueOf(typeString) : null;
        module = moduleBean.getModule(moduleName);
        moduleId = module.getModuleId();

        FacilioForm form = new FacilioForm(formName, displayName, description, moduleId,  module, showInWeb, showInMobile,
                                        hideInList, isSystemForm, primaryForm, labelPosition, type, applicationId, appLinkName);
        form.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        form.setLocked(isLocked);

        // Form Sharing
        XMLBuilder sharingElement = formElement.getElement(PackageConstants.FormXMLComponents.FORM_SHARING);
        if (sharingElement != null) {
            SharingContext<SingleSharingContext> sharingContexts = PackageBeanUtil.constructSharingContextFromBuilder(sharingElement);
            form.setFormSharing(sharingContexts);
        }

        return form;
    }

    private void updateForm(FacilioForm form) throws Exception {
        FacilioModule formModule = ModuleFactory.getFormModule();
        Map<String, Object> props = FieldUtil.getAsProperties(form);

        GenericUpdateRecordBuilder formUpdateBuilder = new GenericUpdateRecordBuilder()
                .table(formModule.getTableName())
                .fields(FieldFactory.getFormFields())
                .andCondition(CriteriaAPI.getIdCondition(form.getId(), formModule));

        formUpdateBuilder.update(props);
    }

    private void addFormSharing(FacilioForm facilioForm) throws Exception {
        FacilioModule formSharingModule = ModuleFactory.getFormSharingModule();
        List<FacilioField> formSharingFields = FieldFactory.getFormSharingFields();
        SharingAPI.addSharing(facilioForm.getFormSharing(), formSharingFields, facilioForm.getId(), formSharingModule);
    }
}
