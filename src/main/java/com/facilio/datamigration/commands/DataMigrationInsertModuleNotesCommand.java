package com.facilio.datamigration.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageNotesUtil;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class DataMigrationInsertModuleNotesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets = (Map<ComponentType, List<PackageChangeSetMappingContext>>) context.get(DataMigrationConstants.PACKAGE_CHANGE_SET);
        PackageFolderContext dataFilesFolder = rootFolder.getFolder(PackageConstants.DATA_ATTACHMENT_FILE_FOLDER_NAME);
        boolean allowNotesAndAttachments = (boolean) context.get(DataMigrationConstants.ALLOW_NOTES_AND__ATTACHMENTS);
        if(!allowNotesAndAttachments){
            return false;
        }
        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        List<String> allModuleNamesXml = PackageFileUtil.getDataConfigModuleNames(rootFolder);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<String> addedNotes = new ArrayList<>();

        for (String moduleName : allModuleNamesXml) {

            LOGGER.info("####Sandbox --- Insert notes for module  :" + moduleName);
            
            FacilioModule parentNoteModule = DataMigrationUtil.getBaseExtendedModule(moduleName, modBean);
            List<FacilioModule> subModules = modBean.getSubModules(moduleName, FacilioModule.ModuleType.NOTES);

            List<NoteContext> notesWithoutPrent = new ArrayList<>();
            List<NoteContext> notesWithPrent = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(subModules) && !addedNotes.contains(subModules.get(0).getName())) {
                FacilioModule noteModule = subModules.get(0);
                File notesCsvFile = PackageFileUtil.getModuleCsvFile(rootFolder, noteModule.getName() + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN);
                List<NoteContext> notes = PackageNotesUtil.getNotesFromFile(notesCsvFile, noteModule,packageChangSets, parentNoteModule, targetConnection, dataMigrationObj, dataFilesFolder);

                for (NoteContext noteContext : notes) {
                    if (noteContext.getParentNote() == null) {
                        notesWithoutPrent.add(noteContext);
                    } else {
                        notesWithPrent.add(noteContext);
                    }
                }

                Map<Long, Long> oldVsNewParentIds = PackageNotesUtil.addNotes(notesWithoutPrent, noteModule.getName(), moduleName);

                for (NoteContext noteContext : notesWithPrent) {
                    long oldParentNoteId = noteContext.getParentNote().getId();
                    long newParentNoteId = oldVsNewParentIds.getOrDefault(oldParentNoteId, -1L);
                    noteContext.getParentNote().setId(newParentNoteId);
                }

                PackageNotesUtil.addNotes(notesWithPrent, noteModule.getName(), moduleName);

                addedNotes.add(noteModule.getName());
            }

        }

        return false;
    }
}
