package com.facilio.datasandbox.commands;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class HandlePickListTypeModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        if(dataMigrationObj.getStatusEnum().getIndex() > DataMigrationStatusContext.DataMigrationStatus.CUSTOMIZATION_MAPPING_IN_PROGRESS.getIndex()) {
            return false;
        }
        
        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        List<String> logModulesNames = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);
        Map<String, String> moduleNameVsXmlFileName = (Map<String, String>) context.get(DataMigrationConstants.MODULE_NAMES_XML_FILE_NAME);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        Map<String, String> pickListModuleNameVsFieldName = SandboxModuleConfigUtil.unhandledPickListModuleNameVsFieldName();

        for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();

            if (!pickListModuleNameVsFieldName.containsKey(moduleName)) {
                continue;
            }

            FacilioModule module = null;

            if (MapUtils.isNotEmpty(moduleDetails) && moduleDetails.get("sourceModule") != null) {
                module = (FacilioModule) moduleDetails.get("sourceModule");
            } else {
                module = modBean.getModule(moduleName);
            }

            if (module == null) {
                LOGGER.info("####Data Migration - Picklist - Module not found for ModuleName - " + moduleName);
                continue;
            }

            LOGGER.info("####Data Migration - Picklist - Started for ModuleName - " + moduleName);

            String moduleFileName = moduleNameVsXmlFileName.get(moduleName);
            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            String uniqueFieldName = pickListModuleNameVsFieldName.get(moduleName);
            Map<String, FacilioField> allFieldsMap = FieldFactory.getAsMap(allFields);
            allFields.addAll(SandboxModuleConfigUtil.getSysDeletedFields(module, allFieldsMap));
            boolean addLogger = (org.apache.commons.collections4.CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            SandboxModuleConfigUtil.addSystemFields(module, allFieldsMap);

            List<Map<String, Object>> currOrgData = getModuleRecordsForCurrOrg(migrationBean, module, allFields);
            List<Map<String, Object>> moduleRecordsFromCSV = getModuleRecordsFromCSV(module, moduleFileName, allFieldsMap);
            Map<Long, Map<String, Object>> idVsRecordFromCSV = currOrgData.stream().collect(Collectors.toMap(m -> (long) m.get("id"), Function.identity()));

            Map<Long, Long> oldVsNewIdMapping = new HashMap<>();
            List<Map<String, Object>> propsToInsert = new ArrayList<>();

            Map<String, Long> currOrgDataUniqueFieldNameVsId = currOrgData.stream().collect(Collectors.toMap(m -> (String) m.get(uniqueFieldName), m -> (Long) m.get("id"), (a, b) -> a));
            Map<String, Long> csvDataUniqueFieldNameVsId = moduleRecordsFromCSV.stream().collect(Collectors.toMap(m -> (String) m.get(uniqueFieldName), m -> (Long) m.get("id"), (a, b) -> a));

            for (Map.Entry<String, Long> entry : csvDataUniqueFieldNameVsId.entrySet()) {
                if (currOrgDataUniqueFieldNameVsId.containsKey(entry.getKey())) {
                    oldVsNewIdMapping.put(entry.getValue(), currOrgDataUniqueFieldNameVsId.get(entry.getKey()));
                } else {
                    propsToInsert.add(idVsRecordFromCSV.get(entry.getValue()));
                }
            }
            
            if (CollectionUtils.isNotEmpty(propsToInsert)) {
                Map<Long, Long> oldVsNewIds = migrationBean.createModuleData(module, allFields, null, propsToInsert, addLogger);
                oldVsNewIdMapping.putAll(oldVsNewIds);
            }

            if (org.apache.commons.collections.MapUtils.isNotEmpty(oldVsNewIdMapping)) {
                migrationBean.addIntoDataMappingTable(dataMigrationObj.getId(), module.getModuleId(), oldVsNewIdMapping);
            }

            LOGGER.info("####Data Migration - Picklist - Started for ModuleName - " + moduleName);
        }

        migrationBean.updateDataMigrationStatus(dataMigrationObj.getId(), DataMigrationStatusContext.DataMigrationStatus.CREATION_IN_PROGRESS, null, 0);
        dataMigrationObj = migrationBean.getDataMigrationStatus(dataMigrationObj.getId());

        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);

        return false;
    }

    private static List<Map<String, Object>> getModuleRecordsFromCSV(FacilioModule module, String moduleFileName, Map<String, FacilioField> allFieldsMap) throws Exception {
        int offset = 0;
        int limit = 5000;
        boolean isModuleMigrated = false;
        String moduleName = module.getName();
        List<Map<String, Object>> resultProps = new ArrayList<>();

        do {
            List<Map<String, Object>> dataFromCSV = SandboxDataMigrationUtil.getDataFromCSV(module, moduleFileName, allFieldsMap, new ArrayList<>(), offset, limit + 1);

            if (org.apache.commons.collections4.CollectionUtils.isEmpty(dataFromCSV)) {
                LOGGER.info("####Data Migration - Picklist - No Records obtained from CSV - " + moduleName);
                isModuleMigrated = true;
            } else {
                if (dataFromCSV.size() > limit) {
                    dataFromCSV.remove(limit);
                } else {
                    isModuleMigrated = true;
                }
                LOGGER.info("####Data Migration - Picklist - In progress - " + moduleName + " - Offset - " + offset);

                resultProps.addAll(dataFromCSV);
                offset = offset + dataFromCSV.size();
            }
        } while (!isModuleMigrated);

        LOGGER.info("####Data Migration - Picklist - Completed for ModuleName -" + moduleName);

        return resultProps;
    }

    private static List<Map<String, Object>> getModuleRecordsForCurrOrg(DataMigrationBean migrationBean, FacilioModule module, List<FacilioField> fields) throws Exception {
        int offset = 0;
        int limit = 5000;
        boolean isDataFetched = false;
        String moduleName = module.getName();
        List<Map<String, Object>> resultProps = new ArrayList<>();

        do {
            List<Map<String, Object>> props;
            try {
                props = migrationBean.getModuleData(module, fields, null, offset, limit + 1, null, null);
            } catch (Exception e) {
                LOGGER.error("####Data Migration - Picklist Fetch - Error while fetching records for ModuleName -  " + moduleName, e);
                isDataFetched = true;
                continue;
            }

            if (CollectionUtils.isEmpty(props)) {
                isDataFetched = true;
            } else {
                LOGGER.info("####Data Package - Picklist Fetch - In progress for ModuleName - " + moduleName + " - Offset - " + offset);

                if (props.size() > limit) {
                    props.remove(limit);
                } else {
                    isDataFetched = true;
                }
                resultProps.addAll(props);
            }
        } while (!isDataFetched);

        LOGGER.info("####Data Package - Picklist Fetch - Completed for ModuleName - " + moduleName);

        return resultProps;
    }
}
