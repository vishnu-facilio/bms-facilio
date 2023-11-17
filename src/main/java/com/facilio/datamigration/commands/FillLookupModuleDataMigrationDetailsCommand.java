package com.facilio.datamigration.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class FillLookupModuleDataMigrationDetailsCommand extends FacilioCommand implements PostTransactionCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);
        Map<String, List<Long>> toBeFetchRecords = (Map<String, List<Long>>) context.get(PackageConstants.TO_BE_FETCH_RECORDS);
        Map<String, List<Long>> fetchedRecords = (Map<String, List<Long>>) context.get(PackageConstants.FETCHED_RECORDS);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (Map<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);
        List<String> allDataMigrationModules = (List<String>) context.get(DataMigrationConstants.ALL_DATA_MIGRATION_MODULES);
        boolean isRestrictDependantModules = (boolean) context.get(DataMigrationConstants.RESTRICT_DEPENDANT_MODULES);

        long sourceOrgId = (long) context.getOrDefault(DataMigrationConstants.SOURCE_ORG_ID, -1l);
        AccountUtil.setCurrentAccount(sourceOrgId);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean sourceConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);

        if(!isRestrictDependantModules) {
            while (MapUtils.isNotEmpty(toBeFetchRecords)) {
                Map<String, List<Long>> streamRecords = new HashMap<>(toBeFetchRecords);

                File moduleCsvFile = null;
                for (Map.Entry<String, List<Long>> moduleNameVsRecordIs : streamRecords.entrySet()) {

                    List<Long> toBeFetchRecordIds = moduleNameVsRecordIs.getValue();
                    String moduleName = moduleNameVsRecordIs.getKey();
                    if (CollectionUtils.isEmpty(toBeFetchRecordIds)) {
                        toBeFetchRecords.remove(moduleName);
                        continue;
                    }
                    PackageFolderContext dataFolder = rootFolder.getFolder(PackageConstants.DATA_FOLDER_NAME);
                    List<String> allModuleNamesXml = PackageFileUtil.getDataConfigModuleNames(rootFolder);
                    Map<String, String> moduleNameVsXmlFileName = PackageFileUtil.getModuleNameVsXmlFileName(rootFolder);

                    FacilioModule sourceModule = modBean.getModule(moduleName);
                    if (sourceModule.getTypeEnum() == FacilioModule.ModuleType.PICK_LIST) {
                        toBeFetchRecords.remove(moduleName);
                        continue;
                    }
                    Map<String, Object> moduleDetails = migrationModuleNameVsDetails.get(moduleName);

                    Map<String, Map<String, Object>> numberLookupDetails = new HashMap<>();
                    if (MapUtils.isNotEmpty(moduleDetails)) {
                        numberLookupDetails = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");
                    }

                    List<FacilioField> sourceFields = modBean.getAllFields(moduleName);
                    List<SupplementRecord> sourceSupplements = DataMigrationUtil.getSupplementFields(sourceFields);
                    int offset = 0;
                    int limit = 5000;
                    boolean isModuleMigrated = false;
                    List<Map<String, Object>> propsForCsv = new ArrayList<>();
                    do {
                        List<Map<String, Object>> props = null;
                        try {
                            props = sourceConnection.getModuleDataForIds(sourceModule, sourceFields, sourceSupplements, offset, limit, null, null, toBeFetchRecordIds);
                        } catch (Exception e) {
                            isModuleMigrated = true;
                            LOGGER.error("Get lookup module record error for lookup module : " + moduleName, e);
                            continue;
                        }
                        if (CollectionUtils.isEmpty(props)) {
                            isModuleMigrated = true;
                        } else {
                            LOGGER.info("Migration - Creation - in progress for lookup moduleName -" + moduleName + " - offset:" + offset);
                            if (props.size() > limit) {
                                props.remove(limit);
                            } else {
                                isModuleMigrated = true;
                            }
                            List<Long> fetchedRecordIds = props.stream().map(p -> (Long) p.get("id")).collect(Collectors.toList());
                            if (fetchedRecords.containsKey(moduleName)) {
                                List<Long> recordIds = fetchedRecords.get(moduleName);
                                recordIds.addAll(fetchedRecordIds);
                                fetchedRecords.put(moduleName, recordIds);
                            } else {
                                fetchedRecords.put(moduleName, fetchedRecordIds);
                            }
                            propsForCsv.addAll(props);
                            offset = offset + props.size();
                        }
                    } while (!isModuleMigrated);
                    toBeFetchRecords.remove(moduleName);

                    if (CollectionUtils.isEmpty(propsForCsv)) {
                        continue;
                    }

                    if (allModuleNamesXml.contains(moduleName)) {
                        String moduleFileName = moduleNameVsXmlFileName.get(moduleName);
                        moduleCsvFile = dataFolder.getFile(moduleFileName).getCsvContent();
                        PackageFileUtil.updateCsvFile(sourceModule, moduleCsvFile, propsForCsv, sourceFields, fetchedRecords, numberLookupDetails, toBeFetchRecords, allDataMigrationModules, false);
                        LOGGER.info("File updated for : " + moduleName + " : update props " + propsForCsv);

                    } else {

                        PackageFileContext dataConfigFileContext = rootFolder.getFile(PackageConstants.DATA_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN);
                        XMLBuilder dataConfigXml = dataConfigFileContext.getXmlContent();
                        XMLBuilder dataXML = dataConfigXml.getElement(PackageConstants.MODULE_NAMES);
                        XMLBuilder moduleNamesXml = dataXML.element(PackageConstants.MODULE);
                        moduleNamesXml.attr(PackageConstants.NAME, moduleName);
                        moduleNamesXml.text(moduleName + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN);
                        LOGGER.info("File created for : " + moduleName + " : update props " + propsForCsv);
                        moduleCsvFile = PackageFileUtil.exportDataAsCSVFile(sourceModule, sourceFields, propsForCsv, dataFolder, toBeFetchRecords, numberLookupDetails, fetchedRecords, allDataMigrationModules, isRestrictDependantModules);
                        dataFolder.addFile(moduleName + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN, new PackageFileContext(moduleName, PackageConstants.CSV_FILE_EXTN, moduleCsvFile));

                    }
                }
            }
        }

        long fileId = PackageFileUtil.saveAsZipFile(rootFolder);
        String downloadUrl = FacilioFactory.getFileStore().getDownloadUrl(fileId);

        context.put(PackageConstants.FILE_ID, fileId);
        context.put(PackageConstants.DOWNLOAD_URL, downloadUrl);

        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        String directoryPath = PackageFileUtil.getSandboxFolderPath();

        File directory = new File(directoryPath);

        if (directory.exists()) {
            FileUtils.deleteDirectory(directory);
        }
        return false;
    }

    @Override
    public void onError() throws Exception {
        String directoryPath = PackageFileUtil.getSandboxFolderPath();

        File directory = new File(directoryPath);

        if (directory.exists()) {
            FileUtils.deleteDirectory(directory);
        }
    }
}
