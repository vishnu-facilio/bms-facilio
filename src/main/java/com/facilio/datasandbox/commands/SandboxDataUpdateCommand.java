package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class SandboxDataUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        if(dataMigrationObj.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.UPDATION_IN_PROGRESS.getIndex()) {
            return false;
        }

        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        long dataMigrationId = (long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1l);
        long transactionStartTime = (long) context.get(DataMigrationConstants.TRANSACTION_START_TIME);
        long transactionTimeOut = (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_TIME_OUT, 500000l);

        List<String> logModulesNames = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);
        List<String> updateOnlyModulesList = (List<String>) context.getOrDefault(DataMigrationConstants.UPDATE_ONLY_MODULES, new ArrayList<>());
        Map<String, String> moduleNameVsXmlFileName = (Map<String, String>) context.get(DataMigrationConstants.MODULE_NAMES_XML_FILE_NAME);
        List<String> skipDataMigrationModules = (List<String>) context.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (Map<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);
        Map<ComponentType, Map<Long, Long>> componentTypeVsOldIdVsNewId = (Map<ComponentType, Map<Long, Long>>) context.get(DataMigrationConstants.PACKAGE_CHANGE_SET);

        Map<String, Map<String, String>> nonNullableModuleVsFieldVsLookupModules = DataMigrationUtil.getNonNullableModuleVsFieldVsLookupModules();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        String lastModuleName = null;
        boolean moduleMigrationStarted = true;
        long lastMigratedModuleId = dataMigrationObj.getLastModuleId();

        if(lastMigratedModuleId > 0) {
            moduleMigrationStarted = false;
            lastModuleName = modBean.getModule(lastMigratedModuleId).getName();
        } else {
            migrationBean.updateDataMigrationStatus(dataMigrationObj.getId(), DataMigrationStatusContext.DataMigrationStatus.UPDATION_IN_PROGRESS, null, 0);
        }

        for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();

            if(!moduleMigrationStarted && !moduleName.equals(lastModuleName)) {
                continue;
            } else {
                moduleMigrationStarted = true;
            }

            if (skipDataMigrationModules.contains(moduleName) || SandboxModuleConfigUtil.SPECIAL_MODULENAME_VS_DETAILS.containsKey(moduleName)) {
                LOGGER.info("####Data Migration - Update - skipped for ModuleName - " + moduleName);
                continue;
            }

            FacilioModule module = null;
            List<String> numberFileFields = new ArrayList<>();
            Map<String, Map<String, Object>> numberLookUps = new HashMap<>();

            if (MapUtils.isNotEmpty(moduleDetails) && moduleDetails.get("sourceModule") != null) {
                module = (FacilioModule) moduleDetails.get("sourceModule");
                numberFileFields = (List<String>) moduleDetails.get("fileFields");
                numberLookUps = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");
            } else {
                module = modBean.getModule(moduleName);
            }

            if (module == null) {
                LOGGER.info("####Data Migration - Update - Module not found for ModuleName - " + moduleName);
                continue;
            }

            LOGGER.info("####Data Migration - Update - Started for ModuleName - " + moduleName);

            Map<String, String> nonNullableFieldVsLookupModules = nonNullableModuleVsFieldVsLookupModules.getOrDefault(moduleName, new HashMap<>());
            if (module.getTypeEnum() == FacilioModule.ModuleType.ACTIVITY) {
                FacilioModule parentModule = modBean.getParentModule(module.getModuleId());
                parentModule = (moduleName.equals(FacilioConstants.ContextNames.CUSTOM_ACTIVITY)) ? module : parentModule;

                nonNullableFieldVsLookupModules.put("parentId", parentModule.getName());
                nonNullableFieldVsLookupModules.put("doneBy", FacilioConstants.ContextNames.USERS);
            } else if (module.getTypeEnum() == FacilioModule.ModuleType.READING) {
                FacilioModule parentModuleForReadingModule = SandboxModuleConfigUtil.getParentModuleForReadingModule(module);
                if (parentModuleForReadingModule != null) {
                    nonNullableFieldVsLookupModules.put("parentId", parentModuleForReadingModule.getName());
                }
            }

            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> allFieldsMap = FieldFactory.getAsMap(allFields);

            Map<String, FacilioField> lookupTypeFieldsMap = getLookupTypeFields(module, allFields, new ArrayList<>(nonNullableFieldVsLookupModules.keySet()), numberLookUps);
            if (MapUtils.isEmpty(lookupTypeFieldsMap)) {
                LOGGER.info("####Data Migration - Update - Fields to Update is null for ModuleName - " + moduleName);
                continue;
            }

            // Add System Fields "id"
            FacilioField idField = FieldFactory.getIdField(module);
            lookupTypeFieldsMap.putIfAbsent("id", idField);

            String moduleFileName = moduleNameVsXmlFileName.get(moduleName);
            List<SupplementRecord> supplementRecords = DataMigrationUtil.getSupplementFields(lookupTypeFieldsMap.values());
            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            int offset = 0;
            int limit = 5000;
            if (moduleName.equals(lastModuleName) && offset < dataMigrationObj.getMigratedCount()) {
                offset = (int) dataMigrationObj.getMigratedCount();
            }

            boolean isModuleMigrated = false;
            Map<Long, Long> siteIdMapping = new HashMap<>();

            if (moduleName.equals(FacilioConstants.ContextNames.TASK)) {
                lookupTypeFieldsMap.put("##ReadingFieldModule##", null);
                lookupTypeFieldsMap.put("inputType", allFieldsMap.get("inputType"));
                lookupTypeFieldsMap.put("readingFieldId", allFieldsMap.get("readingFieldId"));
            } else if (moduleName.equals(FacilioConstants.ContextNames.COMMENT_ATTACHMENTS)) {
                lookupTypeFieldsMap.put("commentModuleId", allFieldsMap.get("commentModuleId"));
            }

            do {
                List<Map<String, Object>> dataFromCSV = SandboxDataMigrationUtil.getDataFromCSV(module, moduleFileName, lookupTypeFieldsMap, numberFileFields, offset, limit + 1);

                if (CollectionUtils.isEmpty(dataFromCSV)) {
                    LOGGER.info("####Data Migration - Update - No Records obtained from CSV - " + moduleName);
                    isModuleMigrated = true;
                } else {
                    if (dataFromCSV.size() > limit) {
                        dataFromCSV.remove(limit);
                    } else {
                        isModuleMigrated = true;
                    }
                    LOGGER.info("####Data Migration - Update - In progress - " + moduleName + " - Offset - " + offset);

                    // dataFromCSV may contain additional fields, remove unnecessary fields for Update
                    for (Map<String, Object> prop : dataFromCSV) {
                        List<String> nonLookUpKeys = prop.keySet().stream().filter(key -> !lookupTypeFieldsMap.containsKey(key)).collect(Collectors.toList());
                        nonLookUpKeys.forEach(prop::remove);
                    }

                    if (MapUtils.isEmpty(siteIdMapping) || moduleName.equals(FacilioConstants.ContextNames.SITE)) {
                        FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
                        siteIdMapping = migrationBean.getOldVsNewId(dataMigrationId, siteModule.getModuleId(), null);
                    }

                    List<Map<String, Object>> propsToUpdate = sanitizePropsBeforeUpdate(module, lookupTypeFieldsMap, dataFromCSV, componentTypeVsOldIdVsNewId,
                            numberLookUps, siteIdMapping, dataMigrationId, migrationBean);

                    if (CollectionUtils.isNotEmpty(propsToUpdate)) {
                        if (moduleName.equals(FacilioConstants.ContextNames.TASK)) {
                            lookupTypeFieldsMap.remove("##ReadingFieldModule##");
                        }
                        migrationBean.updateModuleData(module, new ArrayList<>(lookupTypeFieldsMap.values()), supplementRecords, propsToUpdate, addLogger);
                    }

                    offset = offset + dataFromCSV.size();
                }
                migrationBean.updateDataMigrationStatus(dataMigrationObj.getId(), DataMigrationStatusContext.DataMigrationStatus.UPDATION_IN_PROGRESS, module.getModuleId(), offset);

                if ((System.currentTimeMillis() - transactionStartTime) > transactionTimeOut) {
                    LOGGER.info("####Data Migration - Update - Stopped after exceeding transaction timeout with ModuleName - " + moduleName + " Offset - " + offset);
                    return true;
                }
            } while (!isModuleMigrated);

            LOGGER.info("####Data Migration - Update - Completed for ModuleName - " + moduleName);
        }

        migrationBean.updateDataMigrationStatusWithModuleName(dataMigrationId, DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_IN_PROGRESS, null, 0);

        return false;
    }

    private static List<Map<String, Object>> sanitizePropsBeforeUpdate(FacilioModule module, Map<String, FacilioField> fieldsMap, List<Map<String, Object>> propsList,
                                                                       Map<ComponentType, Map<Long, Long>> componentTypeVsOldVsNewId, Map<String, Map<String, Object>> numberLookUps, Map<Long, Long> siteIdMapping,
                                                                       long dataMigrationId, DataMigrationBean migrationBean) throws Exception {

        List<String> fieldsToParse = new ArrayList<>(fieldsMap.keySet());
        Map<Long, List<Long>> moduleIdVsOldIds = SandboxDataMigrationUtil.getModuleIdVsOldIds(module.getModuleId(), propsList, fieldsMap, numberLookUps, fieldsToParse);
        Map<Long, Map<Long, Long>> oldIdVsNewIdMapping = SandboxDataMigrationUtil.getOldIdVsNewIdMapping(migrationBean, dataMigrationId, moduleIdVsOldIds);

        List<Map<String, Object>> propsToUpdate = new ArrayList<>();
        for (Map<String, Object> prop : propsList) {
            boolean newIdCreated = false;
            boolean hasLookupValue = false;
            Map<String, Object> updatedProp = new HashMap<>();
            for (Map.Entry<String, Object> entry : prop.entrySet()) {
                Object value = entry.getValue();
                String fieldName = entry.getKey();
                FacilioField fieldObj = fieldsMap.get(fieldName);

                if (value != null) {
                    // Handle System Fields
                    if (fieldName.equals("id")) {
                        long oldId = (long) value;
                        if (MapUtils.isNotEmpty(oldIdVsNewIdMapping) && oldIdVsNewIdMapping.containsKey(module.getModuleId()) && oldIdVsNewIdMapping.get(module.getModuleId()).containsKey(oldId)) {
                            Long newId = oldIdVsNewIdMapping.get(module.getModuleId()).get(oldId);
                            updatedProp.put(fieldName, newId);
                            newIdCreated = true;
                        } else if (PackageUtil.nameVsComponentType.containsKey(module.getName())) {
                            Long newId = SandboxDataMigrationUtil.getMetaConfNewId(module.getName(), oldId, componentTypeVsOldVsNewId);
                            updatedProp.put(fieldName, newId);
                            newIdCreated = true;
                        } else {
                            LOGGER.info("####Data Migration - Update - Record not created - ModuleName - " + module.getName() + " OldId - " + oldId);
                        }
                    } else if (fieldName.equals(FacilioConstants.ContextNames.SITE_ID) && MapUtils.isNotEmpty(siteIdMapping)) {
                        hasLookupValue = true;
                        updatedProp.put(fieldName, siteIdMapping.get((Long) prop.get(fieldName)));
                    } else if (fieldName.equals("site") && MapUtils.isNotEmpty(siteIdMapping)) {
                        hasLookupValue = true;
                        Map<String, Long> site = (Map<String, Long>) prop.get(fieldName);
                        if (siteIdMapping.containsKey(site.get("id"))) {
                            site.put("id", siteIdMapping.get(site.get("id")));
                            updatedProp.put(fieldName, site);
                        }
                    } else if (fieldsToParse.contains(fieldName) && fieldObj != null) {
                        hasLookupValue = true;
                        SandboxDataMigrationUtil.updateLookupData(fieldObj, value, updatedProp, numberLookUps, oldIdVsNewIdMapping, componentTypeVsOldVsNewId);
                    } else {
                        updatedProp.put(fieldName, value);
                    }
                }
            }
            if (newIdCreated && hasLookupValue) {
                propsToUpdate.add(updatedProp);
            }
        }
        // Special Handling
        updateLookUpIds(module, new ArrayList<>(fieldsMap.values()), propsToUpdate, dataMigrationId, migrationBean);
        return propsToUpdate;
    }

    private static Map<String, FacilioField> getLookupTypeFields(FacilioModule module, List<FacilioField> allFields, List<String> nonNullableFieldNames, Map<String, Map<String, Object>> numberLookups) throws Exception {
        List<FacilioField> targetFields;
        Map<String, FacilioField> targetFieldNameVsFields = new HashMap<>();

        if (CollectionUtils.isNotEmpty(allFields)) {
            targetFields = allFields.stream().filter(field -> field.getName().equals("id") || field.getName().equals("siteId") || field.getDataTypeEnum().equals(FieldType.LOOKUP)
                            || field.getDataTypeEnum().equals(FieldType.MULTI_LOOKUP)
                            || (MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(field.getName())))
                    .collect(Collectors.toList());
            targetFields.removeIf(field -> (CollectionUtils.isNotEmpty(nonNullableFieldNames) && nonNullableFieldNames.contains(field.getName())));
            targetFieldNameVsFields = targetFields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
        }

        Set<String> siteIdAllowedModules = FieldUtil.getSiteIdAllowedModules();
        if (siteIdAllowedModules.contains(module.getName())) {
            FacilioField siteIdField = FieldFactory.getSiteIdField(module);
            targetFieldNameVsFields.putIfAbsent("siteId", siteIdField);
        }
        return targetFieldNameVsFields;
    }

    private static void updateLookUpIds(FacilioModule module, List<FacilioField> fields, List<Map<String, Object>> propsList,
                                        long dataMigrationId, DataMigrationBean migrationBean) throws Exception {
        if (module.getName().equals(FacilioConstants.ContextNames.COMMENT_ATTACHMENTS)) {
            if (CollectionUtils.isNotEmpty(propsList)) {
                Map<Long, List<Long>> moduleIdVsOldIds = new HashMap<>();
                for (Map<String, Object> prop : propsList) {
                    long commentModuleId = prop.containsKey("commentModuleId") ? (long) prop.get("commentModuleId") : -1;
                    long parentId = prop.containsKey("parent") ? (long) prop.get("parent") : -1;

                    if (commentModuleId > 0 && parentId > 0) {
                        moduleIdVsOldIds.computeIfAbsent(commentModuleId, k -> new ArrayList<>());
                        moduleIdVsOldIds.get(commentModuleId).add(parentId);
                    }
                }
                if (MapUtils.isNotEmpty(moduleIdVsOldIds)) {
                    Map<Long, Map<Long, Long>> oldIdVsNewIdMapping = SandboxDataMigrationUtil.getOldIdVsNewIdMapping(migrationBean, dataMigrationId, moduleIdVsOldIds);
                    if (MapUtils.isNotEmpty(oldIdVsNewIdMapping)) {
                        for (Map<String, Object> prop : propsList) {
                            long commentModuleId = prop.containsKey("commentModuleId") ? (long) prop.get("commentModuleId") : -1;
                            long oldId = prop.containsKey("parent") ? (long) prop.get("parent") : -1;

                            if (oldIdVsNewIdMapping.containsKey(commentModuleId) && oldIdVsNewIdMapping.get(commentModuleId).containsKey(oldId)) {
                                prop.put("parent", oldIdVsNewIdMapping.get(commentModuleId).get(oldId));
                            }
                        }
                    }
                }
            }
        } else if (module.getName().equals(FacilioConstants.ContextNames.TASK)) {
            if (CollectionUtils.isNotEmpty(propsList)) {
                Set<Long> oldSectionIds = new HashSet<>();
                Map<Long, List<Long>> moduleIdVsOldIds = new HashMap<>();
                for (Map<String, Object> prop : propsList) {
                    if (prop.containsKey("sectionId")) {
                        oldSectionIds.add((long) prop.get("sectionId"));
                    }
                    Integer inputType = prop.containsKey("inputType") ? Integer.parseInt(prop.get("inputType") + "") : null;
                    if (inputType != null && inputType == V3TaskContext.InputType.READING.getVal()) {
                        FacilioModule readingFieldModule = prop.containsKey("##ReadingFieldModule##") ? (FacilioModule) prop.get("##ReadingFieldModule##") : null;
                        long readingFieldModuleId = readingFieldModule != null ? readingFieldModule.getModuleId() : -1;
                        long readingDataId = prop.containsKey("readingDataId") ? (long) prop.get("readingDataId") : -1;

                        if (readingDataId > 0 && readingFieldModuleId > 0) {
                            moduleIdVsOldIds.computeIfAbsent(readingFieldModuleId, k -> new ArrayList<>());
                            moduleIdVsOldIds.get(readingFieldModuleId).add(readingDataId);
                        }
                    }
                }
                // sectionId field
                if (CollectionUtils.isNotEmpty(oldSectionIds)) {
                    FacilioModule taskSectionModule = ModuleFactory.getTaskSectionModule();
                    Map<Long, Long> oldVsNewSectionIds = migrationBean.getOldVsNewId(dataMigrationId, taskSectionModule.getName(), new ArrayList<>(oldSectionIds));
                    if (MapUtils.isNotEmpty(oldVsNewSectionIds)) {
                        for (Map<String, Object> prop : propsList) {
                            long oldSectionId = prop.containsKey("sectionId") ? (long) prop.get("sectionId") : -1;
                            Long newSectionId = oldVsNewSectionIds.get(oldSectionId);
                            prop.put("sectionId", newSectionId);
                        }
                    }
                }

                // other fields
                if (MapUtils.isNotEmpty(moduleIdVsOldIds)) {
                    Map<Long, Map<Long, Long>> oldIdVsNewIdMapping = SandboxDataMigrationUtil.getOldIdVsNewIdMapping(migrationBean, dataMigrationId, moduleIdVsOldIds);
                    if (MapUtils.isNotEmpty(oldIdVsNewIdMapping)) {
                        for (Map<String, Object> prop : propsList) {
                            Integer inputType = prop.containsKey("inputType") ? Integer.parseInt(prop.get("inputType") + "") : null;
                            if (inputType != null && inputType == V3TaskContext.InputType.READING.getVal()) {
                                FacilioModule readingFieldModule = prop.containsKey("##ReadingFieldModule##") ? (FacilioModule) prop.get("##ReadingFieldModule##") : null;
                                long readingFieldModuleId = readingFieldModule != null ? readingFieldModule.getModuleId() : -1;
                                long oldId = prop.containsKey("readingDataId") ? (long) prop.get("readingDataId") : -1;

                                if (oldIdVsNewIdMapping.containsKey(readingFieldModuleId) && oldIdVsNewIdMapping.get(readingFieldModuleId).containsKey(oldId)) {
                                    prop.put("readingDataId", oldIdVsNewIdMapping.get(readingFieldModuleId).get(oldId));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
