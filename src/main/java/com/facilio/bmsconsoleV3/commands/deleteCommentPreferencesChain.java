package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class deleteCommentPreferencesChain extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        NotesAPI.deleteDefaultCommentSharingApps();
        return false;
    }
}
