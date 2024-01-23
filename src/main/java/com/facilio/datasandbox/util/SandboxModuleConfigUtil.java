package com.facilio.datasandbox.util;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class SandboxModuleConfigUtil {
    public static List<String> SYSTEM_USER_PEOPLE_LOOKUP_FIELDS = Arrays.asList("sysCreatedBy", "sysModifiedBy", "sysDeletedBy", "sysCreatedByPeople", "sysModifiedByPeople", "sysDeletedByPeople");

    // Handled Special V2 Modules like "taskSection", "taskInputOption" (associated with "task" module)
    public static final Map<String, Map<String, Object>> SPECIAL_MODULENAME_VS_DETAILS = Collections.unmodifiableMap(initializeModuleDetails());

    private static Map<String, Map<String, Object>> initializeModuleDetails() {
        Map<String, Map<String, Object>> moduleNameVsModuleDetails = new LinkedHashMap<>();
        try {
            moduleNameVsModuleDetails.put(ModuleFactory.getTaskSectionModule().getName(), getModuleDetails(ModuleFactory.getTaskSectionModule(), FieldFactory.getTaskSectionFields()));
            moduleNameVsModuleDetails.put(ModuleFactory.getTaskInputOptionModule().getName(), getModuleDetails(ModuleFactory.getTaskInputOptionModule(), getTaskInputOptionsFields()));
            moduleNameVsModuleDetails.put(ModuleFactory.getReadingDataMetaModule().getName(), getModuleDetails(ModuleFactory.getReadingDataMetaModule(), FieldFactory.getReadingDataMetaFields()));
        } catch (Exception e) {
            LOGGER.info("####Data Migration - Exception while fetching SPECIAL_MODULENAME_VS_DETAILS - " + e);
            throw new RuntimeException(e);
        }

        return moduleNameVsModuleDetails;
    }

    private static Map<String, Object> getModuleDetails(FacilioModule module, List<FacilioField> fields) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        if (module.getTypeEnum() == null) {
            module.setType(FacilioModule.ModuleType.SUB_ENTITY);
        }
        List<String> numberFileFields = DataMigrationUtil.getNumberFileFields(module);
        Criteria moduleSpecificCriteria = DataMigrationUtil.getModuleSpecificCriteria(module);
        Map<String, Map<String, Object>> numberLookUps = DataMigrationUtil.getNumberLookups(module, null, null);

        Map<String, Object> moduleDetails = new HashMap<>();
        moduleDetails.put("fields", fields);
        moduleDetails.put("sourceModule", module);
        moduleDetails.put("numberLookups", numberLookUps);
        moduleDetails.put("fileFields", numberFileFields);
        moduleDetails.put("criteria", moduleSpecificCriteria);

        return moduleDetails;
    }

    // Misconfigured in FieldFactory
    public static List<FacilioField> getTaskInputOptionsFields() {
        FacilioModule module = ModuleFactory.getTaskInputOptionModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(FieldFactory.getIdField(module));
        fields.add(FieldFactory.getField("taskId", "TASK_ID", module, FieldType.NUMBER));
        fields.add(FieldFactory.getField("option", "OPTION_VALUE", module, FieldType.STRING));

        return fields;
    }

    public static Map<String, List<String>> getParentModuleVsChildModules() {
        Map<String, List<String>> parentModuleVsChildModules = new HashMap<>();
        parentModuleVsChildModules.put("task", Arrays.asList("tasksection", "taskInputOpyion"));
        parentModuleVsChildModules.put("readingdatameta", Arrays.asList("readingdatameta"));

        return parentModuleVsChildModules;
    }

    public static List<String> getSpecialModules(Map<String, Map<String, Object>> migrationModuleNameVsDetails) {
        List<String> specialModulesList = new ArrayList<>();
        Map<String, List<String>> parentModuleVsChildModules = getParentModuleVsChildModules();
        for (Map.Entry<String, List<String>> entry : parentModuleVsChildModules.entrySet()) {
            if (migrationModuleNameVsDetails.containsKey(entry.getKey())) {
                specialModulesList.addAll(entry.getValue());
            }
        }

        return specialModulesList;
    }

    // Modules copied in Meta are reUpdated in DataMigration flow
    public static List<String> updateOnlyModulesList() {
        return new ArrayList<String>(){{
            add(FacilioConstants.ContextNames.PEOPLE);
            add(FacilioConstants.ContextNames.VENDORS);
            add(FacilioConstants.ContextNames.CLIENT);
            add(FacilioConstants.ContextNames.TENANT);
            add(FacilioConstants.ContextNames.EMPLOYEE);
            add(FacilioConstants.ContextNames.TENANT_CONTACT);
            add(FacilioConstants.ContextNames.VENDOR_CONTACT);
            add(FacilioConstants.ContextNames.CLIENT_CONTACT);
            add(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
            add(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
        }};
    }

    // Picklist Modules copied in Meta (can be skipped in Data Migration)
    public static List<String> pickListTypeModules() {
        return new ArrayList<String>() {{
            add(FacilioConstants.ContextNames.SPACE_CATEGORY);
            add(FacilioConstants.ContextNames.ASSET_CATEGORY);
            add(FacilioConstants.ContextNames.ASSET_TYPE);
            add(FacilioConstants.ContextNames.ASSET_DEPARTMENT);
            add(FacilioConstants.ContextNames.TICKET_CATEGORY);
            add(FacilioConstants.ContextNames.TICKET_STATUS);
            add(FacilioConstants.ContextNames.TICKET_PRIORITY);
        }};
    }

    // Picklist Modules not copied in Meta (ModuleName Vs UniqueFieldName)
    public static Map<String, String> unhandledPickListModuleNameVsFieldName() {
        return new HashMap<String, String>() {{
            put("alarmseverity", "severity");
            put("energymeterpurpose", "name");
            put("inspectionPriority", "priority");
            put("itemStatus", "name");
            put("readingalarmcategory", "name");
            put("servicerequestpriority", "priority");
            put("tickettype", "name");
            put("toolStatus", "name");
            put("weatherservice", "name");
            put("weatherstation", "name");
            put("workpermittype", "type");
            put("alarmType", "linkName");
            put("serviceTaskStatus", "name");
            put("timeOffType", "name");
            put("timeSheetStatus", "status");
            put("serviceAppointmentTicketStatus", "status");
        }};
    }

    public static List<FacilioModule> getModuleObjects(List<String> dataMigrationModules, Map<String, FacilioModule> allSystemModulesMap) throws Exception {
        List<FacilioModule> moduleDetails = new ArrayList<>();
        for (String moduleName : dataMigrationModules) {
            if (allSystemModulesMap.get(moduleName) != null) {
                moduleDetails.add(allSystemModulesMap.get(moduleName));
            }
        }

        return moduleDetails;
    }

    public static Map<String, Map<String, Object>> getModuleDetails(ModuleBean modBean, List<FacilioModule> dataMigrationModules, Map<String, Criteria> reqModuleVsCriteria,
                                                                    Map<String, FacilioModule> dataModuleNameVsObj) throws Exception {

        Map<String, Map<String, Object>> moduleNameVsDetails = new LinkedHashMap<>();

        for (FacilioModule module : dataMigrationModules) {
            String moduleName = module.getName();
            Map<String, Object> details = (moduleNameVsDetails.containsKey(moduleName)) ? moduleNameVsDetails.get(moduleName) : new HashMap<>();
            details.put("sourceModule", module);

            Map<String, Map<String, Object>> numberLookUps = new HashMap<>();
            List<String> numberFileFields = new ArrayList<>();
            FacilioModule currentModule = module;
            while (currentModule != null) {
                numberLookUps.putAll(DataMigrationUtil.getNumberLookups(currentModule, modBean, dataModuleNameVsObj));
                numberFileFields.addAll(DataMigrationUtil.getNumberFileFields(currentModule));
                currentModule = currentModule.getExtendModule();
            }
            details.put("numberLookups", numberLookUps);
            details.put("fileFields", numberFileFields);

            Criteria moduleSpecificCriteria = DataMigrationUtil.getModuleSpecificCriteria(module);
            Criteria requestCriteria = reqModuleVsCriteria.containsKey(module.getName()) ? reqModuleVsCriteria.get(moduleName) : null;

            Criteria criteria = new Criteria();
            if (moduleSpecificCriteria != null) {
                criteria.andCriteria(moduleSpecificCriteria);
            }

            if (requestCriteria != null) {
                criteria.andCriteria(requestCriteria);
            }

            if (!criteria.isEmpty()) {
                details.put("criteria", criteria);
            }

            List<FacilioModule> childModules = modBean.getChildModules(module, null, null, true);
            if (CollectionUtils.isNotEmpty(childModules)) {
                List<Long> childModuleIds = new ArrayList<>();
                List<String> childModuleNames = new ArrayList<>();

                for (FacilioModule childModule : childModules) {
                    childModuleNames.add(childModule.getName());
                    childModuleIds.add(childModule.getModuleId());
                }

                details.put("childModules", childModuleNames);
                details.put("childModuleIds", childModuleIds);
            }
            moduleNameVsDetails.put(moduleName, details);
        }

        return moduleNameVsDetails;
    }

    public static HashMap<String, Map<String, Object>> getModuleSequence(Map<String, Map<String, Object>> moduleNameVsDetails, List<Long> extendedModuleIds) {

        HashMap<String, Map<String, Object>> migrationModuleNameVsDetails = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> moduleWithParentWithoutChild = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> moduleWithBothParentChild = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> notesAndAttachments = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> moduleWithOnlyChild = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> baseEntityModules = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> otherModules = new LinkedHashMap<>();

        for (Map.Entry<String, Map<String, Object>> moduleVsDetails : moduleNameVsDetails.entrySet()) {
            String key = moduleVsDetails.getKey();
            Map<String, Object> value = moduleVsDetails.getValue();
            FacilioModule module = (FacilioModule) value.get("sourceModule");
            if (module.getExtendModule() != null && !extendedModuleIds.contains(module.getModuleId())) {
                moduleWithParentWithoutChild.put(key, value);
            } else if (module.getExtendModule() != null && extendedModuleIds.contains(module.getModuleId())) {
                moduleWithBothParentChild.put(key, value);
            } else if (module.getExtendModule() == null && extendedModuleIds.contains(module.getModuleId())) {
                moduleWithOnlyChild.put(key, value);
            } else if (module.getTypeEnum() == FacilioModule.ModuleType.BASE_ENTITY ||
                    module.getTypeEnum() == FacilioModule.ModuleType.Q_AND_A_RESPONSE ||
                    module.getTypeEnum() == FacilioModule.ModuleType.Q_AND_A) {
                baseEntityModules.put(key, value);
            } else if (module.getTypeEnum() == FacilioModule.ModuleType.PHOTOS ||
                    module.getTypeEnum() == FacilioModule.ModuleType.NOTES ||
                    module.getTypeEnum() == FacilioModule.ModuleType.ATTACHMENTS) {
                notesAndAttachments.put(key, value);
            } else {
                otherModules.put(key, value);
            }
        }

        migrationModuleNameVsDetails.putAll(moduleWithParentWithoutChild);
        migrationModuleNameVsDetails.putAll(moduleWithBothParentChild);
        migrationModuleNameVsDetails.putAll(moduleWithOnlyChild);
        migrationModuleNameVsDetails.putAll(baseEntityModules);
        migrationModuleNameVsDetails.putAll(notesAndAttachments);
        migrationModuleNameVsDetails.putAll(otherModules);

        return migrationModuleNameVsDetails;
    }

    public static List<FacilioModule> getFilteredModuleList(List<FacilioModule> allModules) {
        List<Integer> moduleTypesToSkip = DataMigrationUtil.getModuleTypesToSkip();
        Set<String> skipMigrationModules = DataMigrationUtil.getMisConfiguredModules();
        return allModules.stream()
                .filter(module -> !moduleTypesToSkip.contains(module.getType()) && !skipMigrationModules.contains(module.getName())).collect(Collectors.toList());
    }

    public static List<FacilioModule> getExtendedModulesForSelectedModules(List<FacilioModule> selectedModules, Map<String, FacilioModule> allSystemModulesMap) throws Exception {
        List<FacilioModule> allExtendedModules = new ArrayList<>();
        List<FacilioModule> allSystemModules = new ArrayList<>(allSystemModulesMap.values());

        for (FacilioModule selectedModule : selectedModules) {
            if (LookupSpecialTypeUtil.isSpecialType(selectedModule.getName())) {
                continue;
            }
            List<FacilioModule> extendedModules = allSystemModules.stream()
                    .filter(facilioModule -> (facilioModule.getExtendModule() != null && facilioModule.getExtendModule().getModuleId() == selectedModule.getModuleId()))
                    .collect(Collectors.toList());
            allExtendedModules.addAll(extendedModules);
        }

        return allExtendedModules;

    }

    public static void getRelatedFields(DataMigrationBean migrationBean, List<Long> parentModuleIds, List<Long> childModuleIds, Map<String, List<FacilioField>> moduleNameVsRelatedFields) throws Exception {
        if (CollectionUtils.isEmpty(parentModuleIds) || CollectionUtils.isEmpty(childModuleIds)) {
            return;
        }
        List<FacilioField> relatedFields = migrationBean.getRelatedFields(parentModuleIds, childModuleIds);
        if (CollectionUtils.isNotEmpty(relatedFields)) {
            for (FacilioField field : relatedFields) {
                moduleNameVsRelatedFields.computeIfAbsent(field.getModule().getName(), k -> new ArrayList<>());
                moduleNameVsRelatedFields.get(field.getModule().getName()).add(field);
            }
        }
    }

    public static void addRelatedFields(Map<String, List<FacilioField>> moduleNameVsRelatedFields, Map<String, Map<String, Object>> moduleNameVsDetails) {
        if (MapUtils.isEmpty(moduleNameVsRelatedFields) || MapUtils.isEmpty(moduleNameVsDetails)) {
            return;
        }

        for (String moduleName : moduleNameVsDetails.keySet()) {
            if (moduleNameVsRelatedFields.containsKey(moduleName)) {
                List<FacilioField> relatedFields = moduleNameVsRelatedFields.get(moduleName);
                moduleNameVsDetails.get(moduleName).put("relatedFields", relatedFields);
            }
        }
    }

    public static Map<String, FacilioField> getModuleFields(String moduleName, List<String> fieldsNames) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);

        if (module == null) {
            LOGGER.info("####Data Migration Tracking - Module not found while getting fields - " + moduleName);
            return null;
        }

        Criteria fieldsCriteria = new Criteria();
        fieldsCriteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        fieldsCriteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", StringUtils.join(fieldsNames, ","), StringOperators.IS));

        List<FacilioField> fieldModuleFields = FieldFactory.getFieldFields();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fieldModuleFields)
                .table("Fields");

        if (!fieldsCriteria.isEmpty()) {
            selectBuilder.andCriteria(fieldsCriteria);
        }

        List<Map<String, Object>> fieldProps = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(fieldProps)) {
            Map<String, FacilioField> fieldNameVsField = new HashMap<>();
            for (Map<String, Object> fieldProp : fieldProps) {
                FacilioField field = FieldUtil.getAsBeanFromMap(fieldProp, FacilioField.class);
                field.setModule(module);
                fieldNameVsField.put(field.getName(), field);
            }
            return fieldNameVsField;
        }
        return null;
    }

    public static Criteria computeRelatedFieldCriteriaForSubModules(List<LookupField> relatedFields, Map<String, List<Long>> fetchedRecords) {
        if (CollectionUtils.isNotEmpty(relatedFields)) {
            Criteria relatedCriteria = new Criteria();
            for (LookupField field : relatedFields) {
                String lookupModuleName = field.getLookupModule().getName();
                if (fetchedRecords.containsKey(lookupModuleName)) {
                    List<Long> parentRecordIds = fetchedRecords.get(lookupModuleName);
                    relatedCriteria.addOrCondition(CriteriaAPI.getCondition(field, parentRecordIds, NumberOperators.EQUALS));
                }
            }
            return relatedCriteria;
        }
        return null;
    }

    public static void addSystemFields(FacilioModule module, Map<String, FacilioField> fieldsMap) {
        if (!fieldsMap.containsKey(FacilioConstants.ContextNames.SITE_ID)) {
            FacilioField siteIdField = FieldFactory.getSiteIdField(module);
            fieldsMap.put(siteIdField.getName(), siteIdField);
        }

        if (!fieldsMap.containsKey(FacilioConstants.ContextNames.FORM_ID)) {
            FacilioField formIdField = FieldFactory.getNumberField(FacilioConstants.ContextNames.FORM_ID, "FORM_ID", module.getParentModule());
            fieldsMap.put(formIdField.getName(), formIdField);
        }

        if (!fieldsMap.containsKey(FacilioConstants.ContextNames.STATE_FLOW_ID)) {
            FacilioField stateFlowIdFields = FieldFactory.getNumberField(FacilioConstants.ContextNames.STATE_FLOW_ID, "STATE_FLOW_ID", module);
            fieldsMap.put(stateFlowIdFields.getName(), stateFlowIdFields);
        }

        FacilioField idField = FieldFactory.getIdField(module);
        fieldsMap.put(idField.getName(), idField);
    }

    public static List<FacilioField> getSysDeletedFields(FacilioModule module, Map<String, FacilioField> fieldsMap) {
        List<FacilioField> deletedFields = new ArrayList<>();
        if (module.isTrashEnabled()) {
            FacilioModule parentModule = module.getParentModule();
            FacilioField isDeletedField = FieldFactory.getIsDeletedField(parentModule);
            FacilioField sysDeletedTimeField = FieldFactory.getSysDeletedTimeField(parentModule);
            if (!fieldsMap.containsKey(isDeletedField.getName())) {
                fieldsMap.put(isDeletedField.getName(), isDeletedField);
                deletedFields.add(isDeletedField);
            }
            if (!fieldsMap.containsKey(sysDeletedTimeField.getName())) {
                fieldsMap.put(sysDeletedTimeField.getName(), sysDeletedTimeField);
                deletedFields.add(sysDeletedTimeField);
            }

            if(V3RecordAPI.markAsDeleteEnabled(parentModule)) {
                FacilioField sysDeletedPeopleByField = FieldFactory.getSysDeletedPeopleByField(parentModule);
                if (!fieldsMap.containsKey(sysDeletedPeopleByField.getName())) {
                    fieldsMap.put(sysDeletedPeopleByField.getName(), sysDeletedPeopleByField);
                    deletedFields.add(sysDeletedPeopleByField);
                }
            } else {
                FacilioField sysDeletedByField = FieldFactory.getSysDeletedByField(parentModule);
                if (!fieldsMap.containsKey(sysDeletedByField.getName())) {
                    ((LookupField) sysDeletedByField).setLookupModule(LookupSpecialTypeUtil.getModule(FacilioConstants.ContextNames.USERS));
                    sysDeletedByField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
                    fieldsMap.put(sysDeletedByField.getName(), sysDeletedByField);
                    deletedFields.add(sysDeletedByField);
                }
            }
        }
        return deletedFields;
    }

    public static FacilioModule getParentModuleForSubModule(FacilioModule subModule, FacilioModule.ModuleType parentModuleType) throws Exception {
        FacilioModule moduleModule = ModuleFactory.getModuleModule();
        FacilioModule relModule = ModuleFactory.getSubModulesRelModule();
        List<FacilioField> moduleFields = FieldFactory.getModuleFields();
        Map<String,FacilioField> relModuleFieldMap = FieldFactory.getAsMap(FieldFactory.getSubModuleRelFields(relModule));

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(moduleFields)
                .table(moduleModule.getTableName())
                .innerJoin(relModule.getTableName())
                .on(relModule.getTableName() + ".PARENT_MODULE_ID = " + moduleModule.getTableName() + ".MODULEID")
                .andCondition(CriteriaAPI.getCondition(relModuleFieldMap.get("childModuleId"), String.valueOf(subModule.getModuleId()), NumberOperators.EQUALS));

        if (parentModuleType != null) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("MODULE_TYPE", "moduleType", String.valueOf(parentModuleType.getValue()), NumberOperators.EQUALS));
        }

        Map<String, Object> prop = selectRecordBuilder.fetchFirst();

        if (MapUtils.isNotEmpty(prop)) {
            if (prop.containsKey("createdBy")) {
                IAMUser user = new IAMUser();
                user.setId((long) prop.get("createdBy"));
                prop.put("createdBy", user);
            }
            return FieldUtil.getAsBeanFromMap(prop, FacilioModule.class);
        }
        return null;
    }
}
