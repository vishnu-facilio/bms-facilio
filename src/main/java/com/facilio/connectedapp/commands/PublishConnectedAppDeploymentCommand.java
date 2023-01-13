package com.facilio.connectedapp.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppDeploymentContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import com.facilio.fw.FacilioException;
import org.apache.commons.chain.Context;

public class PublishConnectedAppDeploymentCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long connectedAppId = (Long) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID);
        ConnectedAppDeploymentContext deploymentContext = (ConnectedAppDeploymentContext) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT);
        ConnectedAppDeploymentContext latestDeployment = ConnectedAppHostingAPI.getLatestConnectedAppDeployment(connectedAppId);
        if (deploymentContext.getId() > 0) {
            ConnectedAppDeploymentContext publishedDeployment = ConnectedAppHostingAPI.publishConnectedAppDeployment(connectedAppId, deploymentContext.getId(), deploymentContext.getComments());
            context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT, publishedDeployment);
        }
        else {
            if (ConnectedAppHostingAPI.isChangesAvailable(connectedAppId)) {
                ConnectedAppDeploymentContext publishedDeployment = ConnectedAppHostingAPI.addConnectedAppDeployment(connectedAppId, true, deploymentContext.getComments());
                context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT, publishedDeployment);
            }
            else {
                if (latestDeployment != null && !latestDeployment.isPublished()) {
                    ConnectedAppDeploymentContext publishedDeployment = ConnectedAppHostingAPI.publishConnectedAppDeployment(connectedAppId, latestDeployment.getId(), deploymentContext.getComments());
                    context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT, publishedDeployment);
                }
                else {
                    throw new FacilioException("No changes available to publish.");
                }
            }
        }
        return false;
    }
}
