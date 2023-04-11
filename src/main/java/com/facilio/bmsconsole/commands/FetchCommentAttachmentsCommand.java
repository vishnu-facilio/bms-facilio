package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.CommentAttachmentContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FetchCommentAttachmentsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
            if (notes != null && notes.size() > 0) {
                Map<Long, List<CommentAttachmentContext>> commentAttachments = AttachmentsAPI.getCommentAttachments(notes, moduleName);
                Set<Long> noteIds = commentAttachments.keySet();
                for (long noteId: noteIds) {
                    List<NoteContext> filteredNote = notes.stream().filter(note -> note.getId() == noteId).collect(Collectors.toList());
                    filteredNote.get(0).setAttachments(commentAttachments.get(noteId));
                }
            }
        return false;
    }
}
