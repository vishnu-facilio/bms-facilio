package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

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
				setRepliesMeta(noteListContext,moduleName);
				Map<Long, List<NoteContext>> map = new HashMap<>();

				for (NoteContext note : noteListContext) {
					if (!(note.getCreatedBy().getEmail().contains("system+"))) {
						map.computeIfAbsent(note.getParentId(), k -> new ArrayList<>()).add(note);
					}
				}
				context.put(FacilioConstants.ContextNames.NOTE_MAP, map);
				context.put(FacilioConstants.ContextNames.NOTE_LIST, map.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
				context.put(FacilioConstants.ContextNames.NEED_COMMENT_SHARING, false);

			}
			else {
				
			noteListContext = NotesAPI.fetchNotes(parentId,parentNoteId, moduleName,onlyFetchParentNotes);
			setRepliesMeta(noteListContext,moduleName);
			context.put(FacilioConstants.ContextNames.NOTE_LIST, noteListContext);
			context.put(FacilioConstants.ContextNames.NEED_COMMENT_SHARING, true);
			}
		
		}
		return false;
	}
	public List<NoteContext> setRepliesMeta(List<NoteContext> noteListContext, String moduleName) throws Exception {
		if(noteListContext.isEmpty()){
			return null;
		}
		for (NoteContext note: noteListContext) {
			long repliesCount = NotesAPI.fetchNotesCount(note.getParentId(), note.getId(), moduleName);
			note.setReplyCount(repliesCount);
			NoteContext lastReply = NotesAPI.getLastReply(note.getParentId(), note.getId(), moduleName);
			note.setLastReply(lastReply);
		}
		return noteListContext;
	}
}
