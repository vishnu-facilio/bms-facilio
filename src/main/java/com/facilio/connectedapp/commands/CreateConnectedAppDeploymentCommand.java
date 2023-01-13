package com.facilio.connectedapp.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppDeploymentContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import com.facilio.fw.FacilioException;
import org.apache.commons.chain.Context;

public class CreateConnectedAppDeploymentCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long connectedAppId = (Long) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID);
        boolean isChangesAvailabe = ConnectedAppHostingAPI.isChangesAvailable(connectedAppId);
        if (!isChangesAvailabe) {
            throw new FacilioException("No changes available to deploy.");
        }
        ConnectedAppDeploymentContext deploymentContext = ConnectedAppHostingAPI.addConnectedAppDeployment(connectedAppId, false, null);
        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT, deploymentContext);
        return false;
    }
}
