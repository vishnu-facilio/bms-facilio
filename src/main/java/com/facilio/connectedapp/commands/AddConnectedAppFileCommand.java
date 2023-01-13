package com.facilio.connectedapp.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppFileContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import org.apache.commons.chain.Context;

public class AddConnectedAppFileCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long connectedAppId = (Long) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID);
        ConnectedAppFileContext connectedAppFile = (ConnectedAppFileContext) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE);
        if (connectedAppFile != null) {
            ConnectedAppHostingAPI.addConnectedAppFile(connectedAppId, connectedAppFile);
        }

        return false;
    }
}
