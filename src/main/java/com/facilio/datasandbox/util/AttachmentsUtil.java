package com.facilio.datasandbox.util;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.FileContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.modules.FacilioModule;
import com.opencsv.CSVReader;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Log4j
public class AttachmentsUtil {
    public static List<AttachmentContext> getAttachmentListFromCsv(InputStream inputStream, FacilioModule parentModule, DataMigrationBean targetConnection, DataMigrationStatusContext dataMigrationObj) throws Exception {

        String attachmentFolderName = PackageConstants.DATA_ATTACHMENT_FILE_FOLDER_NAME;
        String attachmentFolderAbsolutePath = PackageUtil.getRootFolderPath() + File.separator + attachmentFolderName;

        List<AttachmentContext> allModuleAttachments = new ArrayList<>();
        if (inputStream == null || !DataPackageFileUtil.isValidDirectory(attachmentFolderAbsolutePath)) {
            return allModuleAttachments;
        }

        Map<Long, List<Long>> moduleIdVsOldIds = new HashMap<>();
        Map<Long, Map<Long, Long>> moduleIdVsOldIdVsNewId = new HashMap<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String[]> csvData = csvReader.readAll();
            String[] fieldNames = csvData.get(0);
            for (int i = 1; i < csvData.size(); i++) {
                String[] fieldValues = csvData.get(i);
                AttachmentContext AttachmentContext = getCsvAttachment(fieldNames, fieldValues, parentModule, moduleIdVsOldIds);
                allModuleAttachments.add(AttachmentContext);
            }
        } catch (Exception ex) {
            LOGGER.info("####Sandbox --- Exception while creating csv attachment file from props modulename :" + parentModule.getName() + "Exception : " + ex);
            throw new Exception(ex);
        }

        for (Map.Entry<Long, List<Long>> entry : moduleIdVsOldIds.entrySet()) {
            Long moduleId = entry.getKey();
            List<Long> oldIds = entry.getValue();
            Map<Long, Long> idMappings = targetConnection.getOldVsNewId(dataMigrationObj.getId(), moduleId, oldIds);
            moduleIdVsOldIdVsNewId.put(moduleId, idMappings);
        }

        Map<Long, Long> idMapping = moduleIdVsOldIdVsNewId.get(parentModule.getModuleId());

        for (AttachmentContext attachmentContext : allModuleAttachments) {
            long oldRecordId = attachmentContext.getParentId();
            attachmentContext.setParentId(idMapping.get(oldRecordId));
            attachmentContext.setCreatedTime(System.currentTimeMillis());
        }

        return allModuleAttachments;
    }

    private static AttachmentContext getCsvAttachment(String[] fieldNames, String[] fieldValues, FacilioModule parentModule, Map<Long, List<Long>> moduleIdVsOldIds) throws Exception {
        AttachmentContext attachmentContext = new AttachmentContext();
        List<Long> oldIds = new ArrayList<>();
        if (MapUtils.isNotEmpty(moduleIdVsOldIds)) {
            oldIds = moduleIdVsOldIds.get(parentModule.getModuleId());
        }
        for (int i = 0; i < fieldNames.length; i++) {
            if (i < fieldValues.length) {
                if (StringUtils.isEmpty(fieldValues[i])) {
                    continue;
                }
                String fieldName = fieldNames[i];
                String fieldValue = fieldValues[i];
                if (Objects.equals(fieldName, "fileName")) {
                    attachmentContext.setFileName(fieldValue);
                } else if (Objects.equals(fieldName, "contentType")) {
                    attachmentContext.setContentType(fieldValue);
                } else if (Objects.equals(fieldName, "recordId") || Objects.equals(fieldName, "parent")) {
                    long oldId = Long.parseLong(fieldValue);
                    if (!oldIds.contains(oldId)) {
                        oldIds.add(oldId);
                    }
                    attachmentContext.setParentId(oldId);
                } else if (Objects.equals(fieldName, "uniqueIdentifier")) {
                    String filePath = DataPackageFileUtil.getAttachmentFilePath(fieldValue);
                    File sourceOrgFile = new File(filePath);
                    File file = new File(sourceOrgFile.getParentFile(), attachmentContext.getFileName());
                    sourceOrgFile.renameTo(file);
                    FileContext fileContext = PackageFileUtil.addFileToStore(file, attachmentContext.getContentType());
                    attachmentContext.setFileId(fileContext.getFileId());
                }

            }
        }

        moduleIdVsOldIds.put(parentModule.getModuleId(), oldIds);

        return attachmentContext;
    }
}
