package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class AddAttachmentTicketActivityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(FacilioConstants.ContextNames.TICKET_ATTACHMENTS.equals(moduleName)) {
			long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			List<AttachmentContext> attachments = (List<AttachmentContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_LIST);
			EventType activityType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			if(attachments != null && !attachments.isEmpty() && recordId != -1 && activityType == EventType.ADD_TICKET_ATTACHMENTS) {
				context.put(FacilioConstants.TicketActivity.MODIFIED_TIME, System.currentTimeMillis());
				context.put(FacilioConstants.TicketActivity.MODIFIED_USER,AccountUtil.getCurrentUser().getId());
				addActivity(context);
			}
		}
		return false;
	}
	
	private void addActivity(Context context) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, RuntimeException {
		List<AttachmentContext> attachments = (List<AttachmentContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_LIST);
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		TicketActivity activity = new TicketActivity();
		activity.setTicketId(recordId);
		activity.setModifiedTime((long) context.get(FacilioConstants.TicketActivity.MODIFIED_TIME));
		activity.setModifiedBy((long) context.get(FacilioConstants.TicketActivity.MODIFIED_USER));
		activity.setActivityType((EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE));
		activity.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		JSONObject info = new JSONObject();
		JSONArray attachmentNames = new JSONArray();
		
		for(AttachmentContext attachment : attachments) {
			attachmentNames.add(attachment.getFileName());
		}
		info.put("attachments", attachmentNames);
		activity.setInfo(info);
		
		GenericInsertRecordBuilder insertActivityBuilder = new GenericInsertRecordBuilder()
																.table(ModuleFactory.getTicketActivityModule().getTableName())
																.fields(FieldFactory.getTicketActivityFields())
																.addRecord(FieldUtil.getAsProperties(activity));
		insertActivityBuilder.save();
	}
}
