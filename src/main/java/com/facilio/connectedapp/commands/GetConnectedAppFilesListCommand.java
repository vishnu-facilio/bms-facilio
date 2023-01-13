package com.facilio.connectedapp.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppFileContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetConnectedAppFilesListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long connectedAppId = (Long) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID);
        List<ConnectedAppFileContext> filesList = ConnectedAppHostingAPI.getConnectedAppFilesList(connectedAppId);

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILES, filesList);

        return false;
    }
}
