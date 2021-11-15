package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;

public class FetchNoteCommand extends FacilioCommand implements PostTransactionCommand {

	@Override
	public boolean postExecute() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long noteId = (Long) context.get(FacilioConstants.ContextNames.NOTE_ID);
	    String moduleName= (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		NoteContext note = NotesAPI.getNoteContext(noteId, moduleName);
		long id=note.getId();
		context.put(FacilioConstants.ContextNames.NOTE, note);
		return false;
	}
	

}
