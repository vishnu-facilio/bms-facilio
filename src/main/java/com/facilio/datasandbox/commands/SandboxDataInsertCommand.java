package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.datasandbox.context.ModuleCSVFileContext;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class SandboxDataInsertCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        if(dataMigrationObj.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_INSERTION.getIndex()) {
            return false;
        }

        long packageId = (long) context.get(DataMigrationConstants.PACKAGE_ID);
        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        long dataMigrationId = (long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1l);
        List<String> logModulesNames = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);
        long transactionStartTime = (long) context.get(DataMigrationConstants.TRANSACTION_START_TIME);
        long transactionTimeOut = (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_TIME_OUT, 500000l);
        List<String> skipDataMigrationModules = (List<String>) context.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES);
        List<String> updateOnlyModulesList = (List<String>) context.getOrDefault(DataMigrationConstants.UPDATE_ONLY_MODULES, new ArrayList<>());
        Map<ComponentType, Map<Long, Long>> componentTypeVsOldVsNewId = (Map<ComponentType, Map<Long, Long>>) context.get(DataMigrationConstants.PACKAGE_CHANGE_SET);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);
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
            SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_INSERTION, null, null, 0);
        }
        
        Map<Long, Long> siteIdMapping = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();
            Stack<ModuleCSVFileContext> moduleCSVFileContexts = moduleNameVsCSVFileContexts.get(moduleName);

            if (!moduleMigrationStarted && !moduleName.equals(lastModuleName)) {
                continue;
            } else {
                moduleMigrationStarted = true;
            }

            if (updateOnlyModulesList.contains(moduleName) || skipDataMigrationModules.contains(moduleName) || SandboxModuleConfigUtil.SPECIAL_MODULENAME_VS_DETAILS.containsKey(moduleName)) {
                LOGGER.info("####Data Migration - Insert - Skipped for ModuleName - " + moduleName);
                continue;
            }

            FacilioModule module = null;
            String moduleFileName = null;
            Criteria moduleCriteria = null;
            List<Long> childModuleIds = new ArrayList<>();
            List<String> numberFileFields = new ArrayList<>();
            Map<String, Map<String, Object>> numberLookUps = new HashMap<>();

            if (MapUtils.isNotEmpty(moduleDetails) && moduleDetails.get("sourceModule") != null) {
                module = (FacilioModule) moduleDetails.get("sourceModule");
                moduleCriteria = (Criteria) moduleDetails.get("criteria");
                childModuleIds = (List<Long>) moduleDetails.get("childModuleIds");
                numberFileFields = (List<String>) moduleDetails.get("fileFields");
                numberLookUps = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");
            } else {
                module = modBean.getModule(moduleName);
            }

            if (module == null) {
                LOGGER.info("####Data Migration - Insert - Module not found for ModuleName - " + moduleName);
                continue;
            }

            LOGGER.info("####Data Migration - Insert - Started for ModuleName - " + moduleName);

            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(allFields);
            allFields.addAll(SandboxModuleConfigUtil.getSysDeletedFields(module, fieldsMap));

            Map<String, String> nonNullableFieldVsLookupModules = DataMigrationUtil.getNonNullableColumns(module);
            DataMigrationUtil.getModuleTypeWiseNonNullableColumns(module, fieldsMap, nonNullableFieldVsLookupModules);

            List<Long> extendedModuleIds = module.getExtendedModuleIds();
            List<SupplementRecord> supplementRecords = DataMigrationUtil.getSupplementFields(allFields);
            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            int offset = 0;
            int limit = 5000;

            SandboxModuleConfigUtil.addSystemFields(module, fieldsMap);

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
                        List<Map<String, Object>> dataFromCSV = SandboxDataMigrationUtil.getDataFromCSV(module, moduleFileName, fieldsMap, numberFileFields, offset, limit + 1);

                        if (CollectionUtils.isEmpty(dataFromCSV)) {
                            LOGGER.info("####Data Migration - Insert - No Records obtained from CSV - " + moduleName);
                            isModuleMigrated = true;
                        } else {
                            if (dataFromCSV.size() > limit) {
                                dataFromCSV.remove(limit);
                            } else {
                                isModuleMigrated = true;
                            }
                            LOGGER.info("####Data Migration - Insert - In progress - " + moduleName + " - Offset - " + offset);

                            // Evaluate User Criteria & Skip Records
                            SandboxModuleConfigUtil.evaluateModuleSkipCriteria(dataFromCSV, moduleCriteria);

                            if (MapUtils.isEmpty(siteIdMapping) || moduleName.equals(FacilioConstants.ContextNames.SITE)) {
                                FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
                                siteIdMapping = migrationBean.getOldVsNewId(dataMigrationId, siteModule.getModuleId(), null);
                            }

                            List<Map<String, Object>> propsToInsert = sanitizePropsBeforeInsert(module, fieldsMap, dataFromCSV, componentTypeVsOldVsNewId,
                                    numberLookUps, nonNullableFieldVsLookupModules, childModuleIds, numberFileFields,
                                    siteIdMapping, dataMigrationId, migrationBean);

                            Map<Long, Long> oldVsNewIds = migrationBean.createModuleData(module, allFields, supplementRecords, propsToInsert, addLogger);

                            // Add oldVsNewId Mapping
                            migrationBean.addIntoDataMappingTable(dataMigrationId, module.getModuleId(), oldVsNewIds);

                            // Add Mapping to extended modules
                            if (CollectionUtils.isNotEmpty(extendedModuleIds)) {
                                for (Long moduleId : extendedModuleIds) {
                                    if (moduleId != module.getModuleId()) {
                                        migrationBean.addIntoDataMappingTable(dataMigrationId, moduleId, oldVsNewIds);
                                    }
                                }
                            }

                            offset = offset + dataFromCSV.size();
                        }
                        SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_INSERTION, module.getModuleId(), moduleFileName, offset);

                        if ((System.currentTimeMillis() - transactionStartTime) > transactionTimeOut) {
                            LOGGER.info("####Data Migration - Insert - Stopped after exceeding transaction timeout with ModuleName - " + moduleName + " Offset - " + offset);
                            return true;
                        }
                    } while (!isModuleMigrated);
                }
            } catch (Exception e) {
                SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_INSERTION, module.getModuleId(), moduleFileName, offset);
                LOGGER.info("####Data Migration - Insert - Error occurred in ModuleName - " + moduleName, e);
                context.put(DataMigrationConstants.ERROR_OCCURRED, true);
                return true;
            }

            LOGGER.info("####Data Migration - Insert - Completed for ModuleName - " + moduleName);
        }

        SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.INSTALLATION_COMPLETED, null, null, 0);
        dataMigrationObj = migrationBean.getDataMigrationStatus(dataMigrationObj.getId());

        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);

        return false;
    }

    private static List<Map<String, Object>> sanitizePropsBeforeInsert(FacilioModule module, Map<String, FacilioField> fieldsMap, List<Map<String, Object>> propsList,
                                                                       Map<ComponentType, Map<Long, Long>> componentTypeVsOldVsNewId, Map<String, Map<String, Object>> numberLookUps,
                                                                       Map<String, String> nonNullableFieldVsLookupModules, List<Long> childModuleIds, List<String> numberFileFields,
                                                                       Map<Long, Long> siteIdMapping, long dataMigrationId, DataMigrationBean migrationBean) throws Exception {

        List<String> nonNullableFieldNames = new ArrayList<>();
        Map<Long, Map<Long, Long>> oldIdVsNewIdMapping = new HashMap<>();
        if (MapUtils.isNotEmpty(nonNullableFieldVsLookupModules)) {
            nonNullableFieldNames = new ArrayList<>(nonNullableFieldVsLookupModules.keySet());
            Map<Long, List<Long>> moduleIdVsOldIds = SandboxDataMigrationUtil.getModuleIdVsOldIds(module.getModuleId(), propsList, fieldsMap, numberLookUps, nonNullableFieldNames);
            oldIdVsNewIdMapping = SandboxDataMigrationUtil.getOldIdVsNewIdMapping(migrationBean, dataMigrationId, moduleIdVsOldIds);

            if (module.getName().equals(FacilioConstants.ContextNames.CUSTOM_ACTIVITY) && moduleIdVsOldIds.containsKey(module.getModuleId())) {
                // "parentId" column refers to CustomModuleData RecordIds, to fetch oldIdVsNewIdMapping, Use all Custom ModuleIds
                List<Long> customModuleIds = null;
                List<FacilioModule> customModules = Constants.getModBean().getModuleList(FacilioModule.ModuleType.valueOf(FacilioModule.ModuleType.BASE_ENTITY.getValue()), true, null, null);
                if (CollectionUtils.isNotEmpty(customModules)) {
                    customModuleIds = customModules.stream().map(FacilioModule::getModuleId).collect(Collectors.toList());
                }
                Map<Long, Long> oldVsNewIdForCustomModules = migrationBean.getOldVsNewIdForCustomModules(dataMigrationId, module.getModuleId(), customModuleIds, moduleIdVsOldIds.get(module.getModuleId()));
                oldIdVsNewIdMapping.computeIfAbsent(module.getModuleId(), k -> new HashMap<>());
                oldIdVsNewIdMapping.get(module.getModuleId()).putAll(oldVsNewIdForCustomModules);
            }
        }

        List<Long> oldIds = propsList.stream().map(m -> (Long) m.get("id")).collect(Collectors.toList());
        // Records for Parent Modules (with child modules -- eg. Tickets & Workorder) are created while creating Child Module Records itself
        Map<Long, Long> oldIdVsNewIdMapFromChildModules = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(childModuleIds)) {
            for (Long childModuleId : childModuleIds) {
                oldIdVsNewIdMapFromChildModules.putAll(migrationBean.getOldVsNewId(dataMigrationId, childModuleId, oldIds));
            }
        }

        List<Map<String, Object>> propsToInsert = new ArrayList<>();

        for (Map<String, Object> prop : propsList) {
            Map<String, Object> updatedProp = new HashMap<>();
            Long id = (Long) prop.get("id");
            updatedProp.put("id", id);

            if (oldIdVsNewIdMapFromChildModules.containsKey(id)) {
                updatedProp.put("##Insert_Only_ID_Mapping##", new HashMap<Long, Long>() {{
                    put(id, oldIdVsNewIdMapFromChildModules.get(id));
                }});
            } else {
                for (Map.Entry<String, Object> entry : prop.entrySet()) {
                    Object value = entry.getValue();
                    String fieldName = entry.getKey();
                    FacilioField fieldObj = fieldsMap.get(fieldName);

                    if (value != null) {
                        if (nonNullableFieldNames.contains(fieldName) && fieldObj != null) {
                            SandboxDataMigrationUtil.updateLookupData(fieldObj, value, updatedProp, numberLookUps, oldIdVsNewIdMapping, componentTypeVsOldVsNewId);
                            continue;
                        }

                        if (fieldName.equals("orgId") || fieldName.equals("moduleId")) {
                            updatedProp.remove(fieldName);
                            continue;
                        }

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

                        // Handle System Fields
                        if (fieldName.equals(FacilioConstants.ContextNames.FORM_ID)) {
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
                        } else if (fieldObj != null && SandboxModuleConfigUtil.SYSTEM_USER_PEOPLE_LOOKUP_FIELDS.contains(fieldName)) {
                            SandboxDataMigrationUtil.updateLookupData(fieldObj, value, updatedProp, numberLookUps, oldIdVsNewIdMapping, componentTypeVsOldVsNewId);
                        } else if (fieldObj != null && (fieldObj.getDataTypeEnum() == FieldType.LOOKUP || fieldObj.getDataTypeEnum() == FieldType.MULTI_LOOKUP ||
                                MapUtils.isNotEmpty(numberLookUps) && numberLookUps.containsKey(fieldName))) {
                            updatedProp.put(fieldName, null);
                        } else {
                            updatedProp.put(fieldName, value);
                        }
                    }
                }
            }
            propsToInsert.add(updatedProp);
        }

        return propsToInsert;
    }
}
