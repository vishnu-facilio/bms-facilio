package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;

public class AddCommentForServiceRequestCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<V3ServiceRequestContext> serviceRequests = Constants.getRecordList((FacilioContext) context);
		
		List<NoteContext> notes = new ArrayList<>();
		
		boolean notifyRequester = (boolean) context.getOrDefault(FacilioConstants.ContextNames.NOTIFY_REQUESTER, false);
		
		for(V3ServiceRequestContext serviceRequest : serviceRequests) {
			
			if(serviceRequest.getDatum("comment") != null) {
				
				ArrayList comments = (ArrayList)serviceRequest.getDatum("comment");
				
				for(int i=0;i<comments.size();i++) {
					
					Map<String, Object> obj = (Map)comments.get(i);
					NoteContext note = new NoteContext();
					note.setBody((String)obj.get("body"));
					note.setParentId(serviceRequest.getId());
					note.setNotifyRequester(notifyRequester);
					notes.add(note);
				}
			}
		}
		if(notes.size() > 0) {
			
			FacilioChain addNote = TransactionChainFactory.getAddNotesChain();
			
			FacilioContext newContext = addNote.getContext();
			
			newContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.SERVICE_REQUEST_NOTES);
			newContext.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, FacilioConstants.ContextNames.SERVICE_REQUEST);
			newContext.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
			
			addNote.execute();
		}
		
		return false;
	}

}
