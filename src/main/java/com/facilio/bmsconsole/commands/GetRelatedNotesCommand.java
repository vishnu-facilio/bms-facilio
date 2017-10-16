package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;

public class GetRelatedNotesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(moduleName == null || recordId == null) {
			return false;
		}
		
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		List<NoteContext> notes = TicketAPI.getRelatedNotes(recordId, conn);
		
		context.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
		return false;
	}

}
