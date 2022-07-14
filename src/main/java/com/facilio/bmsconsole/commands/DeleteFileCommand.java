package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;

public class DeleteFileCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        long fileId = (long) context.get(FacilioConstants.ContextNames.FILE_ID);

        if (fileId != -1) {
            FileStore fileStore = FacilioFactory.getFileStore();
            fileStore.deleteFile(fileId);

        }
        return false;
    }
}
