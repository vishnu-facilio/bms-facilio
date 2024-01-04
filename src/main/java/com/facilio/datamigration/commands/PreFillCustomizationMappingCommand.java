package com.facilio.datamigration.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class PreFillCustomizationMappingCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(PreFillCustomizationMappingCommand.class);
    @Override
    public boolean executeCommand(Context context) throws Exception {

        DataMigrationStatusContext dataMigrationContext = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        if(dataMigrationContext.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.CUSTOMIZATION_MAPPING.getIndex()) {
            return false;
        }

        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);

        Map<String, String> setupCustomizedModulesVsFieldName = new HashMap<>();

        setupCustomizedModulesVsFieldName.put("alarmseverity", "severity");
        setupCustomizedModulesVsFieldName.put("assetcategory", "name");
        setupCustomizedModulesVsFieldName.put("assetdepartment", "name");
        setupCustomizedModulesVsFieldName.put("assettype", "name");
        setupCustomizedModulesVsFieldName.put("energymeterpurpose", "name");
        setupCustomizedModulesVsFieldName.put("itemStatus", "name");
        setupCustomizedModulesVsFieldName.put("readingalarmcategory", "name");
        setupCustomizedModulesVsFieldName.put("servicerequestpriority", "priority");
        setupCustomizedModulesVsFieldName.put("spacecategory", "name");
        setupCustomizedModulesVsFieldName.put("ticketcategory", "name");
        setupCustomizedModulesVsFieldName.put("ticketpriority", "priority");
        setupCustomizedModulesVsFieldName.put("ticketstatus", "displayName");
        setupCustomizedModulesVsFieldName.put("tickettype", "name");
        setupCustomizedModulesVsFieldName.put("toolStatus", "name");
        setupCustomizedModulesVsFieldName.put("workpermittype", "type");
        setupCustomizedModulesVsFieldName.put("weatherservice", "name");
        setupCustomizedModulesVsFieldName.put("weatherstation", "name");
        setupCustomizedModulesVsFieldName.put("inspectionPriority", "priority");
        setupCustomizedModulesVsFieldName.put("peopleGroup", "name");

        DataMigrationBean sourceConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);
        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);
        ModuleBean sourceModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        ModuleBean targetModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", targetOrgId);

        targetConnection.updateDataMigrationStatus(dataMigrationContext.getId(), DataMigrationStatusContext.DataMigrationStatus.CUSTOMIZATION_MAPPING, null, 0);

        for(Map.Entry<String,String> moduleNameVsPrimaryField : setupCustomizedModulesVsFieldName.entrySet()) {
            String moduleName = moduleNameVsPrimaryField.getKey();
            String fieldName = moduleNameVsPrimaryField.getValue();
            LOGGER.info("PreFillCustomizationMapping started for module : "+moduleName);
            FacilioModule sourceModule = sourceModuleBean.getModule(moduleName);
            List<FacilioField> sourceFields = sourceModuleBean.getAllFields(moduleName);

            FacilioModule targetModule = targetModuleBean.getModule(moduleName);//(FacilioModule) moduleDetails.get("targetModule");
            List<FacilioField> targetFields = targetModuleBean.getAllFields(moduleName);

            List<Map<String, Object>> sourceData = getModuleRecords(sourceConnection, sourceModule, sourceFields);
            List<Map<String, Object>> targetData = getModuleRecords(targetConnection, targetModule, targetFields);
            Map<Long, Long> oldVsNewIds = new HashMap<>();

            if(moduleName.equals("ticketstatus")) {
                Set<Long> statusModuleIds = sourceData.stream().filter(m->m.get("parentModuleId") != null).map(m -> (Long)m.get("parentModuleId")).collect(Collectors.toSet());

                Criteria typeCriteria = new Criteria();
                typeCriteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", StringUtils.join(statusModuleIds, ","), NumberOperators.EQUALS));
                List<FacilioModule> modules = sourceModuleBean.getModuleList(typeCriteria);
                Map<Long, String> sourceIdVsName = modules.stream().collect(Collectors.toMap(FacilioModule::getModuleId, FacilioModule::getName));

                Set<Long> targetStatusModuleIds = targetData.stream().filter(m->m.get("parentModuleId") != null).map(m -> (Long)m.get("parentModuleId")).collect(Collectors.toSet());
                typeCriteria = new Criteria();
                typeCriteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", StringUtils.join(targetStatusModuleIds, ","), NumberOperators.EQUALS));
                List<FacilioModule> targetModules = targetModuleBean.getModuleList(typeCriteria);
                Map<Long, String> targetIdVsName = targetModules.stream().collect(Collectors.toMap(FacilioModule::getModuleId, FacilioModule::getName));

                Map<String, Map<String, Long>> moduleNameVsStatusVsId = getConvertedStatusDetails(sourceData, sourceIdVsName);
                Map<String, Map<String, Long>> targetmoduleNameVsStatusVsId = getConvertedStatusDetails(targetData, targetIdVsName);

                for(Map.Entry<String, Map<String, Long>> moduleVsStatusDetails : moduleNameVsStatusVsId.entrySet()) {
                    Map<String,Long> targetStatusVsId = targetmoduleNameVsStatusVsId.get(moduleVsStatusDetails.getKey());
                    if(MapUtils.isNotEmpty(targetStatusVsId)) {
                        for (Map.Entry<String, Long> entryValue : moduleVsStatusDetails.getValue().entrySet()) {
                            if (targetStatusVsId.containsKey(entryValue.getKey())) {
                                oldVsNewIds.put(entryValue.getValue(), targetStatusVsId.get(entryValue.getKey()));
                            }
                        }
                    }
                }
            }
            else {
                Map<String, Long> sourceNameVsId = sourceData.stream().collect(Collectors.toMap(m -> (String) m.get(fieldName), m -> (Long) m.get("id"), (a, b) -> a));
                Map<String, Long> targetNameVsId = targetData.stream().collect(Collectors.toMap(m -> (String) m.get(fieldName), m -> (Long) m.get("id"), (a, b) -> a));

                for (Map.Entry<String, Long> entryValue : sourceNameVsId.entrySet()) {
                    if (targetNameVsId.containsKey(entryValue.getKey())) {
                        oldVsNewIds.put(entryValue.getValue(), targetNameVsId.get(entryValue.getKey()));
                    }
                }
            }
            if (MapUtils.isNotEmpty(oldVsNewIds)) {
                targetConnection.addIntoDataMappingTable(dataMigrationContext.getId(), targetModule.getModuleId(), oldVsNewIds);
            }
            LOGGER.info("PreFillCustomizationMapping completed for module : "+moduleName);
        }

        return false;
    }

    private static List<Map<String,Object>> getModuleRecords(DataMigrationBean connection, FacilioModule module, List<FacilioField> fields) throws Exception {
        int limit = 5000;
        int offset = 0;
        boolean sourceDataFetched = false;
        List<Map<String, Object>> resultProps = new ArrayList<>();
        do {
            List<Map<String, Object>> props = new ArrayList<>();
            try {
                props = connection.getModuleData(module, fields, null, offset, limit + 1, null, null);
            } catch(Exception e) {
                sourceDataFetched = true;
                LOGGER.error("Get record error for module : "+module.getName(), e);
                continue;
            }

            if(CollectionUtils.isEmpty(props)) {
                sourceDataFetched = true;
            }
            else {
                if (props.size() > limit) {
                    props.remove(limit);
                } else {
                    sourceDataFetched = true;
                }
                resultProps.addAll(props);
            }
        } while (!sourceDataFetched);
        return resultProps;
    }

    private static Map<String, Map<String, Long>> getConvertedStatusDetails(List<Map<String, Object>> statusData, Map<Long, String> moduleIdVsName) throws Exception {
        Map<String, Map<String, Long>> moduleNameVsStatusVsId = statusData.stream()
                .filter(m -> m.get("parentModuleId") != null)
                .collect(Collectors.groupingBy(
                        sourceStatus -> moduleIdVsName.get((Long) sourceStatus.get("parentModuleId")),
                        Collectors.toMap(
                                sourceStatus -> (String) sourceStatus.get("displayName"),
                                sourceStatus -> (Long) sourceStatus.get("id"),
                                (id1, id2) -> id1,
                                HashMap::new
                        )
                ));
        return moduleNameVsStatusVsId;
    }
}
