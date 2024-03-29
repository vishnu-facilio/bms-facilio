package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datasandbox.context.ModuleCSVFileContext;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Log4j
public class AddSpecialModuleDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        if (dataMigrationObj.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_DATA_INSERTION.getIndex()) {
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
            SandboxDataMigrationUtil.updateDataMigrationContextWithModuleName(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_DATA_INSERTION, null, null, 0);
        }

        Map<String, List<String>> parentModuleVsChildModules = SandboxModuleConfigUtil.getParentModuleVsChildModules();
        List<String> specialModules = parentModuleVsChildModules.get(FacilioConstants.ContextNames.TASK);

        for (String moduleName : specialModules) {
            if ((CollectionUtils.isNotEmpty(moduleSequenceList) && !moduleSequenceList.contains(moduleName)) ||
                    !SandboxModuleConfigUtil.SPECIAL_MODULENAME_VS_DETAILS.containsKey(moduleName)) {
                continue;
            }
            Map<String, Object> moduleDetails = SandboxModuleConfigUtil.SPECIAL_MODULENAME_VS_DETAILS.get(moduleName);

            if (!moduleMigrationStarted && !moduleName.equals(lastModuleName)) {
                continue;
            } else {
                moduleMigrationStarted = true;
            }

            String moduleFileName = null;
            Stack<ModuleCSVFileContext> moduleCSVFileContexts = moduleNameVsCSVFileContexts.get(moduleName);
            FacilioModule module = (FacilioModule) moduleDetails.get("sourceModule");
            List<FacilioField> allFields = (List<FacilioField>) moduleDetails.get("fields");
            Map<String, FacilioField> allFieldsMap = FieldFactory.getAsMap(allFields);
            List<String> numberFileFields = (List<String>) moduleDetails.getOrDefault("fileFields", new ArrayList<>());
            Map<String, Map<String, Object>> numberLookUps = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");
            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            LOGGER.info("####Data Migration - SpecialModule - Started for ModuleName - " + moduleName);

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
                            LOGGER.info("####Data Migration - SpecialModule - No Records obtained from CSV - " + moduleName);
                            isModuleMigrated = true;
                        } else {
                            if (dataFromCSV.size() > limit) {
                                dataFromCSV.remove(limit);
                            } else {
                                isModuleMigrated = true;
                            }
                            LOGGER.info("####Data Migration - SpecialModule - In progress - " + moduleName + " - Offset - " + offset);

                            List<Map<String, Object>> propsToInsert = sanitizeProps(dataMigrationId, migrationBean, module, allFieldsMap, dataFromCSV, numberLookUps);

                            Map<Long, Long> oldVsNewIds = migrationBean.createModuleDataWithModuleName(module, allFields, propsToInsert, addLogger);

                            // Add oldVsNewId Mapping
                            migrationBean.addIntoDataMappingTableWithModuleName(dataMigrationId, moduleName, oldVsNewIds);

                            offset = offset + dataFromCSV.size();
                        }

                        SandboxDataMigrationUtil.updateDataMigrationContextWithModuleName(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_DATA_INSERTION, module.getName(), moduleFileName, offset);

                        if ((System.currentTimeMillis() - transactionStartTime) > transactionTimeOut) {
                            LOGGER.info("####Data Migration - SpecialModule - Stopped after exceeding transaction timeout with ModuleName - " + moduleName + " Offset - " + offset);
                            return true;
                        }
                    } while (!isModuleMigrated);
                }
            } catch (Exception e) {
                SandboxDataMigrationUtil.updateDataMigrationContextWithModuleName(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_DATA_INSERTION, module.getName(), moduleFileName, offset);
                LOGGER.info("####Data Migration - SpecialModule - Error occurred in ModuleName - " + moduleName, e);
                context.put(DataMigrationConstants.ERROR_OCCURRED, true);
                return true;
            }

            LOGGER.info("####Data Migration - SpecialModule - Completed for ModuleName - " + moduleName);
        }

        SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_UPDATION, null, null, 0);
        dataMigrationObj = migrationBean.getDataMigrationStatus(dataMigrationObj.getId());

        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);
        return false;
    }

    private static List<Map<String, Object>> sanitizeProps(long dataMigrationId, DataMigrationBean migrationBean, FacilioModule module, Map<String, FacilioField> fieldsMap,
                                                           List<Map<String, Object>> propsList, Map<String, Map<String, Object>> numberLookUps) throws Exception {

        List<Map<String, Object>> propsToInsert = new ArrayList<>();
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
                    if (numberLookUps.containsKey(fieldName) && fieldObj != null) {
                        SandboxDataMigrationUtil.updateLookupData(fieldObj, value, updatedProp, numberLookUps, oldIdVsNewIdMapping, new HashMap<>());
                    } else {
                        updatedProp.put(fieldName, value);
                    }
                }
            }
            propsToInsert.add(updatedProp);
        }

        return propsToInsert;
    }
}
