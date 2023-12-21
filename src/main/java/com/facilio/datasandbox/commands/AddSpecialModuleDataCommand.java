package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class AddSpecialModuleDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        if (dataMigrationObj.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_IN_PROGRESS.getIndex()) {
            return false;
        }

        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        long dataMigrationId = (long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1l);
        long transactionStartTime = (long) context.get(DataMigrationConstants.TRANSACTION_START_TIME);
        long transactionTimeOut = (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_TIME_OUT, 500000l);
        List<String> logModulesNames = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);
        Map<String, String> moduleNameVsXmlFileName = (Map<String, String>) context.get(DataMigrationConstants.MODULE_NAMES_XML_FILE_NAME);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        boolean moduleMigrationStarted = true;
        String lastModuleName = dataMigrationObj.getLastModuleName();
        if (StringUtils.isNotEmpty(lastModuleName)) {
            moduleMigrationStarted = false;
        } else {
            migrationBean.updateDataMigrationStatusWithModuleName(dataMigrationId, DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_IN_PROGRESS, null, 0);
        }

        Map<String, List<String>> parentModuleVsChildModules = SandboxModuleConfigUtil.getParentModuleVsChildModules();
        List<String> specialModules = parentModuleVsChildModules.get(FacilioConstants.ContextNames.TASK);

        for (String moduleName : specialModules) {
            if (!SandboxModuleConfigUtil.SPECIAL_MODULENAME_VS_DETAILS.containsKey(moduleName)) {
                continue;
            }
            Map<String, Object> moduleDetails = SandboxModuleConfigUtil.SPECIAL_MODULENAME_VS_DETAILS.get(moduleName);

            if (!moduleMigrationStarted && !moduleName.equals(lastModuleName)) {
                continue;
            } else {
                moduleMigrationStarted = true;
            }

            String moduleFileName = moduleNameVsXmlFileName.get(moduleName);
            FacilioModule module = (FacilioModule) moduleDetails.get("sourceModule");
            List<FacilioField> allFields = (List<FacilioField>) moduleDetails.get("fields");
            Map<String, FacilioField> allFieldsMap = FieldFactory.getAsMap(allFields);
            List<String> numberFileFields = (List<String>) moduleDetails.getOrDefault("fileFields", new ArrayList<>());
            Map<String, Map<String, Object>> numberLookUps = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");
            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            LOGGER.info("####Data Migration - SpecialModule - Started for ModuleName - " + moduleName);

            int offset = 0;
            int limit = 5000;
            if (moduleName.equals(lastModuleName) && offset < dataMigrationObj.getMigratedCount()) {
                offset = (int) dataMigrationObj.getMigratedCount();
            }

            boolean isModuleMigrated = false;

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

                migrationBean.updateDataMigrationStatus(dataMigrationObj.getId(), DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_IN_PROGRESS, module.getModuleId(), offset);

                if ((System.currentTimeMillis() - transactionStartTime) > transactionTimeOut) {
                    LOGGER.info("####Data Migration - SpecialModule - Stopped after exceeding transaction timeout with ModuleName - " + moduleName + " Offset - " + offset);
                    return true;
                }
            } while (!isModuleMigrated);

            LOGGER.info("####Data Migration - SpecialModule - Completed for ModuleName - " + moduleName);
        }

        migrationBean.updateDataMigrationStatus(dataMigrationObj.getId(), DataMigrationStatusContext.DataMigrationStatus.UPDATION_IN_PROGRESS, null, 0);
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
