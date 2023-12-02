package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.datasandbox.util.AttachmentsUtil;
import com.facilio.datasandbox.util.DataPackageFileUtil;
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
public class SandboxDataAddModuleAttachmentsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        boolean allowNotesAndAttachments = (boolean) context.get(DataMigrationConstants.ALLOW_NOTES_AND__ATTACHMENTS);
        if (!allowNotesAndAttachments) {
            return false;
        }
        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        List<String> allModuleNamesXml = DataPackageFileUtil.getDataConfigModuleNames();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<String> addedAttachments = new ArrayList<>();

        for (String moduleName : allModuleNamesXml) {

            LOGGER.info("####Sandbox --- Insert attachment for module  :" + moduleName);

            FacilioModule parentModule = DataMigrationUtil.getBaseExtendedModule(moduleName, modBean);

            List<FacilioModule> subModules = modBean.getSubModules(moduleName, FacilioModule.ModuleType.ATTACHMENTS);

            if (CollectionUtils.isNotEmpty(subModules) && !addedAttachments.contains(subModules.get(0).getName())) {
                FacilioModule attachmentModule = subModules.get(0);
                InputStream attachmentCSVStream = DataPackageFileUtil.getModuleCSVStream(attachmentModule.getName() + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN);
                if (attachmentCSVStream == null) {
                    continue;
                }

                List<AttachmentContext> moduleAttachments = AttachmentsUtil.getAttachmentListFromCsv(attachmentCSVStream, parentModule, targetConnection, dataMigrationObj);

                if (CollectionUtils.isEmpty(moduleAttachments)) {
                    continue;
                }
                AttachmentsAPI.addAttachments(moduleAttachments, attachmentModule.getName());
                addedAttachments.add(attachmentModule.getName());
                attachmentCSVStream.close();
            }

        }
        return false;
    }
}
