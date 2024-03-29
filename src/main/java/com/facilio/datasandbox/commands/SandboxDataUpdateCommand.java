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
import com.facilio.datasandbox.context.ModuleCSVFileContext;
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
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class SandboxDataUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        if(dataMigrationObj.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_UPDATION.getIndex()) {
            return false;
        }

        int queryLimit = (int) context.getOrDefault(DataMigrationConstants.QUERY_LIMIT, 0);
        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        long dataMigrationId = (long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1l);
        long transactionStartTime = (long) context.get(DataMigrationConstants.TRANSACTION_START_TIME);
        long transactionTimeOut = (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_TIME_OUT, 500000l);

        List<String> logModulesNames = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);
        List<String> updateOnlyModulesList = (List<String>) context.getOrDefault(DataMigrationConstants.UPDATE_ONLY_MODULES, new ArrayList<>());
        List<String> skipDataMigrationModules = (List<String>) context.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (Map<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);
        Map<ComponentType, Map<Long, Long>> componentTypeVsOldIdVsNewId = (Map<ComponentType, Map<Long, Long>>) context.get(DataMigrationConstants.PACKAGE_CHANGE_SET);
        Map<String, Stack<ModuleCSVFileContext>> moduleNameVsCSVFileContexts = (Map<String, Stack<ModuleCSVFileContext>>) context.get(DataMigrationConstants.MODULENAME_VS_CSV_FILE_CONTEXT);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        String lastModuleName = null;
        boolean moduleMigrationStarted = true;
        long lastMigratedModuleId = dataMigrationObj.getLastModuleId();
        String lastModuleFileName = dataMigrationObj.getModuleFileName();

        if(lastMigratedModuleId > 0) {
            moduleMigrationStarted = false;
            lastModuleName = modBean.getModule(lastMigratedModuleId).getName();
        } else {
            SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_UPDATION, null, null, 0);
        }

        for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();

            if(!moduleMigrationStarted && !moduleName.equals(lastModuleName)) {
                continue;
            } else {
                moduleMigrationStarted = true;
            }

            if (updateOnlyModulesList.contains(moduleName) || skipDataMigrationModules.contains(moduleName) || SandboxModuleConfigUtil.SPECIAL_MODULENAME_VS_DETAILS.containsKey(moduleName)) {
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

            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> allFieldsMap = FieldFactory.getAsMap(allFields);

            Map<String, String> nonNullableFieldVsLookupModules = DataMigrationUtil.getNonNullableColumns(module);
            DataMigrationUtil.getModuleTypeWiseNonNullableColumns(module, allFieldsMap, nonNullableFieldVsLookupModules);

            Map<String, FacilioField> lookupTypeFieldsMap = getLookupTypeFields(module, allFields, new ArrayList<>(nonNullableFieldVsLookupModules.keySet()), numberLookUps);
            if (MapUtils.isEmpty(lookupTypeFieldsMap)) {
                LOGGER.info("####Data Migration - Update - Fields to Update is null for ModuleName - " + moduleName);
                continue;
            }

            // Add System Fields "id"
            FacilioField idField = FieldFactory.getIdField(module);
            lookupTypeFieldsMap.putIfAbsent("id", idField);

            Stack<ModuleCSVFileContext> moduleCSVFileContexts = moduleNameVsCSVFileContexts.get(moduleName);
            List<SupplementRecord> supplementRecords = DataMigrationUtil.getSupplementFields(lookupTypeFieldsMap.values());
            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            int offset = 0;
            int limit = 5000;
            if (queryLimit > 0) {
                limit = queryLimit;
            }

            String moduleFileName = null;
            Map<Long, Long> siteIdMapping = new HashMap<>();

            if (moduleName.equals(FacilioConstants.ContextNames.TASK) || module.getName().equals(FacilioConstants.ContextNames.JOB_PLAN_TASK)) {
                lookupTypeFieldsMap.put("##ReadingFieldModule##", null);
                lookupTypeFieldsMap.put("inputType", allFieldsMap.get("inputType"));
                lookupTypeFieldsMap.put("readingFieldId", allFieldsMap.get("readingFieldId"));
            } else if (moduleName.equals(FacilioConstants.ContextNames.COMMENT_ATTACHMENTS)) {
                lookupTypeFieldsMap.put("commentModuleId", allFieldsMap.get("commentModuleId"));
            }

            try {
                for (ModuleCSVFileContext moduleCSVFileContext : moduleCSVFileContexts) {
                    offset = 0;
                    boolean isModuleMigrated = false;
                    moduleFileName = moduleCSVFileContext.getCsvFileName();

                    if (StringUtils.isNotEmpty(lastModuleFileName)) {
                        if (lastModuleFileName.equals(moduleFileName)) {
                            if (offset < dataMigrationObj.getMigratedCount()) {
                                offset = (int) dataMigrationObj.getMigratedCount();
                            }
                            lastModuleFileName = null;
                        } else {
                            continue;
                        }
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
                                if (moduleName.equals(FacilioConstants.ContextNames.TASK) || module.getName().equals(FacilioConstants.ContextNames.JOB_PLAN_TASK)) {
                                    lookupTypeFieldsMap.remove("##ReadingFieldModule##");
                                }
                                migrationBean.updateModuleData(module, new ArrayList<>(lookupTypeFieldsMap.values()), supplementRecords, propsToUpdate, addLogger);
                            }

                            offset = offset + dataFromCSV.size();
                        }
                        SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_UPDATION, module.getModuleId(), moduleFileName, offset);

                        if ((System.currentTimeMillis() - transactionStartTime) > transactionTimeOut) {
                            LOGGER.info("####Data Migration - Update - Stopped after exceeding transaction timeout with ModuleName - " + moduleName + " Offset - " + offset);
                            return true;
                        }
                    } while (!isModuleMigrated);
                }
            } catch (Exception e) {
                SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_UPDATION, module.getModuleId(), moduleFileName, offset);
                LOGGER.info("####Data Migration - Update - Error occurred in ModuleName - " + moduleName, e);
                context.put(DataMigrationConstants.ERROR_OCCURRED, true);
                return true;
            }

            LOGGER.info("####Data Migration - Update - Completed for ModuleName - " + moduleName);
        }

        SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.META_MODULES_REUPDATE, null, null, 0);
        dataMigrationObj = migrationBean.getDataMigrationStatus(dataMigrationObj.getId());

        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);
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
            targetFields = allFields.stream().filter(field -> field.getName().equals("id") || field.getDataTypeEnum().equals(FieldType.LOOKUP)
                            || field.getDataTypeEnum().equals(FieldType.MULTI_LOOKUP)
                            || (MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(field.getName())))
                    .collect(Collectors.toList());
            targetFields.removeIf(field -> (CollectionUtils.isNotEmpty(nonNullableFieldNames) && nonNullableFieldNames.contains(field.getName())));
            targetFieldNameVsFields = targetFields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
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
        } else if (module.getName().equals(FacilioConstants.ContextNames.TASK) || module.getName().equals(FacilioConstants.ContextNames.JOB_PLAN_TASK)) {
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
