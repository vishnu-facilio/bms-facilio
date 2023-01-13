package com.facilio.connectedapp.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppFileContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import com.facilio.fw.FacilioException;
import org.apache.commons.chain.Context;

public class DeleteConnectedAppFileCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long connectedAppId = (Long) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID);
        ConnectedAppFileContext connectedAppFile = (ConnectedAppFileContext) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE);
        if (connectedAppFile == null || connectedAppFile.getId() < 0) {
            throw new FacilioException("Connected App File ID param is mandatory.");
        }
        int rowsUpdated = ConnectedAppHostingAPI.deleteConnectedAppFile(connectedAppId, connectedAppFile.getId());
        context.put(ConnectedAppHostingAPI.Constants.ROWS_UPDATED, rowsUpdated);

        return false;
    }
}
