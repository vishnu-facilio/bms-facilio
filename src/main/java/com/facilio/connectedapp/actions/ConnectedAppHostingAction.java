package com.facilio.connectedapp.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.connectedapp.chain.ConnectedAppHostingChain;
import com.facilio.connectedapp.context.ConnectedAppDeploymentContext;
import com.facilio.connectedapp.context.ConnectedAppFileContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.FacilioException;
import com.facilio.services.factory.FacilioFactory;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class ConnectedAppHostingAction extends FacilioAction {

    private long connectedAppId;
    private long connectedAppFileId;
    private ConnectedAppFileContext connectedAppFile;
    private ConnectedAppDeploymentContext connectedAppDeployment;

    public String isInternalHostingEnabled() throws Exception {
        setResult("isInternalHostingEnabled", ConnectedAppHostingAPI.isInternalHostingEnabled());
        return SUCCESS;
    }

    public String addFile() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getAddConnectedAppFileChain();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());
        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE, getConnectedAppFile());

        chain.execute();

        connectedAppFile.setContent(null); // resetting file in response
        setResult(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE, connectedAppFile);
        return SUCCESS;
    }

    public String updateFile() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getUpdateConnectedAppFileChain();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());
        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE, getConnectedAppFile());

        chain.execute();

        setResult(ConnectedAppHostingAPI.Constants.ROWS_UPDATED, context.get(ConnectedAppHostingAPI.Constants.ROWS_UPDATED));
        return SUCCESS;
    }

    public String deleteFile() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getDeleteConnectedAppFileChain();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());
        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE, getConnectedAppFile());

        chain.execute();

        setResult(ConnectedAppHostingAPI.Constants.ROWS_UPDATED, context.get(ConnectedAppHostingAPI.Constants.ROWS_UPDATED));
        return SUCCESS;
    }

    public String getFile() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getConnectedAppFileChain();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());
        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE_ID, getConnectedAppFileId());

        chain.execute();

        setResult(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE, context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE));
        return SUCCESS;
    }

    private InputStream downloadStream;
    public InputStream getDownloadStream() {
        return downloadStream;
    }
    public void setDownloadStream(InputStream downloadStream) {
        this.downloadStream = downloadStream;
    }

    public String downloadFile() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getConnectedAppFileChain();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());
        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE_ID, getConnectedAppFileId());

        chain.execute();

        ConnectedAppFileContext connectedAppFile = (ConnectedAppFileContext) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE);
        if (connectedAppFile != null && !connectedAppFile.isDirectory()) {
            if (connectedAppFile.getFileId() > 0) {
                downloadStream = FacilioFactory.getFileStore().readFile(connectedAppFile.getFileId());
            }
            else {
                // empty file
                downloadStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
            }
            setContentType(connectedAppFile.getContentType());
            setFilename(connectedAppFile.getFileName());
            return SUCCESS;
        }
        else {
            throw new FacilioException("File not found.");
        }
    }

    public String getFileList() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getConnectedAppFilesListChain();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());

        chain.execute();

        setResult(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILES, context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILES));
        return SUCCESS;
    }

    public String fetchTree() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getFetchConnectedAppFileTree();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());

        chain.execute();

        setResult(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE, context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE));
        return SUCCESS;
    }

    public String canDeploy() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getCheckConnectedAppChangesChain();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());

        chain.execute();

        setResult(ConnectedAppHostingAPI.Constants.CAN_DEPLOY, context.get(ConnectedAppHostingAPI.Constants.CAN_DEPLOY));
        setResult(ConnectedAppHostingAPI.Constants.CAN_PUBLISH, context.get(ConnectedAppHostingAPI.Constants.CAN_PUBLISH));
        return SUCCESS;
    }

    public String createDeploy() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getCreateConnectedAppDeployment();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());

        chain.execute();

        setResult(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT, context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT));
        return SUCCESS;
    }

    public String publishDeploy() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getPublishConnectedAppDeployment();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());
        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT, getConnectedAppDeployment());

        chain.execute();

        setResult(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT, context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT));
        return SUCCESS;
    }

    public String listDeployments() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getConnectedAppDeploymentListChain();
        FacilioContext context = chain.getContext();

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID, getConnectedAppId());
        context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());

        chain.execute();

        setResult(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT_LIST, context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT_LIST));
        return SUCCESS;
    }

    public String downloadConnectedApp() throws Exception {
        FacilioChain chain = ConnectedAppHostingChain.getConnectedAppDownloadChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ID, getConnectedAppId());

        chain.execute();

        ConnectedAppContext connectedApp = (ConnectedAppContext) context.get(FacilioConstants.ContextNames.CONNECTED_APP);
        File zipFile = (File) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ZIP_FILE);
        downloadStream = new FileInputStream(zipFile);
        setFilename(connectedApp.getLinkName() + ".zip");
        setContentType("application/zip");
        return SUCCESS;
    }
}
