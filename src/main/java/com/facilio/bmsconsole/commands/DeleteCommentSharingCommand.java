package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class DeleteCommentSharingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if (note != null && note.getId() > 0) {
            NotesAPI.deleteCommentSharing(note,moduleName);
        }
        return false;
    }
}
