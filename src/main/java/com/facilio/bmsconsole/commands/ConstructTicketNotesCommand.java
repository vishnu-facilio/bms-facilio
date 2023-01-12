package com.facilio.bmsconsole.commands;

import java.util.*;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;

import lombok.extern.log4j.Log4j;

@Log4j
public class ConstructTicketNotesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String comment = (String) context.get(FacilioConstants.ContextNames.COMMENT);

		try {
			if (comment == null) {
				HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
				if(recordMap != null) {
					List<V3WorkOrderContext> workOrderContextList = (List<V3WorkOrderContext>) recordMap.get(FacilioConstants.ContextNames.WORK_ORDER);
					if(CollectionUtils.isNotEmpty(workOrderContextList)) {
						ArrayList<LinkedHashMap<String,Object>> commentList = (ArrayList<LinkedHashMap<String, Object>>) workOrderContextList.get(0).getDatum("comment");
						if(commentList != null) {
							comment = (String) commentList.get(0).get("body");
						}
					}
				}
			}
		}
		catch (Exception e){
			LOGGER.error(e.getMessage(),e);
		}

		
		if (comment != null && !comment.isEmpty()) {
			List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			if (recordIds == null) {
				Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				if (recordId != null && recordId > 0) {
					recordIds = Collections.singletonList(recordId);
				}
			}
			
			if (recordIds != null && !recordIds.isEmpty()) {
				boolean notifyRequester = (boolean) context.getOrDefault(FacilioConstants.ContextNames.NOTIFY_REQUESTER, false);
				List<NoteContext> notes = new ArrayList<>();
				for (Long recordId : recordIds) {
					NoteContext note = new NoteContext();
					note.setBody(comment);
					note.setParentId(recordId);
					note.setNotifyRequester(notifyRequester);
					notes.add(note);
				}
				
				String ticketModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
				context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, ticketModuleName);
				context.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
			}
		}
		
		return false;
	}

}
