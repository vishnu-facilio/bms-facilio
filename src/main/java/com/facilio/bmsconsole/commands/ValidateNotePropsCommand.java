package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class ValidateNotePropsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		NoteContext noteContext = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
		
		if(noteContext.getBody() != null && !noteContext.getBody().isEmpty()) {
			noteContext.setBody(noteContext.getBody().trim());
		}
		else {
			throw new IllegalArgumentException("Note body is invalid");
		}
		
		if(noteContext.getTitle() != null && !noteContext.getTitle().isEmpty()) {
			noteContext.setTitle(noteContext.getTitle().trim());
		}
		
		return false;
	}
	
}
