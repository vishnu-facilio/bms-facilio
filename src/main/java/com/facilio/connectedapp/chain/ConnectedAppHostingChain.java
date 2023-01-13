package com.facilio.connectedapp.chain;

import com.facilio.bmsconsole.commands.GetConnectedAppCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.connectedapp.commands.*;

public class ConnectedAppHostingChain {

    public static FacilioChain getAddConnectedAppFileChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new UploadConnectedAppFileCommand());
        chain.addCommand(new AddConnectedAppFileCommand());
        return chain;
    }

    public static FacilioChain getUpdateConnectedAppFileChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new UploadConnectedAppFileCommand());
        chain.addCommand(new UpdateConnectedAppFileCommand());
        return chain;
    }

    public static FacilioChain getDeleteConnectedAppFileChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new DeleteConnectedAppFileCommand());
        return chain;
    }

    public static FacilioChain getConnectedAppFileChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GetConnectedAppFileCommand());
        return chain;
    }

    public static FacilioChain getFetchConnectedAppFileTree() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new FetchConnectedAppFileTreeCommand());
        return chain;
    }

    public static FacilioChain getConnectedAppFilesListChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GetConnectedAppFilesListCommand());
        return chain;
    }

    public static FacilioChain getCheckConnectedAppChangesChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new CheckConnectedAppChangesCommand());
        return chain;
    }

    public static FacilioChain getCreateConnectedAppDeployment() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new CreateConnectedAppDeploymentCommand());
        return chain;
    }

    public static FacilioChain getPublishConnectedAppDeployment() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new PublishConnectedAppDeploymentCommand());
        return chain;
    }

    public static FacilioChain getConnectedAppDeploymentListChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GetConnectedAppDeploymentListCommand());
        return chain;
    }

    public static FacilioChain getConnectedAppDownloadChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GetConnectedAppCommand());
        chain.addCommand(new DownloadConnectedAppCommand());
        return chain;
    }
}
