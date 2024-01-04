package com.facilio.datamigration.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
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
import com.facilio.modules.fields.*;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class DataMigrationCreateRecordCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        DataMigrationStatusContext dataMigrationContext = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        if(dataMigrationContext.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_INSERTION.getIndex()) {
            return false;
        }

        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        long transactionStartTime = (long) context.get(DataMigrationConstants.TRANSACTION_START_TIME);
        long transactionTimeOut = (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_TIME_OUT, 500000l);
        List<String> skipModuleNamesList = (List<String>) context.get(DataMigrationConstants.SKIP_MODULES_LIST);
        List<String> logModuleNamesList = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);
        Long superAdminUserId = AccountUtil.getOrgBean(targetOrgId).getSuperAdmin(targetOrgId).getOuid();
        Map<Long, Long> siteMapping = (Map<Long, Long>) context.getOrDefault(DataMigrationConstants.SITE_MAPPING, new HashMap());
        Map<Long, Long> userIdMapping = (Map<Long, Long>) context.get(DataMigrationConstants.USER_ID_MAPPING);
        Map<Long, Long> groupIdMapping = (Map<Long, Long>) context.get(DataMigrationConstants.GROUP_ID_MAPPING);
        Map<Long, Long> roleIdMapping = (Map<Long, Long>) context.get(DataMigrationConstants.ROLE_ID_MAPPING);

        DataMigrationBean sourceConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);
        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);
        ModuleBean sourceModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        ModuleBean targetModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", targetOrgId);

        HashMap<String,Map<String,Object>> orderedModuleNameVsDetails = (LinkedHashMap<String, Map<String, Object>>) context.get("ModulesVsInfo");
        long lastMigratedModuleId = dataMigrationContext.getLastModuleId();
        String lastModuleName = null;
        Boolean moduleMigrationStarted = true;
        if(lastMigratedModuleId > 0) {
            lastModuleName = targetModuleBean.getModule(lastMigratedModuleId).getName();
            moduleMigrationStarted = false;
        }

        for(Map.Entry<String,Map<String,Object>> moduleNameVsDetails : orderedModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            if(!moduleMigrationStarted && !moduleName.equals(lastModuleName)) {
                continue;
            } else {
                moduleMigrationStarted = true;
            }

            LOGGER.info("Migration - Creation started for : "+moduleName);
            if(CollectionUtils.isNotEmpty(skipModuleNamesList) && skipModuleNamesList.contains(moduleName)) {
                LOGGER.info("Migration - Creation - Skipping for moduleName -"+moduleName);
                continue;
            }

            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();
            FacilioModule sourceModule = sourceModuleBean.getModule(moduleName);//FacilioModule) moduleDetails.get("sourceModule");
            List<FacilioField> sourceFields = sourceModuleBean.getAllFields(moduleName);
            Map<String, FacilioField> sourceFieldNameVsFields = sourceFields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity(), (a, b)->b));
            List<SupplementRecord> sourceSupplements = DataMigrationUtil.getSupplementFields(sourceFields);

            FacilioModule targetModule = targetModuleBean.getModule(moduleName);//(FacilioModule) moduleDetails.get("targetModule");
            List<FacilioField> targetFields = targetModuleBean.getAllFields(moduleName);
            Map<String, FacilioField> targetFieldNameVsFields = targetFields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity(), (a, b)->b));
            List<String> targetFileFieldNamesWithId = targetFields.stream().filter(field -> field.getDataTypeEnum().equals(FieldType.FILE)).map(fieldObj -> fieldObj.getName() + "Id").collect(Collectors.toList());
            List<SupplementRecord> targetSupplements = DataMigrationUtil.getSupplementFields(targetFields);

            Map<String, Map<String, Object>> numberLookups = (targetModule.getTypeEnum().equals(FacilioModule.ModuleType.READING))
                                                    ? (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups") :null;

            List<Long> extendedModuleIds = targetModule.getExtendedModuleIds();
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
            do {
                List<Map<String, Object>> props = null;
                try {
                    props = sourceConnection.getModuleData(sourceModule, sourceFields, sourceSupplements, offset, limit + 1, (Set<Long>) siteMapping.keySet(), moduleCriteria);
                } catch(Exception e) {
                    isModuleMigrated = true;
                    LOGGER.error("Get record error for module : "+moduleName, e);
                    continue;
                }
                if(CollectionUtils.isEmpty(props)) {
                    isModuleMigrated = true;
                }
                else {
                    LOGGER.info("Migration - Creation - in progress for moduleName -"+moduleName+" - offset:"+offset);
                    if(props.size() > limit) {
                        props.remove(limit);
                    } else {
                        isModuleMigrated = true;
                    }

                    Map<Long, Map<Long,Long>> moduleIdVsOldNewIdMapping = null;
                    if(MapUtils.isNotEmpty(numberLookups)) {
                        moduleIdVsOldNewIdMapping = new HashMap<>();
                        Map<Long, List<Long>> moduleIdsVsIdsAndOldLookupIds = DataMigrationUtil.getIdsAndLookupIdsToFetch(props, targetModule.getModuleId(), targetFieldNameVsFields, numberLookups);
                        if (MapUtils.isNotEmpty(moduleIdsVsIdsAndOldLookupIds)) {
                            for (Map.Entry<Long, List<Long>> moduleVsIds : moduleIdsVsIdsAndOldLookupIds.entrySet()) {
                                Map<Long, Long> idMappings = targetConnection.getOldVsNewId(dataMigrationContext.getId(), moduleVsIds.getKey(), moduleVsIds.getValue());
                                if (MapUtils.isNotEmpty(idMappings)) {
                                    if (moduleIdVsOldNewIdMapping.containsKey(moduleVsIds.getKey())) {
                                        moduleIdVsOldNewIdMapping.get(moduleVsIds.getKey()).putAll(idMappings);
                                    } else {
                                        moduleIdVsOldNewIdMapping.put(moduleVsIds.getKey(), idMappings);
                                    }
                                }
                            }
                        }
                    }

                    List<Map<String, Object>> propsToInsert = sanitizePropsBeforeInsert(targetOrgId, dataMigrationContext.getId(),
                            targetFieldNameVsFields, sourceFieldNameVsFields, targetFileFieldNamesWithId, props, userIdMapping,
                            superAdminUserId, groupIdMapping, roleIdMapping, sourceConnection, targetConnection, moduleDetails, context,
                            moduleIdVsOldNewIdMapping, numberLookups);

                    Map<Long, Long> oldVsNewIds = targetConnection.createModuleData(targetModule, targetFields, targetSupplements, propsToInsert, addLogger);

                    targetConnection.addIntoDataMappingTable(dataMigrationContext.getId(), targetModule.getModuleId(), oldVsNewIds);
                    if(CollectionUtils.isNotEmpty(extendedModuleIds)) {
                        for(Long moduleId : extendedModuleIds) {
                            if (moduleId == targetModule.getModuleId()) {
                                continue;
                            }
                            targetConnection.addIntoDataMappingTable(dataMigrationContext.getId(), moduleId, oldVsNewIds);
                        }
                    }

                    offset = offset + props.size();
                }
                targetConnection.updateDataMigrationStatus(dataMigrationContext.getId(), DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_INSERTION, targetModule.getModuleId(), offset);

                if(System.currentTimeMillis() - transactionStartTime > transactionTimeOut) {
                    LOGGER.info("Migration - Creation - stopped after exceeding transaction limit with moduleName: "+moduleName+" offset:"+offset);
                    return true;
                }
            } while (!isModuleMigrated);

            LOGGER.info("Migration creation completed for : "+moduleName);
        }

        targetConnection.updateDataMigrationStatus(dataMigrationContext.getId(), DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_UPDATION, null, 0);
        dataMigrationContext = targetConnection.getDataMigrationStatus(dataMigrationContext.getId());
        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationContext);

        return false;
    }

    private List<Map<String, Object>> sanitizePropsBeforeInsert(long targetOrgId, Long migrationId,
                                                                Map<String, FacilioField> targetFieldNameVsFields,
                                                                Map<String, FacilioField> sourceFieldNameVsFields,
                                                                List<String> targetFileFieldNamesWithId,
                                                                List<Map<String, Object>> props,
                                                                Map<Long, Long> userIdMapping,
                                                                Long superAdminUserId,
                                                                Map<Long, Long> groupIdMapping,
                                                                Map<Long, Long> roleIdMapping,
                                                                DataMigrationBean sourceConnection,
                                                                DataMigrationBean targetConnection,
                                                                Map<String, Object> moduleDetails,
                                                                Context context, Map<Long,
                                                                Map<Long, Long>> moduleIdVsOldNewIdMapping,
                                                                Map<String, Map<String, Object>> numberLookups) throws Exception {

        Map<Long, Long> siteMapping = (Map<Long, Long>) context.get(DataMigrationConstants.SITE_MAPPING);
        Map<Long, Long> formIdMapping = (Map<Long, Long>) context.get(DataMigrationConstants.FORM_ID_MAPPING);
        Map<Long, Long> stateFlowIdMapping = (Map<Long, Long>) context.get(DataMigrationConstants.STATEFLOW_ID_MAPPING);
        Map<Long, Long> slaPolicyIdMapping = (Map<Long, Long>) context.get(DataMigrationConstants.SLA_ID_MAPPING);

        List<Long> childModuleIds = (List<Long>) moduleDetails.get("childModuleIds");
        List<Long> oldIds = props.stream().map(m -> (Long)m.get("id")).collect(Collectors.toList());
        Map<Long, Long> childIdMap = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(childModuleIds)) {
            for (Long childModuleId : childModuleIds) {
                childIdMap.putAll(targetConnection.getOldVsNewId(migrationId, childModuleId, oldIds));
            }
        }

        List<String> fileFieldsAsNumber = (List<String>)moduleDetails.get("fileFields");

        List<Map<String, Object>> propsToInsert = new ArrayList<>();
        for (Map<String, Object> prop : props) {
            Map<String, Object> updatedProp = new LinkedHashMap<>();
            Long id = (Long) prop.get("id");
            updatedProp.put("id", id);

            if (childIdMap.containsKey(id)) {
                updatedProp.put("##Insert_Only_ID_Mapping##", new HashMap<Long,Long>(){{put(id, childIdMap.get(id));}});
            } else {
                for (Map.Entry<String, Object> value : prop.entrySet()) {
                    String fieldName = value.getKey();
                    Object data = value.getValue();

                    if (data != null) {
                        if (targetFieldNameVsFields.containsKey(fieldName)
                            && ((CollectionUtils.isEmpty(fileFieldsAsNumber) || (CollectionUtils.isNotEmpty(fileFieldsAsNumber) && !fileFieldsAsNumber.contains(fieldName))))) {
                            FacilioField fieldObj = targetFieldNameVsFields.get(fieldName);
                            FacilioField sourceFieldObj = sourceFieldNameVsFields.get(fieldName);
                            modifyFieldTypeData(fieldObj, sourceFieldObj, fieldName, data, updatedProp, userIdMapping, superAdminUserId, groupIdMapping, roleIdMapping, moduleIdVsOldNewIdMapping, numberLookups);

                        } else if (targetFileFieldNamesWithId.contains(fieldName) || (CollectionUtils.isNotEmpty(fileFieldsAsNumber) && fileFieldsAsNumber.contains(fieldName))) {
                            Long fileId = (Long) data;
                            try {
                                Map<String, Object> fileData = sourceConnection.getFileFromSource(fileId);
                                Long newFileId = targetConnection.addFile(targetOrgId, (byte[]) fileData.get("bytes"), (String) fileData.get(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME), (String) fileData.get(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE));
                                updatedProp.put(fieldName, newFileId);
                            }catch(Exception e) {
                                LOGGER.info("Error while fetching/creating file for fileId::"+fileId);
                            }

                        } else {
                            updatedProp.put(fieldName, data);
                        }

                        if (fieldName.equals("orgId") || fieldName.equals("moduleId")) {
                            updatedProp.remove(fieldName);
                        }
                        if ((fieldName.equals("siteId")) && MapUtils.isNotEmpty(siteMapping) && siteMapping.containsKey((Long) prop.get(fieldName))) {
                            updatedProp.put(fieldName, siteMapping.get((Long) prop.get(fieldName)));
                        }
                        if ((fieldName.equals("site")) && MapUtils.isNotEmpty(siteMapping)) {
                            Map<String, Long> site = (Map<String, Long>) prop.get(fieldName);
                            if(siteMapping.containsKey(site.get("id"))) {
                                site.put("id", siteMapping.get(site.get("id")));
                                updatedProp.put(fieldName, site);
                            }
                        }
                        if (fieldName.equals("formId") && MapUtils.isNotEmpty(formIdMapping)) {
                            updatedProp.put(fieldName, formIdMapping.get((Long) prop.get(fieldName)));
                        }
                        if ((fieldName.equals("stateFlowId")) && MapUtils.isNotEmpty(stateFlowIdMapping)) {
                            updatedProp.put(fieldName, stateFlowIdMapping.get((Long) prop.get(fieldName)));
                        }
                        if (fieldName.equals("slaPolicyId") && MapUtils.isNotEmpty(slaPolicyIdMapping)) {
                            updatedProp.put(fieldName, slaPolicyIdMapping.get((Long) prop.get(fieldName)));
                        }
                    }
                }
            }
            propsToInsert.add(updatedProp);
        }
        return propsToInsert;
    }



                /*
        to update
        urlfield = fieldid, moduleid, parentid
        filefield = get and create a new file
        filefieldname = id,filename,downloadurl,uploadedby,uploadedtime,url,contenttype
        signature = preview url
        syscreatedby
        sysmodifiedby
        picklist  - id fetch and change
        modulestate  - id fetch change
        moduleid - change to new module
        signature - url,uploadedtime,uploadedby,fielname,id,downloadurl,contenttype
        formid - change
        stateflowid - change
        lookup - change
        siteid- change


         */


//                if (module.isCustom()) {
//                    for (ModuleBaseWithCustomFields record : records) {
//                        CommonCommandUtil.handleLookupFormData(fields, record.getData());
//                    }
//                }


    private void modifyFieldTypeData(FacilioField fieldObj, FacilioField sourceFieldObj, String fieldName, Object data, Map<String, Object> updatedProp, Map<Long, Long> userIdMapping, Long superAdminUserId,
                                     Map<Long, Long> groupIdMapping, Map<Long, Long> roleIdMapping,
                                     Map<Long, Map<Long, Long>> moduleIdVsOldNewIdMapping, Map<String, Map<String, Object>> numberLookups) throws Exception {

        switch (fieldObj.getDataTypeEnum()) {
            case NUMBER:
                //Replacing lookupmoduleids only for reading module
                if(MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(fieldName) && MapUtils.isNotEmpty(moduleIdVsOldNewIdMapping)) {
                    Long lookupDataId = (Long) data;
                    if (lookupDataId != null && lookupDataId > 0) {
                        Long parentModuleId = (Long) numberLookups.get(fieldName).get("lookupModuleId");
                        if (moduleIdVsOldNewIdMapping != null && moduleIdVsOldNewIdMapping.containsKey(parentModuleId) &&
                                moduleIdVsOldNewIdMapping.get(parentModuleId).containsKey(lookupDataId)) {
                            updatedProp.put(fieldName, moduleIdVsOldNewIdMapping.get(parentModuleId).get(lookupDataId));
                        }
                    }
                } else {
                    updatedProp.put(fieldName, data);
                }
                break;
            case URL_FIELD:
                if (data instanceof HashMap) {
                    HashMap urldata = (HashMap) data;
                    urldata.remove("id");
                    urldata.remove("parentId");
                    urldata.remove("fieldId");
                    urldata.remove("moduleId");
                    urldata.remove("orgId");
                    updatedProp.put(fieldName, urldata);
                }
                break;
            case ENUM:
                EnumField enumField = (EnumField) sourceFieldObj;
                String enumValue = (String) enumField.getEnumMap().get(data);
                EnumField currentEnumField = (EnumField) fieldObj;
                updatedProp.put(fieldName, currentEnumField.getIndex(enumValue));
                break;
            case MULTI_ENUM:
                if (data instanceof ArrayList) {
                    List<Integer> array = (List<Integer>) data;
                    MultiEnumField sourceField = (MultiEnumField) sourceFieldObj;
                    MultiEnumField currentField = (MultiEnumField) fieldObj;
                    Map<Integer, Object> enumMap = sourceField.getEnumMap();
                    List<Integer> modifiedArr = new ArrayList();
                    for (Integer multiEnumValueIndex : array) {
                        String multiEnumValue = (String) enumMap.get(multiEnumValueIndex);
                        modifiedArr.add(currentField.getIndex(multiEnumValue));
                    }
                    updatedProp.put(fieldName, modifiedArr);
                }
                break;
            case LOOKUP:
                FacilioModule lookupModule = ((LookupField) fieldObj).getLookupModule();
                if ((FacilioConstants.ContextNames.USERS.equals(lookupModule.getName()))) {
                    Map<String, Object> lookupData = (Map<String, Object>) data;
                    Long lookupDataId = (Long) lookupData.get("id");
                    if (userIdMapping != null && userIdMapping.containsKey(lookupDataId)) {
                        lookupData.put("id", userIdMapping.get(lookupDataId));
                    } else {
                        lookupData.put("id", superAdminUserId);
                    }
                    updatedProp.put(fieldName, lookupData);
                } else if(FacilioConstants.ContextNames.GROUPS.equals(lookupModule.getName())) {
                    Map<String, Object> lookupData = (Map<String, Object>) data;
                    Long lookupDataId = (Long) lookupData.get("id");
                    if (MapUtils.isNotEmpty(groupIdMapping) && groupIdMapping.containsKey(lookupDataId)) {
                        lookupData.put("id", groupIdMapping.get(lookupDataId));
                    }
                    updatedProp.put(fieldName, lookupData);
                } else if (FacilioConstants.ContextNames.ROLE.equals(lookupModule.getName())) {
                    Map<String, Object> lookupData = (Map<String, Object>) data;
                    Long lookupDataId = (Long) lookupData.get("id");
                    if (MapUtils.isNotEmpty(roleIdMapping) && roleIdMapping.containsKey(lookupDataId)) {
                        lookupData.put("id", roleIdMapping.get(lookupDataId));
                    }
                    updatedProp.put(fieldName, lookupData);
                } else {
                    updatedProp.put(fieldName, data);
                }
                break;
            case MULTI_LOOKUP:
                FacilioModule multilookupModule = ((MultiLookupField) fieldObj).getLookupModule();
                List<Map<String, Object>> multiLookupData = (List<Map<String, Object>>) data;
                List<Map<String, Object>> updatedMultiLookupData = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(multiLookupData)) {
                    for (Map<String, Object> lookupData : multiLookupData) {
                        if ((FacilioConstants.ContextNames.USERS.equals(multilookupModule.getName()))) {
                            Long lookupDataId = (Long) lookupData.get("id");
                            if (userIdMapping != null && userIdMapping.containsKey(lookupDataId)) {
                                lookupData.put("id", userIdMapping.get(lookupDataId));
                            } else {
                                lookupData.put("id", superAdminUserId);
                            }
                        }
                        updatedMultiLookupData.add(lookupData);
                    }
                    updatedProp.put(fieldName, updatedMultiLookupData);
                } else {
                    updatedProp.put(fieldName, data);
                }
                break;
//            case LINE_ITEM:
//                int i3 = 0;
//                break;
            default:
                updatedProp.put(fieldName, data);
                break;
        }
    }

}
