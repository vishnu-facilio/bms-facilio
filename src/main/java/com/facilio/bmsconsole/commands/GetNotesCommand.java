package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.constants.FacilioConstants;

public class GetNotesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(moduleName != null && !moduleName.isEmpty()) {
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			List<Long> notesIds = new ArrayList<>();
			notesIds = (List<Long>) context.get(FacilioConstants.ContextNames.NOTE_IDS);
			List<NoteContext> noteListContext = new ArrayList<NoteContext>();
			if (notesIds != null && !notesIds.isEmpty()) {
			    noteListContext = NotesAPI.fetchNote(notesIds, moduleName);
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
				
			noteListContext = NotesAPI.fetchNotes(parentId, moduleName);
			context.put(FacilioConstants.ContextNames.NOTE_LIST, noteListContext);
			context.put(FacilioConstants.ContextNames.NEED_COMMENT_SHARING, true);
			}
		
		}
		return false;
	}

}
