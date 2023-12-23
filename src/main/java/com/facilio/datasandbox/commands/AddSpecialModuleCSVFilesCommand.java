package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class AddSpecialModuleCSVFilesCommand extends FacilioCommand {
    /**
     * Handled Special V2 Modules like "taskSection", "taskInputOption" (associated with "task" module)
     */
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        int reqOffset = (int) context.getOrDefault(DataMigrationConstants.OFFSET, 0);
        int reqLimit = (int) context.getOrDefault(DataMigrationConstants.LIMIT, 0);
        boolean getDependantModuleData = (boolean) context.get(DataMigrationConstants.GET_DEPENDANT_MODULE_DATA);
        Map<String, List<Long>> fetchedRecords = (Map<String, List<Long>>) context.get(PackageConstants.FETCHED_RECORDS);
        Map<String, List<Long>> toBeFetchRecords = (Map<String, List<Long>>) context.get(PackageConstants.TO_BE_FETCH_RECORDS);
        Map<String, String> moduleNameVsCsvFileName = (Map<String, String>) context.get(DataMigrationConstants.MODULENAME_VS_CSV_FILENAME);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);

        List<String> dataMigrationModuleNames = (List<String>) context.get(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES);
        List<String> runDataMigrationOnlyForModulesNames = (List<String>) context.get(DataMigrationConstants.RUN_ONLY_FOR_MODULES);
        boolean createFullDataPackage = (boolean) context.getOrDefault(DataMigrationConstants.CREATE_FULL_PACKAGE, false);

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);

        for (Map.Entry<String, Map<String, Object>> moduleVsDetails : SandboxModuleConfigUtil.SPECIAL_MODULENAME_VS_DETAILS.entrySet()) {
            String moduleName = moduleVsDetails.getKey();
            Map<String, Object> moduleDetails = moduleVsDetails.getValue();

            if (!createFullDataPackage && !dataMigrationModuleNames.contains(moduleName) && !runDataMigrationOnlyForModulesNames.contains(moduleName)) {
                continue;
            }

            Criteria moduleCriteria = (Criteria) moduleDetails.get("criteria");
            FacilioModule module = (FacilioModule) moduleDetails.get("sourceModule");
            List<FacilioField> allFields = (List<FacilioField>) moduleDetails.get("fields");
            List<String> numberFileFields = (List<String>) moduleDetails.getOrDefault("fileFields", new ArrayList<>());
            Map<String, Map<String, Object>> numberLookUps = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");

            List<String> fileFieldNamesWithId = allFields.stream().filter(field -> field.getDataTypeEnum().equals(FieldType.FILE)).map(fieldObj -> fieldObj.getName() + "Id").collect(Collectors.toList());
            fileFieldNamesWithId.addAll(numberFileFields);

            File moduleCsvFile = null;
            String moduleCsvFilePath = null;

            List<Map<String, Object>> propsForCsv = getPropsForCSV(module, allFields, moduleCriteria, reqOffset, reqLimit);
            if (CollectionUtils.isEmpty(propsForCsv)) {
                LOGGER.info("####Data Package - Fetch - No Records found for ModuleName - " + moduleName);
                continue;
            }

            // module data csv creation
            LOGGER.info("####Data Migration - Creation - CSV Creation started for ModuleName - " + moduleName);

            moduleCsvFile = SandboxDataMigrationUtil.exportDataAsCSVFile(module, allFields, propsForCsv, fileFieldNamesWithId, getDependantModuleData, fetchedRecords, toBeFetchRecords, numberLookUps);

            LOGGER.info("####Data Migration - Creation - CSV Creation completed for ModuleName - " + moduleName);

            if (moduleCsvFile != null) {
                moduleCsvFilePath = moduleName + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN;
                DataPackageFileUtil.addModuleCSVFile(moduleName, moduleCsvFile);
                moduleNameVsCsvFileName.put(moduleName, moduleCsvFilePath);
                migrationModuleNameVsDetails.put(moduleName, moduleDetails);
            }
        }

        context.put(DataMigrationConstants.MODULENAME_VS_CSV_FILENAME, moduleNameVsCsvFileName);
        return false;
    }

    private static List<Map<String, Object>> getPropsForCSV(FacilioModule module, List<FacilioField> allFields, Criteria moduleCriteria, int reqOffset, int reqLimit) throws Exception {
        int offset = 0;
        int limit = 5000;
        if (reqOffset > 0) {
            offset = reqOffset;
        }
        if (reqLimit > 0 && reqLimit < limit) {
            limit = reqLimit;
        }
        boolean isModuleMigrated = false;
        String moduleName = module.getName();
        List<Map<String, Object>> propsForCsv = new ArrayList<>();

        if (CollectionUtils.isEmpty(allFields)) {
            LOGGER.info("####Data Package - Fetch - No Fields found for ModuleName - " + moduleName);
            return propsForCsv;
        }

        LOGGER.info("####Data Package - Fetch - Started for ModuleName - " + moduleName);

        do {
            List<Map<String, Object>> props = new ArrayList<>();
            try {
                props = getModuleData(module, allFields, moduleCriteria, offset, limit + 1);
            } catch (Exception e) {
                LOGGER.error("####Data Package - Fetch - Error while fetching records for ModuleName - " + moduleName, e);
                isModuleMigrated = true;
                continue;
            }
            if (CollectionUtils.isEmpty(props)) {
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
            }
        } while (!isModuleMigrated);

        LOGGER.info("####Data Package - Fetch - Completed for ModuleName - " + moduleName);

        return propsForCsv;
    }

    private static List<Map<String, Object>> getModuleData(FacilioModule module, List<FacilioField> allFields, Criteria moduleCriteria, int offset, int limit) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(allFields)
                .table(module.getTableName())
                .offset(offset).limit(limit);

        if (moduleCriteria != null && !moduleCriteria.isEmpty()) {
            selectBuilder.andCriteria(moduleCriteria);
        }

        String customWhereClause = DataMigrationUtil.getCustomWhereClause(module);
        if (StringUtils.isNotEmpty(customWhereClause)) {
            selectBuilder.andCustomWhere(customWhereClause);
        }

        List<Map<String, Object>> propsList = selectBuilder.get();
        return propsList;
    }
}
