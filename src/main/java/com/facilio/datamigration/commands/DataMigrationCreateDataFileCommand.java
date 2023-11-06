package com.facilio.datamigration.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageNotesUtil;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.time.DateTimeUtil;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class DataMigrationCreateDataFileCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long sourceOrgId = (long) context.getOrDefault(DataMigrationConstants.SOURCE_ORG_ID, -1l);
        HashMap<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);
        Map<String,FacilioModule> allSystemModules = (Map<String,FacilioModule>) context.get(DataMigrationConstants.ALL_SYSTEM_MODULES);
        int limit = (int) context.getOrDefault(DataMigrationConstants.LIMIT, 5000);

        int actualLimit = 0;
        boolean getLimitedRecords = true;
        if (limit <= 0) {
            getLimitedRecords = false;
            limit = 5000;
        }

        actualLimit = getLimitedRecords ? limit : limit+1;

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        PackageFolderContext rootFolder = new PackageFolderContext("Data_Package_" + sourceOrgId + "_" + DateTimeUtil.getFormattedTime(DateTimeUtil.getCurrenTime()));
        PackageFolderContext dataFileFolder = rootFolder.addFolder(PackageConstants.DATA_ATTACHMENT_FILE_FOLDER_NAME);
        PackageFolderContext dataFolder = rootFolder.addFolder(PackageConstants.DATA_FOLDER_NAME);
        XMLBuilder dataModuleConfigXML = XMLBuilder.create(PackageConstants.DATA_FOLDER_NAME);
        XMLBuilder dataModulesConfigXML = dataModuleConfigXML.element(PackageConstants.MODULE_NAMES);

        Map<String, List<Long>> toBeFetchRecords = new HashMap<>();
        Map<String, List<Long>> fetchedRecords = new HashMap<>();
        List<String> fetchedNotesModule = new ArrayList<>();
        List<String> fetchedAttachmentsModule = new ArrayList<>();
        List<String> allMigrationModuleNames = new ArrayList<>(migrationModuleNameVsDetails.keySet());

        for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();

            LOGGER.info("Data Migration  started for : " + moduleName);

            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();
            Map<String, Map<String, Object>> numberLookupDetails = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");
            File moduleCsvFile = null;
            File moduleNotesCsvFile = null;
            File moduleAttachmentsCsv = null;

            DataMigrationBean sourceConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);
            FacilioModule sourceModule = (FacilioModule) moduleDetails.get("sourceModule");
            List<FacilioField> sourceFields = moduleBean.getAllFields(moduleName);
            List<SupplementRecord> sourceSupplements = DataMigrationUtil.getSupplementFields(sourceFields);

            Criteria moduleCriteria = null;
            if (moduleDetails.containsKey("criteria")) {
                moduleCriteria = (Criteria) moduleDetails.get("criteria");
            }

            int offset = 0;
            boolean isModuleMigrated = false;
            List<Map<String, Object>> propsForCsv = new ArrayList<>();

            do {

                List<Map<String, Object>> props = new ArrayList<>();
                try {
                    props = sourceConnection.getModuleData(sourceModule, sourceFields, sourceSupplements, offset, actualLimit, null, moduleCriteria);
                } catch (Exception e) {
                    isModuleMigrated = true;
                    LOGGER.error("Get record error for module : " + moduleName, e);
                    continue;
                }
                if (CollectionUtils.isEmpty(props)) {
                    isModuleMigrated = true;
                } else {

                    LOGGER.info("Migration - Creation - in progress for moduleName -" + moduleName + " - offset:" + offset);

                    if (props.size() > limit) {
                        props.remove(limit);
                    } else {
                        isModuleMigrated = true;
                    }

                    List<Long> fetchedRecordIds = props.stream().map(p -> (Long) p.get("id")).collect(Collectors.toList());
                    if (toBeFetchRecords.containsKey(moduleName)) {
                        List<Long> tobeFetchedRecordIds = toBeFetchRecords.get(moduleName);
                        tobeFetchedRecordIds.removeAll(fetchedRecordIds);
                        toBeFetchRecords.put(moduleName, tobeFetchedRecordIds);
                    }
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

            if(CollectionUtils.isEmpty(propsForCsv)){
                continue;
            }

            // module data csv creation
            moduleCsvFile = PackageFileUtil.exportDataAsCSVFile(sourceModule, sourceFields, propsForCsv, dataFolder, toBeFetchRecords, numberLookupDetails, fetchedRecords, allMigrationModuleNames);

            if (moduleCsvFile != null) {

                // dataConfig.xml  creation
                XMLBuilder datasModulesConfigXML = dataModuleConfigXML.getElement(PackageConstants.MODULE_NAMES);
                XMLBuilder dataModuleNameConfigXML = datasModulesConfigXML.element(PackageConstants.MODULE);
                dataModuleNameConfigXML.attr(PackageConstants.NAME, moduleName);
                dataModuleNameConfigXML.text(moduleName + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN);

                //module data attachments csv creation
                List<AttachmentContext> moduleAttachments = new ArrayList<>();
                List<FacilioModule> subAttachmentModules = moduleBean.getSubModules(sourceModule.getName(), FacilioModule.ModuleType.ATTACHMENTS);
                if (CollectionUtils.isNotEmpty(subAttachmentModules)) {
                    FacilioModule attachmentModule = subAttachmentModules.get(0);
                    if (!fetchedAttachmentsModule.contains(attachmentModule.getName())) {
                        moduleAttachments = AttachmentsAPI.getAttachments(attachmentModule.getName(), -1l, false);
                        fetchedAttachmentsModule.add(attachmentModule.getName());
                        if (CollectionUtils.isNotEmpty(moduleAttachments)) {
                            moduleAttachmentsCsv = PackageFileUtil.exportAttachmentsAsCSVFile(attachmentModule, dataFolder, moduleAttachments, dataFileFolder, moduleBean);
                        }
                    }
                    if (moduleAttachmentsCsv != null) {
                        dataFolder.addFile(attachmentModule.getName() + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN, new PackageFileContext(attachmentModule.getName(), PackageConstants.CSV_FILE_EXTN, moduleAttachmentsCsv));
                    }
                }

                //module data notes csv creation
                List<NoteContext> moduleNotes = new ArrayList<>();
                List<FacilioModule> subModules = moduleBean.getSubModules(sourceModule.getName(), FacilioModule.ModuleType.NOTES);
                if (CollectionUtils.isNotEmpty(subModules)) {
                    FacilioModule noteModule = subModules.get(0);
                    if (!fetchedNotesModule.contains(noteModule.getName())) {
                        moduleNotes = PackageNotesUtil.getAllNotes(noteModule);
                        fetchedNotesModule.add(noteModule.getName());
                        if (CollectionUtils.isNotEmpty(moduleNotes)) {
                            moduleNotesCsvFile = PackageFileUtil.exportNotesAsCSVFile(noteModule, rootFolder, moduleNotes);
                        }
                    }

                    if (moduleNotesCsvFile != null) {
                        dataFolder.addFile(noteModule.getName() + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN, new PackageFileContext(noteModule.getName(), PackageConstants.CSV_FILE_EXTN, moduleNotesCsvFile));
                    }
                }

                if (moduleCsvFile != null) {
                    dataFolder.addFile(moduleName + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN, new PackageFileContext(moduleName, PackageConstants.CSV_FILE_EXTN, moduleCsvFile));
                }
            }
            LOGGER.info("CSV creation completed for : " + moduleName);

        }

        rootFolder.addFile(PackageConstants.DATA_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN, new PackageFileContext(PackageConstants.DATA_CONF_FILE_NAME, PackageConstants.XML_FILE_EXTN, dataModuleConfigXML));

        Iterator<Map.Entry<String, List<Long>>> iterator = toBeFetchRecords.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Long>> entry = iterator.next();
            List<Long> values = entry.getValue();

            if (CollectionUtils.isEmpty(values)) {
                iterator.remove();
            }
        }

        context.put(PackageConstants.PACKAGE_ROOT_FOLDER, rootFolder);
        context.put(DataMigrationConstants.ALL_DATA_MIGRATION_MODULES, allMigrationModuleNames);
        context.put(PackageConstants.TO_BE_FETCH_RECORDS, toBeFetchRecords);
        context.put(PackageConstants.FETCHED_RECORDS, fetchedRecords);

        return false;
    }


}
