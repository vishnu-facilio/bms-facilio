package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.constants.FacilioConstants;
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
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class UpdateDataCSVFilesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean getDependantModuleData = (boolean) context.get(DataMigrationConstants.GET_DEPENDANT_MODULE_DATA);
        if (!getDependantModuleData) {
            return false;
        }

        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        Map<String, List<Long>> fetchedRecords = (Map<String, List<Long>>) context.get(PackageConstants.FETCHED_RECORDS);
        Map<String, List<Long>> toBeFetchRecords = (Map<String, List<Long>>) context.get(PackageConstants.TO_BE_FETCH_RECORDS);
        boolean fetchDeletedRecords = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_DELETED_RECORDS, false);
        Map<String, String> moduleNameVsCsvFileName = (Map<String, String>) context.get(DataMigrationConstants.MODULENAME_VS_CSV_FILENAME);
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
                File moduleCsvFile = null;
                String moduleCsvFileName = null;
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

                // update csv file
                if (moduleNameVsCsvFileName.containsKey(moduleName)) {
                    moduleCsvFileName = moduleNameVsCsvFileName.get(moduleName);
                    File tempCsvFile = DataPackageFileUtil.addFileToTempFolder(moduleCsvFileName, moduleName + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN);
                    SandboxDataMigrationUtil.updateDataInCSVFile(tempCsvFile, allFields, propsForCsv, fileFieldNamesWithId, getDependantModuleData, fetchedRecords, toBeFetchRecords, numberLookUps);

                    DataPackageFileUtil.addModuleCSVFile(moduleName, tempCsvFile);
                    FileUtils.delete(tempCsvFile);
                    LOGGER.info("####Data Migration - LookUp Fetch - Old CSV File updated for ModuleName - " + moduleName);
                } else {
                    // add new csv file
                    moduleCsvFile = SandboxDataMigrationUtil.exportDataAsCSVFile(module, allFields, propsForCsv, fileFieldNamesWithId, getDependantModuleData, fetchedRecords, toBeFetchRecords, numberLookUps);
                    moduleCsvFileName = DataPackageFileUtil.addModuleCSVFile(moduleName, moduleCsvFile);
                    moduleNameVsCsvFileName.put(moduleName, moduleCsvFileName);

                    LOGGER.info("####Data Migration - LookUp Fetch - New CSV File created for ModuleName - " + moduleName);
                }
            }
        }

        return false;
    }
}
