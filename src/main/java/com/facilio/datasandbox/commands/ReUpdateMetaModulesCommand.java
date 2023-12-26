package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import com.facilio.fw.BeanFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class ReUpdateMetaModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        if (dataMigrationObj.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.REUPDATE_META_MODULES.getIndex()) {
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

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        String lastModuleName = null;
        boolean moduleMigrationStarted = true;
        long lastMigratedModuleId = dataMigrationObj.getLastModuleId();

        if (lastMigratedModuleId > 0) {
            moduleMigrationStarted = false;
            lastModuleName = modBean.getModule(lastMigratedModuleId).getName();
        } else {
            migrationBean.updateDataMigrationStatus(dataMigrationObj.getId(), DataMigrationStatusContext.DataMigrationStatus.REUPDATE_META_MODULES, null, 0);
        }

        for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();

            if (!updateOnlyModulesList.contains(moduleName)) {
                continue;
            }

            if (!moduleMigrationStarted && !moduleName.equals(lastModuleName)) {
                continue;
            } else {
                moduleMigrationStarted = true;
            }

            if (skipDataMigrationModules.contains(moduleName)) {
                LOGGER.info("####Data Migration - MetaModule Update - Skipped for ModuleName - " + moduleName);
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
                LOGGER.info("####Data Migration - MetaModule Update - Module not found for ModuleName - " + moduleName);
                continue;
            }

            LOGGER.info("####Data Migration - MetaModule Update - Started for ModuleName - " + moduleName);

            String moduleFileName = moduleNameVsXmlFileName.get(moduleName);
            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(allFields);
            allFields.addAll(SandboxModuleConfigUtil.getSysDeletedFields(module, fieldsMap));
            List<SupplementRecord> supplementRecords = DataMigrationUtil.getSupplementFields(allFields);
            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            int offset = 0;
            int limit = 5000;
            if (moduleName.equals(lastModuleName) && offset < dataMigrationObj.getMigratedCount()) {
                offset = (int) dataMigrationObj.getMigratedCount();
            }

            boolean isModuleMigrated = false;
            Map<Long, Long> siteIdMapping = new HashMap<>();
            SandboxModuleConfigUtil.addSystemFields(module, fieldsMap);

            try {
                do {
                    List<Map<String, Object>> dataFromCSV = SandboxDataMigrationUtil.getDataFromCSV(module, moduleFileName, fieldsMap, numberFileFields, offset, limit + 1);

                    if (CollectionUtils.isEmpty(dataFromCSV)) {
                        LOGGER.info("####Data Migration - MetaModule Update - No Records obtained from CSV - " + moduleName);
                        isModuleMigrated = true;
                    } else {
                        if (dataFromCSV.size() > limit) {
                            dataFromCSV.remove(limit);
                        } else {
                            isModuleMigrated = true;
                        }
                        LOGGER.info("####Data Migration - MetaModule Update - In progress - " + moduleName + " - Offset - " + offset);

                        if (MapUtils.isEmpty(siteIdMapping) || moduleName.equals(FacilioConstants.ContextNames.SITE)) {
                            FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
                            siteIdMapping = migrationBean.getOldVsNewId(dataMigrationId, siteModule.getModuleId(), null);
                        }

                        List<Map<String, Object>> propsToUpdate = sanitizePropsBeforeUpdate(module, fieldsMap, dataFromCSV, componentTypeVsOldIdVsNewId,
                                numberLookUps, numberFileFields, siteIdMapping, dataMigrationId, migrationBean);

                        if (CollectionUtils.isNotEmpty(propsToUpdate)) {
                            migrationBean.updateModuleData(module, new ArrayList<>(fieldsMap.values()), supplementRecords, propsToUpdate, addLogger);
                        }

                        offset = offset + dataFromCSV.size();
                    }
                    migrationBean.updateDataMigrationStatus(dataMigrationObj.getId(), DataMigrationStatusContext.DataMigrationStatus.REUPDATE_META_MODULES, module.getModuleId(), offset);

                    if ((System.currentTimeMillis() - transactionStartTime) > transactionTimeOut) {
                        LOGGER.info("####Data Migration - MetaModule Update - Stopped after exceeding transaction timeout with ModuleName - " + moduleName + " Offset - " + offset);
                        return true;
                    }
                } while (!isModuleMigrated);
            } catch (Exception e) {
                migrationBean.updateDataMigrationStatus(dataMigrationObj.getId(), DataMigrationStatusContext.DataMigrationStatus.REUPDATE_META_MODULES, module.getModuleId(), offset);
                LOGGER.info("####Data Migration - MetaModule Update - Error occurred in ModuleName - " + moduleName, e);
                return true;
            }

            LOGGER.info("####Data Migration - MetaModule Update - Completed for ModuleName - " + moduleName);
        }

        migrationBean.updateDataMigrationStatus(dataMigrationId, DataMigrationStatusContext.DataMigrationStatus.COMPLETED, null, 0);
        dataMigrationObj = migrationBean.getDataMigrationStatus(dataMigrationObj.getId());

        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);
        return false;
    }

    private static List<Map<String, Object>> sanitizePropsBeforeUpdate(FacilioModule module, Map<String, FacilioField> fieldsMap, List<Map<String, Object>> propsList,
                                                                       Map<ComponentType, Map<Long, Long>> componentTypeVsOldVsNewId, Map<String, Map<String, Object>> numberLookUps,
                                                                       List<String> numberFileFields, Map<Long, Long> siteIdMapping, long dataMigrationId,
                                                                       DataMigrationBean migrationBean) throws Exception {

        List<String> fieldsToParse = new ArrayList<>(fieldsMap.keySet());
        Map<Long, List<Long>> moduleIdVsOldIds = SandboxDataMigrationUtil.getModuleIdVsOldIds(module.getModuleId(), propsList, fieldsMap, numberLookUps, fieldsToParse);
        Map<Long, Map<Long, Long>> oldIdVsNewIdMapping = SandboxDataMigrationUtil.getOldIdVsNewIdMapping(migrationBean, dataMigrationId, moduleIdVsOldIds);

        List<Map<String, Object>> propsToUpdate = new ArrayList<>();
        for (Map<String, Object> prop : propsList) {
            boolean newIdCreated = false;
            Map<String, Object> updatedProp = new HashMap<>();
            for (Map.Entry<String, Object> entry : prop.entrySet()) {
                Object value = entry.getValue();
                String fieldName = entry.getKey();
                FacilioField fieldObj = fieldsMap.get(fieldName);

                if (value != null) {
                    // File Field
                    if ((fieldObj != null && fieldObj.getDataTypeEnum() == FieldType.FILE) || (CollectionUtils.isNotEmpty(numberFileFields) && numberFileFields.contains(fieldName))) {
                        Map<String, Object> fileProp = (Map<String, Object>) value;
                        if (MapUtils.isNotEmpty(fileProp)) {
                            String fileFieldPropName = (fieldObj != null && fieldObj.getDataTypeEnum() == FieldType.FILE) ? fieldName + "Id" : fieldName;
                            long newFileId = DataPackageFileUtil.getNewFileId(fileProp);
                            updatedProp.put(fileFieldPropName, newFileId);
                        }
                        continue;
                    }

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
                    } else if (fieldName.equals(FacilioConstants.ContextNames.FORM_ID)) {
                        Long newId = SandboxDataMigrationUtil.getMetaConfNewId(FacilioConstants.ContextNames.FORM_ID, (Long) value, componentTypeVsOldVsNewId);
                        updatedProp.put(fieldName, newId);
                    } else if (fieldName.equals(FacilioConstants.ContextNames.STATE_FLOW_ID)) {
                        Long newId = SandboxDataMigrationUtil.getMetaConfNewId(FacilioConstants.ContextNames.STATE_FLOW_ID, (Long) value, componentTypeVsOldVsNewId);
                        updatedProp.put(fieldName, newId);
                    } else if (fieldName.equals(FacilioConstants.ContextNames.SITE_ID) && MapUtils.isNotEmpty(siteIdMapping)) {
                        updatedProp.put(fieldName, siteIdMapping.get((Long) prop.get(fieldName)));
                    } else if (fieldName.equals("site") && MapUtils.isNotEmpty(siteIdMapping)) {
                        Map<String, Long> site = (Map<String, Long>) prop.get(fieldName);
                        if (siteIdMapping.containsKey(site.get("id"))) {
                            site.put("id", siteIdMapping.get(site.get("id")));
                            updatedProp.put(fieldName, site);
                        }
                    } else if (fieldName.equals("slaPolicyId")) {
                        Long newId = SandboxDataMigrationUtil.getMetaConfNewId(FacilioConstants.ContextNames.SLA_POLICY_ID, (Long) value, componentTypeVsOldVsNewId);
                        updatedProp.put(fieldName, newId);
                    } else if (fieldName.equals("parentModuleId")) {
                        Long newId = SandboxDataMigrationUtil.getMetaConfNewId(FacilioConstants.ContextNames.MODULE, (Long) value, componentTypeVsOldVsNewId);
                        updatedProp.put(fieldName, newId);
                    } else if (fieldName.equals("approvalRuleId") || fieldName.equals("approvalFlowId")) {
                        Long newId = SandboxDataMigrationUtil.getMetaConfNewId(FacilioConstants.ApprovalRule.APPROVAL_RULE_ID_FIELD_NAME, (Long) value, componentTypeVsOldVsNewId);
                        updatedProp.put(fieldName, newId);
                    } else if (fieldsToParse.contains(fieldName) && fieldObj != null) {
                        SandboxDataMigrationUtil.updateLookupData(fieldObj, value, updatedProp, numberLookUps, oldIdVsNewIdMapping, componentTypeVsOldVsNewId);
                    } else {
                        updatedProp.put(fieldName, value);
                    }
                }
            }
            if (newIdCreated) {
                propsToUpdate.add(updatedProp);
            }
        }

        return propsToUpdate;
    }
}
