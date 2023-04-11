package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetNotesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(moduleName != null && !moduleName.isEmpty()) {
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			long parentNoteId = (long) context.get(FacilioConstants.ContextNames.PARENT_NOTE_ID);
			Boolean onlyFetchParentNotes =(Boolean) context.get("onlyFetchParentNotes");
			List<Long> notesIds = new ArrayList<>();
			notesIds = (List<Long>) context.get(FacilioConstants.ContextNames.NOTE_IDS);
			List<NoteContext> noteListContext = new ArrayList<NoteContext>();
			if (notesIds != null && !notesIds.isEmpty()) {
			    noteListContext = NotesAPI.fetchNote(notesIds, moduleName);
				setRepliesCount(noteListContext,moduleName);
			    Map<Long, List<NoteContext>> map = new HashMap<>();
			    for (int j = 0; j < noteListContext.size(); j++) {
					if (!(noteListContext.get(j).getCreatedBy().getEmail().contains("system+"))) {
					if (map.containsKey(noteListContext.get(j).getParentId())){
						map.get(noteListContext.get(j).getParentId()).add(noteListContext.get(j));
					}
					else {
						List<NoteContext> temp = new ArrayList<>();
						temp.add(noteListContext.get(j));
						map.put(noteListContext.get(j).getParentId(), temp);
					}
					}
				}
			    context.put(FacilioConstants.ContextNames.NOTE_LIST, map);
				context.put(FacilioConstants.ContextNames.NEED_COMMENT_SHARING, false);
			    
			}
			else {
				
			noteListContext = NotesAPI.fetchNotes(parentId,parentNoteId, moduleName,onlyFetchParentNotes);
			setRepliesCount(noteListContext,moduleName);
			context.put(FacilioConstants.ContextNames.NOTE_LIST, noteListContext);
			context.put(FacilioConstants.ContextNames.NEED_COMMENT_SHARING, true);
			}
		
		}
		return false;
	}
	public List<NoteContext> setRepliesCount(List<NoteContext> noteListContext,String moduleName) throws Exception {
		if(noteListContext.isEmpty()){
			return null;
		}
		for (NoteContext note:noteListContext) { // need to change this to single iteration
			long repliesCount = NotesAPI.fetchNotesCount(note.getParentId(), note.getId(), moduleName);
			note.setReplyCount(repliesCount);
		}
		return noteListContext;
	}

}
