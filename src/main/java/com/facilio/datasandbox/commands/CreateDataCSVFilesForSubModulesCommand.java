package com.facilio.datasandbox.commands;

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
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class CreateDataCSVFilesForSubModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long sourceOrgId = (long) context.getOrDefault(DataMigrationConstants.SOURCE_ORG_ID, -1l);
        PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);
        boolean allowNotesAndAttachments = (boolean) context.get(DataMigrationConstants.ALLOW_NOTES_AND__ATTACHMENTS);
        boolean isRestrictDependantModules = (boolean) context.get(DataMigrationConstants.RESTRICT_DEPENDANT_MODULES);
        Map<String, List<Long>> fetchedRecords = (Map<String, List<Long>>) context.get(PackageConstants.FETCHED_RECORDS);
        Map<String, List<Long>> toBeFetchRecords = (Map<String, List<Long>>) context.get(PackageConstants.TO_BE_FETCH_RECORDS);
        List<String> allDataMigrationModules = (List<String>) context.get(DataMigrationConstants.ALL_DATA_MIGRATION_MODULES);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (Map<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);
        Map<String, Map<String, Object>> subModuleNameVsDetails = (Map<String, Map<String, Object>>) context.getOrDefault(DataMigrationConstants.SUB_MODULES_VS_DETAILS, new HashMap<>());

        // all the sub-module records should be fetched, no limit should be applied
        int limit = 5000;

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);

        PackageFolderContext dataFileFolder = rootFolder.getFolder(PackageConstants.DATA_ATTACHMENT_FILE_FOLDER_NAME);
        PackageFolderContext dataFolder = rootFolder.getFolder(PackageConstants.DATA_FOLDER_NAME);

        PackageFileContext dataConfigFileContext = rootFolder.getFile(PackageConstants.DATA_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN);
        XMLBuilder dataConfigXml = dataConfigFileContext.getXmlContent();
        XMLBuilder moduleNamesXML = dataConfigXml.getElement(PackageConstants.MODULE_NAMES);

        List<String> allSubModuleNames = new ArrayList<>(subModuleNameVsDetails.keySet());
        List<String> fetchedNotesModule = new ArrayList<>();
        List<String> fetchedAttachmentsModule = new ArrayList<>();


        for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : subModuleNameVsDetails.entrySet()) {
            String moduleName = moduleNameVsDetails.getKey();
            Map<String, Object> moduleDetails = moduleNameVsDetails.getValue();

            LOGGER.info("Data Migration started for SubModule : " + moduleName);
            File moduleCsvFile = null;
            File moduleNotesCsvFile = null;
            File moduleAttachmentsCsv = null;

            Criteria moduleCriteria = (Criteria) moduleDetails.get("criteria");
            FacilioModule sourceModule = (FacilioModule) moduleDetails.get("sourceModule");
            List<LookupField> relatedFields = (List<LookupField>) moduleDetails.get("relatedFields");
            Map<String, Map<String, Object>> numberLookupDetails = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");

            if (CollectionUtils.isNotEmpty(relatedFields)) {
                Criteria relatedFieldCriteria = SandboxModuleConfigUtil.computeRelatedFieldCriteriaForSubModules(relatedFields, fetchedRecords);

                if (moduleCriteria == null) {
                    moduleCriteria = relatedFieldCriteria;
                } else {
                    moduleCriteria.andCriteria(relatedFieldCriteria);
                }
            }

            List<FacilioField> sourceFields = moduleBean.getAllFields(moduleName);
            List<SupplementRecord> sourceSupplements = DataMigrationUtil.getSupplementFields(sourceFields);

            int offset = 0;
            boolean isModuleMigrated = false;
            List<Map<String, Object>> propsForCsv = new ArrayList<>();

            do {
                List<Map<String, Object>> props = new ArrayList<>();
                try {
                    props = migrationBean.getModuleData(sourceModule, sourceFields, sourceSupplements, offset, limit + 1, null, moduleCriteria);
                } catch (Exception e) {
                    LOGGER.error("Error while fetching records for module : " + moduleName, e);
                    isModuleMigrated = true;
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
                    if (fetchedRecords.containsKey(moduleName)) {
                        List<Long> recordIds = fetchedRecords.get(moduleName);
                        recordIds.addAll(fetchedRecordIds);
                        fetchedRecords.put(moduleName, recordIds);
                    } else {
                        fetchedRecords.put(moduleName, fetchedRecordIds);
                    }

                    if (toBeFetchRecords.containsKey(moduleName)) {
                        List<Long> tobeFetchedRecordIds = toBeFetchRecords.get(moduleName);
                        tobeFetchedRecordIds.removeAll(fetchedRecordIds);
                        toBeFetchRecords.put(moduleName, tobeFetchedRecordIds);
                    }

                    propsForCsv.addAll(props);
                    offset = offset + props.size();
                }
            } while (!isModuleMigrated);

            if (CollectionUtils.isEmpty(propsForCsv)) {
                continue;
            }

            // module data csv creation
            moduleCsvFile = PackageFileUtil.exportDataAsCSVFile(sourceModule, sourceFields, propsForCsv, dataFolder, toBeFetchRecords, numberLookupDetails, fetchedRecords, allSubModuleNames, isRestrictDependantModules);

            if (moduleCsvFile != null) {
                // dataConfig.xml creation
                XMLBuilder dataModuleNameConfigXML = moduleNamesXML.element(PackageConstants.MODULE);
                dataModuleNameConfigXML.attr(PackageConstants.NAME, moduleName);
                dataModuleNameConfigXML.text(moduleName + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN);

                if (allowNotesAndAttachments) {
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
                }
                dataFolder.addFile(moduleName + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN, new PackageFileContext(moduleName, PackageConstants.CSV_FILE_EXTN, moduleCsvFile));
            }

            LOGGER.info("CSV creation completed for : " + moduleName);
        }

        Iterator<Map.Entry<String, List<Long>>> iterator = toBeFetchRecords.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Long>> entry = iterator.next();
            List<Long> values = entry.getValue();

            if (CollectionUtils.isEmpty(values)) {
                iterator.remove();
            }
        }

        context.put(PackageConstants.DOWNLOAD_URL, null);
        context.put(PackageConstants.PACKAGE_ROOT_FOLDER, rootFolder);
        context.put(PackageConstants.FETCHED_RECORDS, fetchedRecords);
        context.put(PackageConstants.TO_BE_FETCH_RECORDS, toBeFetchRecords);
        context.put(DataMigrationConstants.ALL_DATA_SUB_MIGRATION_MODULES, allSubModuleNames);

        return false;
    }
}
