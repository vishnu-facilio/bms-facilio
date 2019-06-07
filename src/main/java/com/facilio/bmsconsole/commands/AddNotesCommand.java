package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.ItemActivityType;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AddNotesCommand implements Command, PostTransactionCommand {

	private Set<Long> idsToUpdateCount;
	private String ticketModuleName;
	private String moduleName;

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
		if (notes == null) {
			NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
			if (note != null) {
				notes = Collections.singletonList(note);
				context.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
			}
		}
		
		if(notes != null && !notes.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String ticketModule = null;
			if (moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
				ticketModule = (String) context.get(FacilioConstants.ContextNames.TICKET_MODULE);
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ADD_TICKET_NOTE);
				if (ticketModule == null || ticketModule.isEmpty()) {
					throw new IllegalArgumentException("Module name for ticket notes should be specified");
				}
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<NoteContext> noteBuilder = new InsertRecordBuilder<NoteContext>()
					.module(module)
					.fields(fields)
					;
			
			Set<Long> parentIds = new HashSet<>();
			for (NoteContext note : notes) {
				if (StringUtils.isEmpty(note.getBody())) {
					throw new IllegalArgumentException("Comment cannot be null/ empty");
				}

				if (note.getCreatedTime() == -1) {
					note.setCreatedTime(System.currentTimeMillis());
				}
				note.setCreatedBy(AccountUtil.getCurrentUser());
				
				parentIds.add(note.getParentId());
				JSONObject info = new JSONObject();
				info.put("Comment", note.getBody());
	     		if(moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
				CommonCommandUtil.addActivityToContext(note.getParentId(), -1, WorkOrderActivityType.ADD_COMMENT, info, (FacilioContext) context);
	     		}
	     		else if(moduleName.equals(FacilioConstants.ContextNames.ITEM_TYPES_NOTES)) {
				CommonCommandUtil.addActivityToContext(note.getParentId(), -1, ItemActivityType.ITEM_NOTES, info, (FacilioContext) context);
	     		}
	     		else if(moduleName.equals(FacilioConstants.ContextNames.ASSET_NOTES)) {
	     		}
				
				noteBuilder.addRecord(note);
				if(moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
					sendEmail(moduleName, ticketModule, note);
				}
			}
			idsToUpdateCount = parentIds;
			this.ticketModuleName = (String) context.get(FacilioConstants.ContextNames.TICKET_MODULE);
			this.moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
//			FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.IDS_TO_UPDATE_COUNT, parentIds);
//			FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.TICKET_MODULE, context.get(FacilioConstants.ContextNames.TICKET_MODULE));
//			FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.MODULE_NAME, context.get(FacilioConstants.ContextNames.MODULE_NAME));
			noteBuilder.save();
		}
		return false;
	}
	
	@Override
	public boolean postExecute() throws Exception {
		NotesAPI.updateNotesCount(idsToUpdateCount, ticketModuleName, moduleName);
		return false;
	}
	
	private void sendEmail(String moduleName, String ticketModule, NoteContext note) throws Exception {
		if (note.getNotifyRequester()) {
			TicketContext ticket = TicketAPI.getParentTicket(note.getParentId(), ticketModule);
			
			User requester = null;
			if (ticket instanceof WorkOrderContext) {
				requester = ((WorkOrderContext) ticket).getRequester();
			}
			else if (ticket instanceof WorkOrderRequestContext) {
				requester = ((WorkOrderRequestContext) ticket).getRequester();
			}
			if (requester == null) {
				return ;
			}
			
			UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
			requester = userBean.getUser(requester.getId());
			
			if (requester.getEmail() != null) { //This has to be changed to support any notification
				JSONObject mailJson = new JSONObject();
				mailJson.put("sender", "support@facilio.com");
				mailJson.put("to", requester.getEmail());
				mailJson.put("subject", AccountUtil.getCurrentUser().getName() + " commented on your request");
				mailJson.put("message", note.getBody());
				AwsUtil.sendEmail(mailJson);
			}
		}
	}
}
