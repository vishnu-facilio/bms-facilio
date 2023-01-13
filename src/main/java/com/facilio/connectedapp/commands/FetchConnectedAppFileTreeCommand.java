package com.facilio.connectedapp.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppFileContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import org.apache.commons.chain.Context;

public class FetchConnectedAppFileTreeCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long connectedAppId = (Long) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID);
        ConnectedAppFileContext rootFolder = ConnectedAppHostingAPI.getConnectedAppFileTree(connectedAppId);

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE, rootFolder);

        return false;
    }
}
