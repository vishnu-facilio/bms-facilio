package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.chargebee.models.Invoice.Note;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.constants.FacilioConstants;

public class ConstructTicketNotesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String comment = (String) context.get(FacilioConstants.ContextNames.COMMENT);
		if (comment != null && !comment.isEmpty()) {
			List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			if (recordIds == null) {
				Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				if (recordId != null && recordId > 0) {
					recordIds = Collections.singletonList(recordId);
				}
			}
			
			if (recordIds != null && !recordIds.isEmpty()) {
				List<NoteContext> notes = new ArrayList<>();
				for (Long recordId : recordIds) {
					NoteContext note = new NoteContext();
					note.setBody(comment);
					note.setParentId(recordId);
					
					notes.add(note);
				}
				
				String ticketModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
				context.put(FacilioConstants.ContextNames.TICKET_MODULE, ticketModuleName);
				context.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
			}
		}
		
		return false;
	}

}
