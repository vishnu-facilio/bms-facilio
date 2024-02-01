package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.datasandbox.context.ModuleCSVFileContext;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class UpdateDataCSVFilesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO - Handle Pause & Continue flow
        boolean getDependantModuleData = (boolean) context.get(DataMigrationConstants.GET_DEPENDANT_MODULE_DATA);
        DataMigrationStatusContext dataMigrationObj = DataMigrationConstants.getDataMigrationStatusContext(context);
        if (!getDependantModuleData || DataMigrationConstants.hasCompletedCurrentStep(context, DataMigrationStatusContext.DataMigrationStatus.DEPENDENT_MODULE_DATA_CSV_CREATION)) {
            SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_DATA_CSV_CREATION, null, null, 0);
            return false;
        }

        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        Map<String, List<Long>> fetchedRecords = (Map<String, List<Long>>) context.get(PackageConstants.FETCHED_RECORDS);
        Map<String, List<Long>> toBeFetchRecords = (Map<String, List<Long>>) context.get(PackageConstants.TO_BE_FETCH_RECORDS);
        boolean fetchDeletedRecords = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_DELETED_RECORDS, false);
        Map<String, Stack<ModuleCSVFileContext>> moduleNameVsCsvFileContext = (Map<String, Stack<ModuleCSVFileContext>>) context.get(DataMigrationConstants.MODULENAME_VS_CSV_FILE_CONTEXT);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);

        while (MapUtils.isNotEmpty(toBeFetchRecords)) {
            for (Map.Entry<String, List<Long>> moduleNameVsRecordIs : toBeFetchRecords.entrySet()) {
                String moduleName = moduleNameVsRecordIs.getKey();
                List<Long> toBeFetchRecordIds = moduleNameVsRecordIs.getValue();

                if (CollectionUtils.isEmpty(toBeFetchRecordIds)) {
                    toBeFetchRecords.remove(moduleName);
                    continue;
                }

                Map<String, Object> moduleDetails = migrationModuleNameVsDetails.get(moduleName);
                if (MapUtils.isEmpty(moduleDetails)) {
                    continue;
                }

                FacilioModule module = (FacilioModule) moduleDetails.get("sourceModule");
                if (module.getTypeEnum() == FacilioModule.ModuleType.PICK_LIST) {
                    toBeFetchRecords.remove(moduleName);
                    continue;
                }

                Criteria moduleCriteria = (Criteria) moduleDetails.get("criteria");
                List<String> numberFileFields = (List<String>) moduleDetails.getOrDefault("fileFields", new ArrayList<>());
                Map<String, Map<String, Object>> numberLookUps = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");

                List<FacilioField> allFields = moduleBean.getAllFields(moduleName);
                List<SupplementRecord> sourceSupplements = DataMigrationUtil.getSupplementFields(allFields);

                List<String> fileFieldNamesWithId = allFields.stream().filter(field -> field.getDataTypeEnum().equals(FieldType.FILE)).map(fieldObj -> fieldObj.getName() + "Id").collect(Collectors.toList());
                fileFieldNamesWithId.addAll(numberFileFields);

                int offset = 0;
                int limit = 5000;
                int lastCountInCSVFile = 0;
                boolean isModuleMigrated = false;
                List<Map<String, Object>> propsForCsv = new ArrayList<>();

                if (MapUtils.isEmpty(numberLookUps) && CollectionUtils.isEmpty(allFields)) {
                    LOGGER.info("####Data Package - LookUp Fetch - No Fields found for ModuleName - " + moduleName);
                    continue;
                }

                LOGGER.info("####Data Package - LookUp Fetch - Started for ModuleName - " + moduleName);

                do {
                    List<Map<String, Object>> props = new ArrayList<>();
                    try {
                        props = migrationBean.getModuleData(module, allFields, sourceSupplements, offset, limit + 1, null, moduleCriteria, toBeFetchRecordIds, fetchDeletedRecords);
                    } catch (Exception e) {
                        LOGGER.error("####Data Package - LookUp Fetch - Error while fetching records for ModuleName - " + moduleName, e);
                        isModuleMigrated = true;
                        continue;
                    }
                    if (CollectionUtils.isEmpty(props)) {
                        isModuleMigrated = true;
                    } else {
                        LOGGER.info("####Data Package - LookUp Fetch - In progress for ModuleName - " + moduleName + " - Offset - " + offset);

                        if (props.size() > limit) {
                            props.remove(limit);
                        } else {
                            isModuleMigrated = true;
                        }

                        propsForCsv.addAll(props);
                        offset = offset + props.size();

                        // module data csv creation
                        if (isModuleMigrated || propsForCsv.size() == DataMigrationConstants.MAX_RECORDS_PER_ITERATION) {
                            SandboxDataMigrationUtil.addDataPropsToCSVFile(moduleNameVsCsvFileContext, module, new ArrayList<>(allFields), propsForCsv,
                                    fileFieldNamesWithId, true, fetchedRecords, toBeFetchRecords, numberLookUps, context);
                            lastCountInCSVFile = SandboxDataMigrationUtil.getMigratedRecordCount(moduleNameVsCsvFileContext.get(moduleName));
                            propsForCsv = new ArrayList<>();
                        }
                    }
                } while (!isModuleMigrated);

                LOGGER.info("####Data Package - LookUp Fetch - Completed for ModuleName - " + moduleName);

                if (CollectionUtils.isEmpty(propsForCsv)) {
                    LOGGER.info("####Data Package - LookUp Fetch - No Records found for ModuleName - " + moduleName);
                    continue;
                }

                List<Long> newlyFetchRecordIds = propsForCsv.stream().map(p -> (Long) p.get("id")).collect(Collectors.toList());
                if (fetchedRecords.containsKey(moduleName)) {
                    List<Long> recordIds = fetchedRecords.get(moduleName);
                    recordIds.addAll(newlyFetchRecordIds);
                } else {
                    fetchedRecords.put(moduleName, newlyFetchRecordIds);
                }
                toBeFetchRecords.get(moduleName).removeAll(newlyFetchRecordIds);
            }
        }

        SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.SPECIAL_MODULE_DATA_CSV_CREATION, null, null, 0);
        dataMigrationObj = migrationBean.getDataMigrationStatus(dataMigrationObj.getId());

        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);
        context.put(DataMigrationConstants.MODULENAME_VS_CSV_FILE_CONTEXT, moduleNameVsCsvFileContext);

        return false;
    }
}
