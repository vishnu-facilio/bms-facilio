package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NoteApi;

public class AddNoteCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		NoteContext noteContext = (NoteContext) context;
		long noteId = NoteApi.addNote(noteContext);
		noteContext.setNoteId(noteId);
		
		return false;
	}

}
