package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddNotesCommand implements Command {

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
				context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ADD_TICKET_NOTE);
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
				note.setCreatedTime(System.currentTimeMillis());
				note.setCreatedBy(AccountUtil.getCurrentUser());
				
				parentIds.add(note.getParentId());
				
				noteBuilder.addRecord(note);
				if(moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
					sendEmail(moduleName, ticketModule, note);
				}
			}
			context.put("counts_to_update", parentIds);
			noteBuilder.save();
		}
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
