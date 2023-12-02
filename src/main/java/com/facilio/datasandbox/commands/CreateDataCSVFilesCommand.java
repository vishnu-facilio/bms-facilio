package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class CreateDataCSVFilesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        boolean getDependantModuleData = (boolean) context.get(DataMigrationConstants.GET_DEPENDANT_MODULE_DATA);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);

        Map<String, List<Long>> fetchedRecords = new HashMap<>();
        Map<String, List<Long>> toBeFetchRecords = new HashMap<>();
        Map<String, String> moduleNameVsCsvFileName = new HashMap<>();

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);

        for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();

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
            File moduleCsvFile = null;
            String moduleCsvFilePath = null;
            boolean isModuleMigrated = false;
            List<Map<String, Object>> propsForCsv = new ArrayList<>();

            if (MapUtils.isEmpty(numberLookUps) && CollectionUtils.isEmpty(allFields)) {
                LOGGER.info("####Data Package - Fetch - No Fields found for ModuleName - " + moduleName);
                continue;
            }

            LOGGER.info("####Data Package - Fetch - Started for ModuleName - " + moduleName);

            do {
                List<Map<String, Object>> props = new ArrayList<>();
                try {
                    props = migrationBean.getModuleData(module, allFields, sourceSupplements, offset, limit + 1, null, moduleCriteria);
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
                }
            } while (!isModuleMigrated);

            LOGGER.info("####Data Package - Fetch - Completed for ModuleName - " + moduleName);

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
            }
        }

        context.put(PackageConstants.FETCHED_RECORDS, fetchedRecords);
        context.put(PackageConstants.TO_BE_FETCH_RECORDS, toBeFetchRecords);
        context.put(DataMigrationConstants.MODULENAME_VS_CSV_FILENAME, moduleNameVsCsvFileName);

        return false;
    }
}
