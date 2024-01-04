package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.datasandbox.context.ModuleCSVFileContext;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class ReadingDataInsertCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        if (dataMigrationObj.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.READING_MODULE_DATA_INSERTION.getIndex()) {
            return false;
        }

        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        long dataMigrationId = (long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1l);
        long transactionStartTime = (long) context.get(DataMigrationConstants.TRANSACTION_START_TIME);
        long transactionTimeOut = (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_TIME_OUT, 500000l);
        List<String> moduleSequenceList = (List<String>) context.get(DataMigrationConstants.MODULE_SEQUENCE);
        List<String> logModulesNames = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);
        Map<String, Stack<ModuleCSVFileContext>> moduleNameVsCSVFileContexts = (Map<String, Stack<ModuleCSVFileContext>>) context.get(DataMigrationConstants.MODULENAME_VS_CSV_FILE_CONTEXT);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        boolean moduleMigrationStarted = true;
        String lastModuleName = dataMigrationObj.getLastModuleName();
        String lastModuleFileName = dataMigrationObj.getModuleFileName();
        if (StringUtils.isNotEmpty(lastModuleName)) {
            moduleMigrationStarted = false;
        } else {
            SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.READING_MODULE_DATA_INSERTION, null, null, 0);
        }

        Map<String, List<String>> parentModuleVsChildModules = SandboxModuleConfigUtil.getParentModuleVsChildModules();
        List<String> readingModulesList = parentModuleVsChildModules.get(FacilioConstants.ContextNames.READING_DATA_META);

        for (String moduleName : readingModulesList) {
            if ((CollectionUtils.isNotEmpty(moduleSequenceList) && !moduleSequenceList.contains(moduleName)) ||
                    !SandboxModuleConfigUtil.SPECIAL_MODULENAME_VS_DETAILS.containsKey(moduleName)) {
                continue;
            }
            Stack<ModuleCSVFileContext> moduleCSVFileContexts = moduleNameVsCSVFileContexts.get(moduleName);
            Map<String, Object> moduleDetails = SandboxModuleConfigUtil.SPECIAL_MODULENAME_VS_DETAILS.get(moduleName);

            if (!moduleMigrationStarted && !moduleName.equals(lastModuleName)) {
                continue;
            } else {
                moduleMigrationStarted = true;
            }

            String moduleFileName = null;
            FacilioModule module = (FacilioModule) moduleDetails.get("sourceModule");
            List<FacilioField> allFields = (List<FacilioField>) moduleDetails.get("fields");
            Map<String, FacilioField> allFieldsMap = FieldFactory.getAsMap(allFields);
            List<String> numberFileFields = (List<String>) moduleDetails.getOrDefault("fileFields", new ArrayList<>());
            Map<String, Map<String, Object>> numberLookUps = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");
            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            LOGGER.info("####Data Migration - ReadingModule - Started for ModuleName - " + moduleName);

            int offset = 0;
            int limit = 5000;

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
                        List<Map<String, Object>> dataFromCSV = SandboxDataMigrationUtil.getDataFromCSV(module, moduleFileName, allFieldsMap, numberFileFields, offset, limit + 1);

                        if (CollectionUtils.isEmpty(dataFromCSV)) {
                            LOGGER.info("####Data Migration - ReadingModule - No Records obtained from CSV - " + moduleName);
                            isModuleMigrated = true;
                        } else {
                            if (dataFromCSV.size() > limit) {
                                dataFromCSV.remove(limit);
                            } else {
                                isModuleMigrated = true;
                            }
                            LOGGER.info("####Data Migration - ReadingModule - In progress - " + moduleName + " - Offset - " + offset);

                            List<Map<String, Object>> propsToInsert = sanitizeProps(dataMigrationId, migrationBean, module, allFieldsMap, dataFromCSV, numberLookUps);

                            Map<Long, Long> oldVsNewIds = migrationBean.createModuleDataWithModuleName(module, allFields, propsToInsert, addLogger);

                            // Add oldVsNewId Mapping
                            migrationBean.addIntoDataMappingTableWithModuleName(dataMigrationId, moduleName, oldVsNewIds);

                            offset = offset + dataFromCSV.size();
                        }

                        SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.READING_MODULE_DATA_INSERTION, module.getModuleId(), moduleFileName, offset);

                        if ((System.currentTimeMillis() - transactionStartTime) > transactionTimeOut) {
                            LOGGER.info("####Data Migration - ReadingModule - Stopped after exceeding transaction timeout with ModuleName - " + moduleName + " Offset - " + offset);
                            return true;
                        }
                    } while (!isModuleMigrated);
                }
            } catch (Exception e) {
                SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.READING_MODULE_DATA_INSERTION, module.getModuleId(), moduleFileName, offset);
                LOGGER.info("####Data Migration - ReadingModule - Error occurred in ModuleName - " + moduleName, e);
                context.put(DataMigrationConstants.ERROR_OCCURRED, true);
                return true;
            }

            LOGGER.info("####Data Migration - ReadingModule - Completed for ModuleName - " + moduleName);
        }

        SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_DATA_INSERTION, null, null, 0);
        dataMigrationObj = migrationBean.getDataMigrationStatus(dataMigrationObj.getId());

        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);
        return false;
    }

    private static List<Map<String, Object>> sanitizeProps(long dataMigrationId, DataMigrationBean migrationBean, FacilioModule module, Map<String, FacilioField> fieldsMap,
                                                           List<Map<String, Object>> propsList, Map<String, Map<String, Object>> numberLookUps) throws Exception {

        List<Map<String, Object>> propsForEvaluation = new ArrayList<>();
        List<String> fieldsToParse = new ArrayList<>(numberLookUps.keySet());
        Map<Long, List<Long>> moduleIdVsOldIds = SandboxDataMigrationUtil.getModuleIdVsOldIds(module.getModuleId(), propsList, fieldsMap, numberLookUps, fieldsToParse);
        Map<Long, Map<Long, Long>> oldIdVsNewIdMapping = SandboxDataMigrationUtil.getOldIdVsNewIdMapping(migrationBean, dataMigrationId, moduleIdVsOldIds);

        for (Map<String, Object> prop : propsList) {
            Map<String, Object> updatedProp = new HashMap<>();
            Long id = (Long) prop.get("id");
            updatedProp.put("id", id);

            for (Map.Entry<String, Object> entry : prop.entrySet()) {
                Object value = entry.getValue();
                String fieldName = entry.getKey();
                FacilioField fieldObj = fieldsMap.get(fieldName);

                if (value != null) {
                    if (fieldObj != null && numberLookUps.containsKey(fieldName)) {
                        SandboxDataMigrationUtil.updateLookupData(fieldObj, value, updatedProp, numberLookUps, oldIdVsNewIdMapping, new HashMap<>());
                    } else {
                        updatedProp.put(fieldName, value);
                    }
                }
            }
            propsForEvaluation.add(updatedProp);
        }

        List<Map<String, Object>> propsToInsert = getPropsToInsert(dataMigrationId, migrationBean, module, new ArrayList<>(fieldsMap.values()), propsForEvaluation);
        updateReadingDataId(propsToInsert, dataMigrationId, migrationBean);
        return propsToInsert;
    }

    private static List<Map<String, Object>> getPropsToInsert(long dataMigrationId, DataMigrationBean migrationBean, FacilioModule module,
                                                              List<FacilioField> fieldsList, List<Map<String, Object>> propsForEvaluation) throws Exception {
        List<Map<String, Object>> propsToInsert = new ArrayList<>();

        int fromIndex = 0;
        int toIndex = Math.min(propsForEvaluation.size(), 250);

        List<Map<String, Object>> propsSubListFromCSV;
        while (fromIndex < propsForEvaluation.size()) {
            propsSubListFromCSV = propsForEvaluation.subList(fromIndex, toIndex);
            List<Map<String, Object>> currOrgReadingMetaData = getCurrOrgReadingMetaData(module, fieldsList, propsSubListFromCSV);

            // Check and add data
            Map<Long, Map<Long, Long>> csvResourceIdVsFieldIdVsRecordId = getResourceIdVsFieldIdVsRecordId(propsSubListFromCSV);
            Map<Long, Map<Long, Long>> currOrgResourceIdVsFieldIdVsRecordId = getResourceIdVsFieldIdVsRecordId(currOrgReadingMetaData);
            Map<Long, Map<String, Object>> idVsPropFromCSV = propsSubListFromCSV.stream().collect(Collectors.toMap(prop -> (long) prop.get("id"), prop -> prop, (a, b) -> b));

            List<Long> idsToInsert = new ArrayList<>();
            Map<Long, Long> oldVsNewIdMapping = new HashMap<>();

            if (MapUtils.isNotEmpty(csvResourceIdVsFieldIdVsRecordId)) {
                for (Map.Entry<Long, Map<Long, Long>> resourceIdVsFieldIdVsRecordId : csvResourceIdVsFieldIdVsRecordId.entrySet()) {
                    long resourceId = resourceIdVsFieldIdVsRecordId.getKey();
                    for (Map.Entry<Long, Long> fieldIdVsRecordId : resourceIdVsFieldIdVsRecordId.getValue().entrySet()) {
                        if (currOrgResourceIdVsFieldIdVsRecordId.containsKey(resourceId) && currOrgResourceIdVsFieldIdVsRecordId.get(resourceId).containsKey(fieldIdVsRecordId.getKey())) {
                            oldVsNewIdMapping.put(fieldIdVsRecordId.getValue(), currOrgResourceIdVsFieldIdVsRecordId.get(resourceId).get(fieldIdVsRecordId.getKey()));
                        } else {
                            idsToInsert.add(fieldIdVsRecordId.getValue());
                        }
                    }
                }
            }

            // Add new record
            for (Long id : idsToInsert) {
                propsToInsert.add(idVsPropFromCSV.get(id));
            }

            // Add oldVsNewId mapping
            migrationBean.addIntoDataMappingTableWithModuleName(dataMigrationId, module.getName(), oldVsNewIdMapping);

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), propsForEvaluation.size());
        }

        return propsToInsert;
    }

    private static Map<Long, List<Long>> getResourceIdVsFieldIdsMap(List<Map<String, Object>> propsList) {
        Map<Long, List<Long>> resourceIdVsFieldIds = new HashMap<>();
        if (CollectionUtils.isNotEmpty(propsList)) {
            for (Map<String, Object> prop : propsList) {
                Long fieldId = (Long) prop.getOrDefault("fieldId", -1L);
                Long resourceId = (Long) prop.getOrDefault("resourceId", -1L);

                if (fieldId > 0 && resourceId > 0) {
                    resourceIdVsFieldIds.computeIfAbsent(resourceId, k -> new ArrayList<>());
                    resourceIdVsFieldIds.get(resourceId).add(fieldId);
                }
            }
        }
        return resourceIdVsFieldIds;
    }

    private static Map<Long, Map<Long, Long>> getResourceIdVsFieldIdVsRecordId(List<Map<String, Object>> props) {
        Map<Long, Map<Long, Long>> resourceIdVsFieldIdVsOldRecordId = new HashMap<>();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                Long id = (Long) prop.getOrDefault("id", -1L);
                Long fieldId = (Long) prop.getOrDefault("fieldId", -1L);
                Long resourceId = (Long) prop.getOrDefault("resourceId", -1L);

                if (fieldId > 0 && resourceId > 0) {
                    resourceIdVsFieldIdVsOldRecordId.computeIfAbsent(resourceId, k -> new HashMap<>());
                    resourceIdVsFieldIdVsOldRecordId.get(resourceId).put(fieldId, id);
                }
            }
        }
        return resourceIdVsFieldIdVsOldRecordId;
    }

    private static List<Map<String, Object>> getCurrOrgReadingMetaData(FacilioModule module, List<FacilioField> fieldsList, List<Map<String, Object>> propsList) throws Exception {
        List<Map<String, Object>> currOrgPropsList = new ArrayList<>();
        Map<Long, List<Long>> resourceIdVsFieldIds = getResourceIdVsFieldIdsMap(propsList);
        if (MapUtils.isNotEmpty(resourceIdVsFieldIds)) {
            Criteria criteria = new Criteria();
            for (Map.Entry<Long, List<Long>> entry : resourceIdVsFieldIds.entrySet()) {
                Criteria subCriteria = new Criteria();
                subCriteria.addAndCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", String.valueOf(entry.getKey()), NumberOperators.EQUALS));
                subCriteria.addAndCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", StringUtils.join(entry.getValue(), ","), NumberOperators.EQUALS));
                criteria.orCriteria(subCriteria);
            }

            currOrgPropsList = getModuleData(module, fieldsList, criteria);
        }

        return currOrgPropsList;
    }

    private static List<Map<String, Object>> getModuleData(FacilioModule module, List<FacilioField> allFields, Criteria moduleCriteria) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(allFields)
                .table(module.getTableName());

        if (moduleCriteria != null && !moduleCriteria.isEmpty()) {
            selectBuilder.andCriteria(moduleCriteria);
        }

        List<Map<String, Object>> propsList = selectBuilder.get();
        return propsList;
    }

    private static void updateReadingDataId(List<Map<String, Object>> propsList, long dataMigrationId, DataMigrationBean migrationBean) throws Exception {
        if (CollectionUtils.isNotEmpty(propsList)) {
            Map<Long, List<Long>> moduleIdVsOldIds = new HashMap<>();
            for (Map<String, Object> prop : propsList) {
                FacilioModule readingFieldModule = prop.containsKey("##ReadingFieldModule##") ? (FacilioModule) prop.get("##ReadingFieldModule##") : null;
                long readingFieldModuleId = readingFieldModule != null ? readingFieldModule.getModuleId() : -1;
                long readingDataId = prop.containsKey("readingDataId") ? (long) prop.get("readingDataId") : -1;

                if (readingDataId > 0 && readingFieldModuleId > 0) {
                    moduleIdVsOldIds.computeIfAbsent(readingFieldModuleId, k -> new ArrayList<>());
                    moduleIdVsOldIds.get(readingFieldModuleId).add(readingDataId);
                }
            }

            if (MapUtils.isNotEmpty(moduleIdVsOldIds)) {
                Map<Long, Map<Long, Long>> oldIdVsNewIdMapping = SandboxDataMigrationUtil.getOldIdVsNewIdMapping(migrationBean, dataMigrationId, moduleIdVsOldIds);
                if (MapUtils.isNotEmpty(oldIdVsNewIdMapping)) {
                    for (Map<String, Object> prop : propsList) {
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
