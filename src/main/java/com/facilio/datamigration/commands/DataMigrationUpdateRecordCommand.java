package com.facilio.datamigration.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.SupplementRecord;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class DataMigrationUpdateRecordCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DataMigrationStatusContext dataMigrationContext = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);

        if(dataMigrationContext.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_UPDATION.getIndex()) {
            return false;
        }

        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        long transactionStartTime = (long) context.get(DataMigrationConstants.TRANSACTION_START_TIME);
        long transactionTimeOut = (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_TIME_OUT, 500000l);
        Long superAdminUserId = AccountUtil.getOrgBean(targetOrgId).getSuperAdmin(targetOrgId).getOuid();
        Map<Long, Long> userIdMapping = (Map<Long, Long>) context.get(DataMigrationConstants.USER_ID_MAPPING);
        Map<Long, Long> siteMapping = (Map<Long, Long>) context.getOrDefault(DataMigrationConstants.SITE_MAPPING, new HashMap());
        List<String> skipModuleNamesList = (List<String>) context.get(DataMigrationConstants.SKIP_MODULES_LIST);
        List<String> logModuleNamesList = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);

        DataMigrationBean sourceConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);
        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);
        ModuleBean sourceModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        ModuleBean targetModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", targetOrgId);

        List<Long> customModuleIds = null;
        List<FacilioModule> customModules = targetModuleBean.getModuleList(FacilioModule.ModuleType.valueOf(FacilioModule.ModuleType.BASE_ENTITY.getValue()), true, null, null);
        if (CollectionUtils.isNotEmpty(customModules)) {
            customModuleIds = customModules.stream().map(FacilioModule::getModuleId).collect(Collectors.toList());
        }

        HashMap<String,Map<String,Object>> orderedModuleNameVsDetails = (LinkedHashMap<String, Map<String, Object>>) context.get("ModulesVsInfo");
        long lastMigratedModuleId = dataMigrationContext.getLastModuleId();
        String lastModuleName = null;
        Boolean moduleMigrationStarted = true;
        if(lastMigratedModuleId > 0) {
            lastModuleName = targetModuleBean.getModule(lastMigratedModuleId).getName();
            moduleMigrationStarted = false;
        } else {
            targetConnection.updateDataMigrationStatus(dataMigrationContext.getId(), DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_UPDATION, null, 0);
        }

        for(Map.Entry<String,Map<String,Object>> moduleNameVsDetails : orderedModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            if(!moduleMigrationStarted && !moduleName.equals(lastModuleName)) {
                continue;
            } else {
                moduleMigrationStarted = true;
            }

            LOGGER.info("Migration - Updation - Started for moduleName -"+moduleName);
            FacilioModule targetModule = targetModuleBean.getModule(moduleName);
            if((CollectionUtils.isNotEmpty(skipModuleNamesList) && skipModuleNamesList.contains(moduleName)) ||
                    targetModule.getTypeEnum().equals(FacilioModule.ModuleType.READING)) {
                LOGGER.info("Migration - Updation - Skipping for moduleName -"+moduleName);
                continue;
            }

            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();
            Map<String, Map<String, Object>> numberLookups = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");
            FacilioModule sourceModule = sourceModuleBean.getModule(moduleName);//FacilioModule) moduleDetails.get("sourceModule");
            List<FacilioField> sourceFields = sourceModuleBean.getAllFields(moduleName);
            sourceFields = sourceFields.stream().filter(field -> (field.getName().equals("id") || field.getDataTypeEnum().equals(FieldType.LOOKUP)
                    || field.getDataTypeEnum().equals(FieldType.MULTI_LOOKUP))
                    || (MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(field.getName())))
                    .collect(Collectors.toList());

            Map<String, FacilioField> sourceFieldNameVsFields = sourceFields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
            List<SupplementRecord> sourceSupplements = DataMigrationUtil.getSupplementFields((Collection<FacilioField>) sourceFieldNameVsFields.values());

            if(CollectionUtils.isEmpty(sourceSupplements) && MapUtils.isEmpty(numberLookups) && (sourceFields.isEmpty() || (CollectionUtils.isNotEmpty(sourceFields) && sourceFields.size() < 1))) {
                LOGGER.info("####Skipped Module - " + moduleName + " source fields - " + sourceFields);
                continue;
            }

            List<FacilioField> targetFields = targetModuleBean.getAllFields(moduleName);
            targetFields = targetFields.stream().filter(field -> (field.getName().equals("id") || field.getDataTypeEnum().equals(FieldType.LOOKUP)
                    || field.getDataTypeEnum().equals(FieldType.MULTI_LOOKUP))
                    || (MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(field.getName())))
                    .collect(Collectors.toList());

            Map<String, FacilioField> targetFieldNameVsFields = targetFields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
            List<SupplementRecord> targetSupplements = DataMigrationUtil.getSupplementFields((Collection<FacilioField>) targetFieldNameVsFields.values());

            Criteria moduleCriteria = null;
            if(moduleDetails.containsKey("criteria")) {
                moduleCriteria = (Criteria) moduleDetails.get("criteria");
            }

            boolean addLogger = (CollectionUtils.isNotEmpty(logModuleNamesList) && logModuleNamesList.contains(moduleName));

            int limit = 5000;
            if(moduleDetails.containsKey("recordsLimit")) {
                limit = (int) moduleDetails.get("recordsLimit");
            }
            int offset = 0;
            if(moduleName.equals(lastModuleName) && offset < dataMigrationContext.getMigratedCount()) {
                offset = (int)dataMigrationContext.getMigratedCount();
            }
            boolean isModuleMigrated = false;
            Map<Long, Map<Long,Long>> moduleIdVsOldNewIdMapping = new HashMap<>();
            Map<String, Map<Long, Long>> fieldDataMapping = new HashMap<>();

            do {
                List<Map<String, Object>> props = null;
                try {
                    if (CollectionUtils.isEmpty(sourceFields) || sourceFields.size() < 1) {
                        break;
                    }
                    props = sourceConnection.getModuleData(sourceModule, sourceFields, sourceSupplements, offset, limit + 1, (Set<Long>) siteMapping.keySet(), moduleCriteria);

                    } catch(Exception e) {
                    isModuleMigrated = true;
                    LOGGER.error("Update Command : Get record error for module : "+moduleName, e);
                    continue;
                }
                if(CollectionUtils.isEmpty(props)) {
                    isModuleMigrated = true;
                }
                else {
                    LOGGER.info("Migration - Updation - in progress for moduleName -"+moduleName+" - offset:"+offset);
                    if(props.size() > limit) {
                        props.remove(limit);
                    } else {
                        isModuleMigrated = true;
                    }
                    Map<Long, List<Long>> moduleIdsVsIdsAndOldLookupIds = getIdsAndLookupIdsToFetch(props, targetModule.getModuleId(), targetFieldNameVsFields, numberLookups);
                    if(MapUtils.isNotEmpty(moduleIdsVsIdsAndOldLookupIds)) {
                        for (Map.Entry<Long, List<Long>> moduleVsIds : moduleIdsVsIdsAndOldLookupIds.entrySet()) {
                            Map<Long, Long> idMappings = targetConnection.getOldVsNewId(dataMigrationContext.getId(), moduleVsIds.getKey(), moduleVsIds.getValue());
                            if (moduleName.equals("customactivity")) {
                                Map<Long, Long> dataMapping = null;
                                dataMapping = targetConnection.getOldVsNewIdForCustomModules(dataMigrationContext.getId(), moduleVsIds.getKey(), customModuleIds, moduleVsIds.getValue());
                                fieldDataMapping.put("parentId", dataMapping);
                            }
                            if(MapUtils.isNotEmpty(idMappings)) {
                                if (moduleIdVsOldNewIdMapping.containsKey(moduleVsIds.getKey())) {
                                    moduleIdVsOldNewIdMapping.get(moduleVsIds.getKey()).putAll(idMappings);
                                } else {
                                    moduleIdVsOldNewIdMapping.put(moduleVsIds.getKey(), idMappings);
                                }
                            }
                        }
                    }

                    List<Map<String, Object>> propsToUpdate = sanitizePropsBeforeUpdate(targetModule.getModuleId(), targetFieldNameVsFields, sourceFieldNameVsFields, props, userIdMapping,
                            superAdminUserId, moduleIdVsOldNewIdMapping, numberLookups, fieldDataMapping);

                    targetConnection.updateModuleData(targetModule, targetFields, targetSupplements, propsToUpdate, addLogger);

                    offset = offset + props.size();
                }
                targetConnection.updateDataMigrationStatus(dataMigrationContext.getId(), DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_UPDATION, targetModule.getModuleId(), offset);

                if(System.currentTimeMillis() - transactionStartTime > transactionTimeOut) {
                    LOGGER.info("Migration - Updation - stopped after exceeding transaction limit with moduleName: "+moduleName+" offset:"+offset);
                    return true;
                }
            } while (!isModuleMigrated);

            LOGGER.info("Migration - Updation - Completed for moduleName -"+moduleName);
        }

        targetConnection.updateDataMigrationStatus(dataMigrationContext.getId(), DataMigrationStatusContext.DataMigrationStatus.INSTALLATION_COMPLETED, null, 0);

        return false;
    }

    private Map<Long, List<Long>> getIdsAndLookupIdsToFetch(List<Map<String, Object>> props, long moduleId,
                                                            Map<String, FacilioField> targetFieldNameVsFields, Map<String, Map<String, Object>> numberLookups) throws Exception {
        Map<Long, List<Long>> moduleIdVsOldRecordIds = new HashMap<>();
        for (Map<String, Object> prop : props) {
            for (Map.Entry<String, Object> value : prop.entrySet()) {
                String fieldName = value.getKey();
                Object data = value.getValue();
                if (data != null) {
                    FacilioField fieldObj = targetFieldNameVsFields.get(fieldName);
                    if(fieldObj != null) {
                        switch (fieldObj.getDataTypeEnum()) {
                            case LOOKUP:
                                FacilioModule lookupModule = ((LookupField) fieldObj).getLookupModule();
                                if (lookupModule.getModuleId() > 0 && MapUtils.isNotEmpty(((Map<String, Object>) data))) {
                                    Long lookupDataId = (Long) ((Map<String, Object>) data).get("id");
                                    if (moduleIdVsOldRecordIds.containsKey(lookupModule.getModuleId())) {
                                        moduleIdVsOldRecordIds.get(lookupModule.getModuleId()).add(lookupDataId);
                                    } else {
                                        moduleIdVsOldRecordIds.put(lookupModule.getModuleId(), new ArrayList<Long>() {{
                                            add(lookupDataId);
                                        }});
                                    }
                                }
                                break;
                            case MULTI_LOOKUP:
                                FacilioModule multilookupModule = ((MultiLookupField) fieldObj).getLookupModule();
                                if (multilookupModule.getModuleId() > 0 && CollectionUtils.isNotEmpty((List<Map<String, Object>>) data)) {
                                    for (Map<String, Object> lookupData : (List<Map<String, Object>>) data) {
                                        Long lookupDataId = (Long) (lookupData).get("id");
                                        if (moduleIdVsOldRecordIds.containsKey(multilookupModule.getModuleId())) {
                                            moduleIdVsOldRecordIds.get(multilookupModule.getModuleId()).add(lookupDataId);
                                        } else {
                                            moduleIdVsOldRecordIds.put(multilookupModule.getModuleId(), new ArrayList<Long>() {{
                                                add(lookupDataId);
                                            }});
                                        }
                                    }
                                }
                                break;
                            case NUMBER:
                                if(MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(fieldName)) {
                                    Long lookupDataId = (Long) data;
                                    if(lookupDataId != null && lookupDataId > 0) {
                                        String lookupModuleName = (String) numberLookups.get(fieldName).get("lookupModuleName");
                                        if (!(FacilioConstants.ContextNames.USERS.equals(lookupModuleName))) {
                                            Long parentModuleId = (Long) numberLookups.get(fieldName).get("lookupModuleId");
                                            if (moduleIdVsOldRecordIds.containsKey(parentModuleId)) {
                                                moduleIdVsOldRecordIds.get(parentModuleId).add(lookupDataId);
                                            } else {
                                                moduleIdVsOldRecordIds.put(parentModuleId, new ArrayList<Long>() {{
                                                    add(lookupDataId);
                                                }});
                                            }
                                        }
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    if(fieldName.equals("id")) {
                        if(moduleIdVsOldRecordIds.containsKey(moduleId)) {
                            moduleIdVsOldRecordIds.get(moduleId).add((long)data);
                        } else {
                            moduleIdVsOldRecordIds.put(moduleId, new ArrayList<Long>(){{add((long)data);}});
                        }
                    }
                }
            }
        }
        return moduleIdVsOldRecordIds;
    }

    private List<Map<String, Object>> sanitizePropsBeforeUpdate(Long moduleId,
                                                                Map<String, FacilioField> targetFieldNameVsFields,
                                                                Map<String, FacilioField> sourceFieldNameVsFields,
                                                                List<Map<String, Object>> props,
                                                                Map<Long, Long> userIdMapping,
                                                                Long superAdminUserId,
                                                                Map<Long, Map<Long, Long>> moduleIdVsOldNewIdMapping, Map<String, Map<String, Object>> numberLookups,
                                                                Map<String, Map<Long, Long>> fieldDataMapping) throws Exception {

        List<Map<String, Object>> propsToUpdate = new ArrayList<>();
        for (Map<String, Object> prop : props) {
            Map<String, Object> updatedProp = new LinkedHashMap<>();
            Long id = (Long) prop.get("id");
            Long newId = moduleIdVsOldNewIdMapping.get(moduleId).get(id);
            if(newId == null || newId <=0) {
                continue;
            }
            updatedProp.put("id", newId);
            Boolean hasLookupValue = false;
            for (Map.Entry<String, Object> value : prop.entrySet()) {
                String fieldName = value.getKey();
                Object data = value.getValue();

                if (!fieldName.equals("id") && data != null && targetFieldNameVsFields.containsKey(fieldName)) {
                    hasLookupValue = true;
                    FacilioField fieldObj = targetFieldNameVsFields.get(fieldName);
                    FacilioField sourceFieldObj = sourceFieldNameVsFields.get(fieldName);
                    modifyFieldTypeData(fieldObj, sourceFieldObj, fieldName, data, updatedProp, userIdMapping, superAdminUserId, moduleIdVsOldNewIdMapping, numberLookups);
                    if (fieldDataMapping.containsKey(fieldName) && fieldDataMapping.get(fieldName) != null) {
                        updatedProp.put(fieldName, fieldDataMapping.get(fieldName).get((Long) data));
                    }
                }
            }
            if(hasLookupValue) {
                propsToUpdate.add(updatedProp);
            }
        }
        return propsToUpdate;
    }

    private void modifyFieldTypeData(FacilioField fieldObj, FacilioField sourceFieldObj, String fieldName, Object data,
                                     Map<String, Object> updatedProp, Map<Long, Long> userIdMapping, Long superAdminUserId,
                                     Map<Long, Map<Long, Long>> moduleIdVsOldNewIdMapping, Map<String, Map<String, Object>> numberLookups) throws Exception {

        switch (fieldObj.getDataTypeEnum()) {
            case LOOKUP:
                FacilioModule lookupModule = ((LookupField) fieldObj).getLookupModule();
                if (!LookupSpecialTypeUtil.isSpecialType(lookupModule.getName())) {
                    Map<String, Object> lookupData = (Map<String, Object>) data;
                    Long lookupDataId = (Long) lookupData.get("id");
                    if (moduleIdVsOldNewIdMapping != null && moduleIdVsOldNewIdMapping.containsKey(lookupModule.getModuleId()) &&
                                moduleIdVsOldNewIdMapping.get(lookupModule.getModuleId()).containsKey(lookupDataId)) {
                        lookupData.put("id", moduleIdVsOldNewIdMapping.get(lookupModule.getModuleId()).get(lookupDataId));
                    } else {
                        lookupData = null;
                    }
                    updatedProp.put(fieldName, lookupData);
                }
                break;
            case MULTI_LOOKUP:
                FacilioModule multilookupModule = ((MultiLookupField) fieldObj).getLookupModule();
                if (!LookupSpecialTypeUtil.isSpecialType(multilookupModule.getName())) {
                    List<Map<String, Object>> multiLookupData = (List<Map<String, Object>>) data;
                    List<Map<String, Object>> updatedMultiLookupData = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(multiLookupData)) {
                        for (Map<String, Object> lookupData : multiLookupData) {
                            Long lookupDataId = (Long) lookupData.get("id");
                            if (moduleIdVsOldNewIdMapping != null && moduleIdVsOldNewIdMapping.containsKey(multilookupModule.getModuleId()) &&
                                    moduleIdVsOldNewIdMapping.get(multilookupModule.getModuleId()).containsKey(lookupDataId)) {
                                lookupData.put("id", moduleIdVsOldNewIdMapping.get(multilookupModule.getModuleId()).get(lookupDataId));
                                updatedMultiLookupData.add(lookupData);
                            }
                        }
                    }
                    updatedProp.put(fieldName, (CollectionUtils.isNotEmpty(updatedMultiLookupData)) ? updatedMultiLookupData : null);
                }
                break;
            case NUMBER:
                if(MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(fieldName)) {
                    Long lookupDataId = (Long) data;
                    if (lookupDataId != null && lookupDataId > 0) {
                        String lookupModuleName = (String) numberLookups.get(fieldName).get("lookupModuleName");
                        Long parentModuleId = (Long) numberLookups.get(fieldName).get("lookupModuleId");
                        if ((FacilioConstants.ContextNames.USERS.equals(lookupModuleName))) {
                            if (userIdMapping != null && userIdMapping.containsKey(lookupDataId)) {
                                lookupDataId = userIdMapping.get(lookupDataId);
                            } else {
                                lookupDataId = superAdminUserId;
                            }
                            updatedProp.put(fieldName, lookupDataId);
                        }
                        else if (moduleIdVsOldNewIdMapping != null && moduleIdVsOldNewIdMapping.containsKey(parentModuleId) &&
                                moduleIdVsOldNewIdMapping.get(parentModuleId).containsKey(lookupDataId)) {
                            updatedProp.put(fieldName, moduleIdVsOldNewIdMapping.get(parentModuleId).get(lookupDataId));
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

}
