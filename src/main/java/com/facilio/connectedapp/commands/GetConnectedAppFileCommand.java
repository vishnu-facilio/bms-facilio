package com.facilio.connectedapp.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppFileContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import com.facilio.fw.FacilioException;
import org.apache.commons.chain.Context;

public class GetConnectedAppFileCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long connectedAppId = (Long) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID);
        Long connectedAppFileId = (Long) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE_ID);
        if (connectedAppFileId == null || connectedAppFileId < 0) {
            throw new FacilioException("Connected App File ID param is mandatory.");
        }
        ConnectedAppFileContext connectedAppFile = ConnectedAppHostingAPI.getConnectedAppFile(connectedAppId, connectedAppFileId);
        if (connectedAppFile != null) {
            connectedAppFile.setSysCreatedByUser(AccountUtil.getUserBean(AccountUtil.getCurrentOrg().getId()).getUser(connectedAppFile.getSysCreatedBy(), true));
            connectedAppFile.setSysModifiedByUser(AccountUtil.getUserBean(AccountUtil.getCurrentOrg().getId()).getUser(connectedAppFile.getSysModifiedBy(), true));
        }

        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_FILE, connectedAppFile);

        return false;
    }
}
