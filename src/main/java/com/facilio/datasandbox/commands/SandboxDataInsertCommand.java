package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
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
        if(dataMigrationObj.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.CREATION_IN_PROGRESS.getIndex()) {
            return false;
        }

        long packageId = (long) context.get(DataMigrationConstants.PACKAGE_ID);
        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        long dataMigrationId = (long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1l);
        List<String> logModulesNames = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);
        long transactionStartTime = (long) context.get(DataMigrationConstants.TRANSACTION_START_TIME);
        long transactionTimeOut = (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_TIME_OUT, 500000l);
        List<String> skipDataMigrationModules = (List<String>) context.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES);
        Map<String, String> moduleNameVsXmlFileName = (Map<String, String>) context.get(DataMigrationConstants.MODULE_NAMES_XML_FILE_NAME);
        List<String> updateOnlyModulesList = (List<String>) context.getOrDefault(DataMigrationConstants.UPDATE_ONLY_MODULES, new ArrayList<>());
        Map<ComponentType, Map<Long, Long>> componentTypeVsOldVsNewId = (Map<ComponentType, Map<Long, Long>>) context.get(DataMigrationConstants.PACKAGE_CHANGE_SET);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        String lastModuleName = null;
        boolean moduleMigrationStarted = true;
        long lastMigratedModuleId = dataMigrationObj.getLastModuleId();

        if(lastMigratedModuleId > 0) {
            moduleMigrationStarted = false;
            lastModuleName = modBean.getModule(lastMigratedModuleId).getName();
        } else {
            migrationBean.updateDataMigrationStatus(dataMigrationObj.getId(), DataMigrationStatusContext.DataMigrationStatus.CREATION_IN_PROGRESS, null, 0);
        }
        
        Map<Long, Long> siteIdMapping = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();

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
            List<Long> childModuleIds = new ArrayList<>();
            List<String> numberFileFields = new ArrayList<>();
            Map<String, Map<String, Object>> numberLookUps = new HashMap<>();

            if (MapUtils.isNotEmpty(moduleDetails) && moduleDetails.get("sourceModule") != null) {
                module = (FacilioModule) moduleDetails.get("sourceModule");
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

            Map<String, String> nonNullableFieldVsLookupModules = DataMigrationUtil.getNonNullableColumns(module);
            if (module.getTypeEnum() == FacilioModule.ModuleType.ACTIVITY) {
                FacilioModule parentModule = modBean.getParentModule(module.getModuleId());
                parentModule = (moduleName.equals(FacilioConstants.ContextNames.CUSTOM_ACTIVITY)) ? module : parentModule;

                nonNullableFieldVsLookupModules.put("parentId", parentModule.getName());
                nonNullableFieldVsLookupModules.put("doneBy", FacilioConstants.ContextNames.USERS);
            } else if (module.getTypeEnum() == FacilioModule.ModuleType.READING) {
                FacilioModule parentModuleForReadingModule = SandboxModuleConfigUtil.getParentModuleForSubModule(module, null);
                if (parentModuleForReadingModule != null) {
                    nonNullableFieldVsLookupModules.put("parentId", parentModuleForReadingModule.getName());
                }
            } else if (module.getTypeEnum() == FacilioModule.ModuleType.TIME_LOG && !nonNullableFieldVsLookupModules.containsKey("parent")) {
                FacilioModule parentModuleForSubModule = SandboxModuleConfigUtil.getParentModuleForSubModule(module, FacilioModule.ModuleType.BASE_ENTITY);
                if (parentModuleForSubModule != null) {
                    nonNullableFieldVsLookupModules.put("parent", parentModuleForSubModule.getName());
                }
            } else if (module.getTypeEnum() == FacilioModule.ModuleType.NOTES || module.getTypeEnum() == FacilioModule.ModuleType.ATTACHMENTS) {
                FacilioModule parentModuleForSubModule = SandboxModuleConfigUtil.getParentModuleForSubModule(module, null);
                if (parentModuleForSubModule != null) {
                    String parentFieldName = fieldsMap.keySet().stream().filter(fieldName -> fieldName.equals("parentId")).findFirst().orElse(null);
                    if (StringUtils.isEmpty(parentFieldName)) {
                        parentFieldName = fieldsMap.keySet().stream().filter(fieldName -> fieldName.contains("parent")).findFirst().orElse(null);
                    }
                    if (StringUtils.isNotEmpty(parentFieldName)) {
                        nonNullableFieldVsLookupModules.put(parentFieldName, parentModuleForSubModule.getName());
                    }
                }
            }

            List<Long> extendedModuleIds = module.getExtendedModuleIds();
            String moduleFileName = moduleNameVsXmlFileName.get(moduleName);
            List<SupplementRecord> supplementRecords = DataMigrationUtil.getSupplementFields(allFields);
            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            int offset = 0;
            int limit = 5000;
            if (moduleName.equals(lastModuleName) && offset < dataMigrationObj.getMigratedCount()) {
                offset = (int) dataMigrationObj.getMigratedCount();
            }

            boolean isModuleMigrated = false;
            SandboxModuleConfigUtil.addSystemFields(module, fieldsMap);

            do {
                try {

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
                } } catch (Exception e) {
                    isModuleMigrated = true;
                    LOGGER.info("#################################################################", e);
                }
                migrationBean.updateDataMigrationStatus(dataMigrationObj.getId(), DataMigrationStatusContext.DataMigrationStatus.CREATION_IN_PROGRESS, module.getModuleId(), offset);

                if ((System.currentTimeMillis() - transactionStartTime) > transactionTimeOut) {
                    LOGGER.info("####Data Migration - Insert - Stopped after exceeding transaction timeout with ModuleName - " + moduleName + " Offset - " + offset);
                    return true;
                }
            } while (!isModuleMigrated);

            LOGGER.info("####Data Migration - Insert - Completed for ModuleName - " + moduleName);
        }

        migrationBean.updateDataMigrationStatusWithModuleName(dataMigrationId, DataMigrationStatusContext.DataMigrationStatus.READING_MODULE_IN_PROGRESS, null, 0);
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
                            Long newId = SandboxDataMigrationUtil.getMetaConfNewId(FacilioConstants.ContextNames.STATE_FLOW_ID, (Long) value, componentTypeVsOldVsNewId);
                            updatedProp.put(fieldName, newId);
                        } else if (fieldName.equals("sysCreatedBy") || fieldName.equals("sysModifiedBy")) {
                            SandboxDataMigrationUtil.updateLookupData(fieldObj, value, updatedProp, numberLookUps, oldIdVsNewIdMapping, componentTypeVsOldVsNewId);
                        } else if (fieldObj != null && (fieldObj.getDataTypeEnum() == FieldType.LOOKUP || fieldObj.getDataTypeEnum() == FieldType.MULTI_LOOKUP) ||
                                MapUtils.isNotEmpty(numberLookUps) && numberLookUps.containsKey(fieldName)) {
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
