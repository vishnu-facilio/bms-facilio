package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchCommentSharingCommand extends FacilioCommand  {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Boolean needSharing= (Boolean) context.get(FacilioConstants.ContextNames.NEED_COMMENT_SHARING);
        boolean notesEditAvailable = Boolean.valueOf(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.NOTES_EDIT_AVAILABLE, Boolean.FALSE));
        if(needSharing) {
            List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
            if (notes != null && notes.size() > 0) {
                for (NoteContext note : notes) {
                    note.setCommentSharing(NotesAPI.getNoteSharing(note.getId(), moduleName));
                    if(note.getCreatedBy() != null) {
                        if ((notesEditAvailable || note.getCreatedBy().getId() == AccountUtil.getCurrentUser().getId()) && !AccountUtil.getCurrentApp().getAppCategoryEnum().equals(ApplicationContext.AppCategory.PORTALS)) {
                            note.setEditAvailable(true);
                        }
                    }
                }
            }
        }
        return false;
    }
}
