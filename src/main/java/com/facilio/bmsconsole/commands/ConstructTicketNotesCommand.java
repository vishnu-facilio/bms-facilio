package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FieldUtil;
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
		if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_COMMENTS)){
			ConstructTicketNotesFromBody(context);
		}else {
			ConstructTicketNotes(context);
		}

		return false;
	}

	private static void ConstructTicketNotesFromBody(Context context) {
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
				String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
				context.put("oldParentName", parentModuleName);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
				context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, ticketModuleName);
				context.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
			}
		}
	}

	private static void ConstructTicketNotes(Context context) {
		HashMap<String, Object> commentMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.COMMENT);

		try {
			if (commentMap == null) {
				HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
				if(recordMap != null) {
					List<V3WorkOrderContext> workOrderContextList = (List<V3WorkOrderContext>) recordMap.get(FacilioConstants.ContextNames.WORK_ORDER);
					if(CollectionUtils.isNotEmpty(workOrderContextList)) {
						ArrayList<LinkedHashMap<String,Object>> commentList = (ArrayList<LinkedHashMap<String, Object>>) workOrderContextList.get(0).getDatum("comment");
						if(commentList != null) {
							 commentMap = commentList.get(0);
						}
					}
				}
			}
		}
		catch (Exception e){
			LOGGER.error(e.getMessage(),e);
		}


		if(commentMap != null && !commentMap.isEmpty()) {
			NoteContext comment = FieldUtil.getAsBeanFromMap(commentMap, NoteContext.class);
			if( comment != null && comment.getBody() ==  null || comment.getBody().isEmpty()){
				return;
			}
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
					comment.setParentId(recordId);
					comment.setNotifyRequester(notifyRequester);
					notes.add(comment);
				}
				String ticketModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
				context.put("oldParentName", parentModuleName);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
				context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, ticketModuleName);
				context.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
			}
		}
	}

}
