package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.NotesUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class SandboxDataAddModuleNotesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets = (Map<ComponentType, List<PackageChangeSetMappingContext>>) context.get(DataMigrationConstants.PACKAGE_CHANGE_SET);
        boolean allowNotesAndAttachments = (boolean) context.get(DataMigrationConstants.ALLOW_NOTES_AND__ATTACHMENTS);
        if (!allowNotesAndAttachments) {
            return false;
        }
        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        List<String> allModuleNamesXml = DataPackageFileUtil.getDataConfigModuleNames();
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
                InputStream notesCSVStream = DataPackageFileUtil.getModuleCSVStream(noteModule.getName() + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN);
                if (notesCSVStream == null) {
                    continue;
                }
                List<NoteContext> notes = NotesUtil.getNotesFromFile(notesCSVStream, noteModule, packageChangSets, parentNoteModule, targetConnection, dataMigrationObj);

                for (NoteContext noteContext : notes) {
                    if (noteContext.getParentNote() == null) {
                        notesWithoutPrent.add(noteContext);
                    } else {
                        notesWithPrent.add(noteContext);
                    }
                }

                Map<Long, Long> oldVsNewParentIds = NotesUtil.addNotes(notesWithoutPrent, noteModule.getName(), moduleName);

                for (NoteContext noteContext : notesWithPrent) {
                    long oldParentNoteId = noteContext.getParentNote().getId();
                    long newParentNoteId = oldVsNewParentIds.getOrDefault(oldParentNoteId, -1L);
                    noteContext.getParentNote().setId(newParentNoteId);
                }

                NotesUtil.addNotes(notesWithPrent, noteModule.getName(), moduleName);

                addedNotes.add(noteModule.getName());
                notesCSVStream.close();
            }

        }

        return false;
    }
}
