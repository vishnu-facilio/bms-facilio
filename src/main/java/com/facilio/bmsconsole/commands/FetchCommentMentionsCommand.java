package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.CommentMentionContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchCommentMentionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
            if (notes != null && notes.size() > 0) {
                for (NoteContext note : notes) {
                    List<CommentMentionContext> mentions = NotesAPI.getNoteMentions(note.getId(), moduleName);
                    if( mentions != null && !mentions.isEmpty()) {
                        note.setMentions(mentions);
                    }
                }
            }
        return false;
    }
}
