package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddMultipleAttachmentCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<Long, List<Object>> attachmentMap = (Map<Long, List<Object>>) context.get(FacilioConstants.ContextNames.ATTACHMENT_MAP_FILE_LIST);
        FileStore fs = FacilioFactory.getFileStore();

//        List<Long> attachmentIds = new ArrayList<Long>();
        List<AttachmentContext> attachments = new ArrayList<>();
        if (attachmentMap != null && !attachmentMap.isEmpty()) {
            for (Long attachmentId : attachmentMap.keySet()) {
                List<Object> attachmentList = attachmentMap.get(attachmentId);
                for (Object attachment : attachmentList) {
                    JSONObject attachmentObject = (JSONObject) attachment;
                    File file = (File) attachmentObject.get("attachedFiles");
                    String fileName = (String) attachmentObject.get("attachedFilesFileName");
                    String contentType = (String) attachmentObject.get("attachedFilesContentType");
                    int[] resize = {80, 120, 360};
                    long fileId = fs.addFile(fileName, file, contentType, resize);
                    AttachmentContext attachmentContext = new AttachmentContext();
                    attachmentContext.setFileId(fileId);
                    attachmentContext.setCreatedTime(System.currentTimeMillis());
                    attachmentContext.setParentId(attachmentId);
                    attachments.add(attachmentContext);
                }

            }
            AttachmentsAPI.addAttachments(attachments, "mailAttachments");
            List<Long> attachmentIds = new ArrayList<>();
            for (AttachmentContext ac : attachments) {
                attachmentIds.add(ac.getId());
            }

            attachments = AttachmentsAPI.getAttachments("mailAttachments", attachmentIds);
            context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, attachments);
        }
        return false;
    }
}
