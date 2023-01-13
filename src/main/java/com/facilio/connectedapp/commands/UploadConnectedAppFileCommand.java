package com.facilio.connectedapp.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppFileContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import com.facilio.fw.FacilioException;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.filestore.FileStoreFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import java.nio.charset.StandardCharsets;

public class UploadConnectedAppFileCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ConnectedAppFileContext connectedAppFile = (ConnectedAppFileContext) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE);
        if (connectedAppFile.getContent() != null && connectedAppFile.getContentFileName() != null && connectedAppFile.getContentContentType() != null) {
            if (connectedAppFile.isDirectory()) {
                throw new FacilioException("File content cannot be uploaded to directory.");
            }

            long fileSize = connectedAppFile.getContent().length();
            if (fileSize > ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE_MAX_SIZE) {
                throw new FacilioException("File size cannot be more than 2MB.");
            }
            long fileId = FileStoreFactory.getInstance().getFileStore().addFile(connectedAppFile.getContentFileName(), connectedAppFile.getContent(), connectedAppFile.getContentContentType());
            connectedAppFile.setFileId(fileId);
            connectedAppFile.setFileName(connectedAppFile.getContentFileName());
            connectedAppFile.setContentType(connectedAppFile.getContentContentType());
            connectedAppFile.setFileSize(fileSize);
        }
        else if (connectedAppFile.getRawContent() != null && connectedAppFile.getFileName() != null && connectedAppFile.getContentType() != null) {
            if (connectedAppFile.isDirectory()) {
                throw new FacilioException("File content cannot be uploaded to directory.");
            }

            byte[] byteContent = connectedAppFile.getRawContent().getBytes(StandardCharsets.UTF_8);
            long fileSize = byteContent.length;
            if (fileSize > ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE_MAX_SIZE) {
                throw new FacilioException("File size cannot be more than 2MB.");
            }
            long fileId = FileStoreFactory.getInstance().getFileStore().addFile(FileStore.DEFAULT_NAMESPACE, connectedAppFile.getFileName(), byteContent, connectedAppFile.getContentType(), false);
            connectedAppFile.setFileId(fileId);
            connectedAppFile.setFileSize(fileSize);
        }

        return false;
    }
}
