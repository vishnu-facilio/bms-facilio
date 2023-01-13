package com.facilio.connectedapp.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppDeploymentContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import org.apache.commons.chain.Context;

public class CheckConnectedAppChangesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long connectedAppId = (Long) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID);
        ConnectedAppDeploymentContext latestVersion = ConnectedAppHostingAPI.getLatestConnectedAppDeployment(connectedAppId);
        boolean canDeploy = ConnectedAppHostingAPI.isChangesAvailable(connectedAppId, latestVersion);
        boolean canPublish = canDeploy || (latestVersion == null || !latestVersion.isPublished());

        context.put(ConnectedAppHostingAPI.Constants.CAN_DEPLOY, canDeploy);
        context.put(ConnectedAppHostingAPI.Constants.CAN_PUBLISH, canPublish);
        return false;
    }
}
