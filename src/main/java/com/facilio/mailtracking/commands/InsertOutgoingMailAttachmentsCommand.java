package com.facilio.mailtracking.commands;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.command.FacilioCommand;
import com.facilio.fs.FileInfo;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailAttachmentContext;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertOutgoingMailAttachmentsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, String> files = (Map<String, String>) context.get(MailConstants.Params.FILES);
        if(files == null || files.isEmpty()) {
            return false;
        }
        V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);
        List<V3OutgoingMailAttachmentContext> records = new ArrayList<>();
        for(Map.Entry<String, String> en : files.entrySet()) {
            V3OutgoingMailAttachmentContext record = new V3OutgoingMailAttachmentContext();
            record.setMailId(mailLogContext);
            record.setFileName(en.getKey());
            records.add(record);
        }
        OutgoingMailAPI.insertV3(MailConstants.ModuleNames.OUTGOING_MAIL_ATTACHMENTS, records);
        if(FacilioProperties.isDevelopment()) {
            updateFilePath(files);
        }
        return false;
    }

    private void updateFilePath(Map<String, String> files) throws Exception {
        for(Map.Entry<String, String> en : files.entrySet()) {
            if(en.getValue() == null) {
                continue;
            }
            String url = en.getValue();
            FileStore fs = FacilioFactory.getFileStore();
            String fileId = url.substring(url.lastIndexOf("/")+1);
            FileInfo fileInfo = fs.getFileInfo(FacilioUtil.parseLong(fileId));
            files.put(en.getKey(), fileInfo.getFilePath());
        }
    }
}