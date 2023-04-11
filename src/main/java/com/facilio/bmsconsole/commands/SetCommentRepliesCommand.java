package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetCommentRepliesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
        Map<Long, NoteContext> notesMap = notes.stream().collect(Collectors.toMap(note ->  note.getId(), note -> note));
        ArrayList<NoteContext>movedNotes = new ArrayList<>();
        for (NoteContext note: notes) {
            if(note.getParentNote()!= null){
                if(notesMap.containsKey(note.getParentNote().getId())){
                    NoteContext parentNote = notesMap.get(note.getParentNote().getId());
                    parentNote.addReply(note);

                }
                movedNotes.add(note);
            }
        }
        notes.removeAll(movedNotes);
        return false;
    }
}
