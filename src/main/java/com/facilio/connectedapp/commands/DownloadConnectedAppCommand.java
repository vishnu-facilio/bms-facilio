package com.facilio.connectedapp.commands;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppFileContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.FacilioException;
import com.facilio.services.factory.FacilioFactory;
import org.apache.commons.chain.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadConnectedAppCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ConnectedAppContext connectedApp = (ConnectedAppContext) context.get(FacilioConstants.ContextNames.CONNECTED_APP);
        if (connectedApp == null) {
            throw new FacilioException("Connected App ID param is missing or invalid.");
        }
        else if (connectedApp.getHostingTypeEnum() != ConnectedAppContext.HostingType.INTERNAL) {
            throw new FacilioException("Only internally hosted connected apps can be downloaded.");
        }

        ConnectedAppFileContext fileTree = ConnectedAppHostingAPI.getConnectedAppFileTree(connectedApp.getId());
        List<ConnectedAppFileContext> filesList = new ArrayList<>();
        getFilesList(fileTree, filesList, 1);

        File tempZipFile = File.createTempFile(connectedApp.getLinkName(), ".zip");
        FileOutputStream fos = new FileOutputStream(tempZipFile);
        try (ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (ConnectedAppFileContext file : filesList){
                if (file.getFileId() < 0) {
                    // may be empty file
                    continue;
                }
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(file.getFilePath());
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                InputStream ins = FacilioFactory.getFileStore().readFile(file.getFileId());
                if (ins != null) {
                    byte[] buffer = new byte[2048];
                    int len;
                    while ((len = ins.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    ins.close();
                }
                zos.closeEntry();
            }
            zos.close();
            fos.close();
        }
        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ZIP_FILE, tempZipFile);
        return false;
    }

    private void getFilesList(ConnectedAppFileContext tree, List<ConnectedAppFileContext> filesList, int nestedCount) {
        List<ConnectedAppFileContext> children = tree.getChildren();
        if (children != null) {
            for (ConnectedAppFileContext child : children) {
                if (child.isDirectory()) {
                    if (nestedCount <= 10) {
                        // 10 nested loops only allowed
                        getFilesList(child, filesList, nestedCount + 1);
                    }
                }
                else {
                    filesList.add(child);
                }
            }
        }
    }
}
