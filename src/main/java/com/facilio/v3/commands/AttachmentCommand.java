package com.facilio.v3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttachmentCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(AttachmentCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub

        List<File> attachmentList = Constants.getAttachmentFileList(context);
        List<String> attachmentName = Constants.getAttachmentFileNames(context);
        List<String> attachmentContentType = Constants.getAttachmentContentTypes(context);

        FileStore fs = FacilioFactory.getFileStore();

        Map<String, Long> attachmentNameVsId = new HashMap<>();

        if (attachmentList != null && !attachmentList.isEmpty()) {
            for (int i=0; i< attachmentList.size(); i++) {
                File file = attachmentList.get(i);
                String fileName = attachmentName.get(i);
                String contentType = attachmentContentType.get(i);
                int[] resize = {80, 120, 360};
                long fileId = fs.addOrphanedFile(fileName, file, contentType, resize);
                LOGGER.log(Level.SEVERE,"file id " + fileId);
                attachmentNameVsId.put(fileName, fileId);
            }

            Constants.setAttachmentNameVsId(context, attachmentNameVsId);
        }
        return false;
    }
}
