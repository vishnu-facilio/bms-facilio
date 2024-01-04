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
public class CreateDataCSVFilesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DataMigrationStatusContext dataMigrationObj = DataMigrationConstants.getDataMigrationStatusContext(context);
        if (DataMigrationConstants.hasCompletedCurrentStep(context, DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_CSV_CREATION)) {
            return false;
        }

        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        int reqOffset = (int) context.getOrDefault(DataMigrationConstants.OFFSET, 0);
        int reqLimit = (int) context.getOrDefault(DataMigrationConstants.LIMIT, 0);
        long transactionTimeOut = (long) context.get(DataMigrationConstants.TRANSACTION_TIME_OUT);
        boolean fetchDeletedRecords = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_DELETED_RECORDS, false);
        boolean getDependantModuleData = (boolean) context.get(DataMigrationConstants.GET_DEPENDANT_MODULE_DATA);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);
        Map<String, Stack<ModuleCSVFileContext>> moduleNameVsCsvFileContext = (Map<String, Stack<ModuleCSVFileContext>>) context.get(DataMigrationConstants.MODULENAME_VS_CSV_FILE_CONTEXT);

        Map<String, List<Long>> fetchedRecords = new HashMap<>();
        Map<String, List<Long>> toBeFetchRecords = new HashMap<>();

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);

        String lastModuleName = null;
        boolean moduleMigrationStarted = true;
        long lastMigratedModuleId = dataMigrationObj.getLastModuleId();

        if(lastMigratedModuleId > 0) {
            moduleMigrationStarted = false;
            lastModuleName = moduleBean.getModule(lastMigratedModuleId).getName();
        } else {
            SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.MODULE_DATA_CSV_CREATION, null, null, 0);
        }

        for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();

            if (!moduleMigrationStarted && !moduleName.equals(lastModuleName)) {
                continue;
            } else {
                moduleMigrationStarted = true;
            }

            Criteria moduleCriteria = (Criteria) moduleDetails.get("criteria");
            FacilioModule module = (FacilioModule) moduleDetails.get("sourceModule");
            List<String> numberFileFields = (List<String>) moduleDetails.getOrDefault("fileFields", new ArrayList<>());
            Map<String, Map<String, Object>> numberLookUps = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");

            List<FacilioField> allFields = moduleBean.getAllFields(moduleName);
            List<SupplementRecord> sourceSupplements = DataMigrationUtil.getSupplementFields(allFields);

            List<String> fileFieldNamesWithId = allFields.stream().filter(field -> field.getDataTypeEnum().equals(FieldType.FILE)).map(fieldObj -> fieldObj.getName() + "Id").collect(Collectors.toList());
            fileFieldNamesWithId.addAll(numberFileFields);

            int offset = 0;
            int limit = 5000;
            int lastCountInCSVFile = 0;
            if (moduleName.equals(lastModuleName) && offset < dataMigrationObj.getMigratedCount()) {
                offset = (int) dataMigrationObj.getMigratedCount();
            } else if (reqOffset > 0) {
                offset = reqOffset;
            }
            if (reqLimit > 0 && reqLimit < limit) {
                limit = reqLimit;
            }
            boolean isModuleMigrated = false;
            List<Map<String, Object>> propsForCsv = new ArrayList<>();

            if (MapUtils.isEmpty(numberLookUps) && CollectionUtils.isEmpty(allFields)) {
                LOGGER.info("####Data Package - Fetch - No Fields found for ModuleName - " + moduleName);
                continue;
            }

            LOGGER.info("####Data Package - Fetch - Started for ModuleName - " + moduleName);

            try {
                do {
                    List<Map<String, Object>> props = new ArrayList<>();
                    try {
                        props = migrationBean.getModuleData(module, allFields, sourceSupplements, offset, limit + 1, null, moduleCriteria, null, fetchDeletedRecords);
                    } catch (Exception e) {
                        LOGGER.error("####Data Package - Fetch - Error while fetching records for ModuleName - " + moduleName, e);
                        isModuleMigrated = true;
                        continue;
                    }
                    if (CollectionUtils.isEmpty(props)) {
                        LOGGER.info("####Data Package - Fetch - No Records found for ModuleName - " + moduleName);
                        isModuleMigrated = true;
                    } else {
                        LOGGER.info("####Data Package - Fetch - In progress for ModuleName - " + moduleName + " - Offset - " + offset);

                        if (props.size() > limit) {
                            props.remove(limit);
                        } else {
                            isModuleMigrated = true;
                        }

                        propsForCsv.addAll(props);
                        offset = offset + props.size();

                        if (reqLimit > 0 && offset >= reqLimit) {
                            isModuleMigrated = true;
                        }

                        // module data csv creation
                        if (isModuleMigrated || propsForCsv.size() == DataMigrationConstants.MAX_RECORDS_PER_ITERATION) {
                            String fileName = SandboxDataMigrationUtil.addDataPropsToCSVFile(moduleNameVsCsvFileContext, module, new ArrayList<>(allFields), propsForCsv,
                                    fileFieldNamesWithId, getDependantModuleData, fetchedRecords, toBeFetchRecords, numberLookUps);
                            propsForCsv = new ArrayList<>();
                            lastCountInCSVFile = offset;

                            SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, null, module.getModuleId(), fileName, offset);
                        }
                    }

                    if (DataMigrationConstants.isTransactionTimeOutReached(DataMigrationConstants.getTransactionStartTime(context), transactionTimeOut)) {
                        SandboxDataMigrationUtil.constructResponse(dataMigrationObj, migrationModuleNameVsDetails, moduleNameVsCsvFileContext);
                        LOGGER.info("####Data Migration - Fetch - Stopped after exceeding transaction timeout with ModuleName - " + moduleName + " Offset - " + offset);
                        return true;
                    }
                } while (!isModuleMigrated);
            } catch (Exception e) {
                SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, null, module.getModuleId(), null, lastCountInCSVFile);
                SandboxDataMigrationUtil.constructResponse(dataMigrationObj, migrationModuleNameVsDetails, moduleNameVsCsvFileContext);
                LOGGER.info("####Data Migration - Fetch - Error occurred in ModuleName - " + moduleName, e);
                context.put(DataMigrationConstants.ERROR_OCCURRED, true);
                return true;
            }

            LOGGER.info("####Data Package - Fetch - Completed for ModuleName - " + moduleName);
        }

        SandboxDataMigrationUtil.updateDataMigrationContext(dataMigrationObj, DataMigrationStatusContext.DataMigrationStatus.DEPENDENT_MODULE_DATA_CSV_CREATION, null, null, 0);
        dataMigrationObj = migrationBean.getDataMigrationStatus(dataMigrationObj.getId());

        context.put(PackageConstants.FETCHED_RECORDS, fetchedRecords);
        context.put(PackageConstants.TO_BE_FETCH_RECORDS, toBeFetchRecords);
        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);
        context.put(DataMigrationConstants.MODULENAME_VS_CSV_FILE_CONTEXT, moduleNameVsCsvFileContext);

        return false;
    }
}
