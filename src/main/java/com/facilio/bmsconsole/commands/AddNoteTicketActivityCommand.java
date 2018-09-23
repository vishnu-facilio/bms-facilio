package com.facilio.bmsconsole.commands;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.bmsconsole.workflow.rule.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddNoteTicketActivityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
		if(note != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			if(FacilioConstants.ContextNames.TICKET_NOTES.equals(moduleName)) {
				long parentId = note.getParentId();
				ActivityType activityType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
				if(parentId != -1 && activityType != null && activityType == ActivityType.ADD_TICKET_NOTE) {
					context.put(FacilioConstants.TicketActivity.MODIFIED_TIME, System.currentTimeMillis());
					context.put(FacilioConstants.TicketActivity.MODIFIED_USER, AccountUtil.getCurrentUser().getId());
					addActivity(context);
				}
			}
		}
		return false;
	}
		
	private void addActivity(Context context) throws SQLException, RuntimeException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
		
		TicketActivity activity = new TicketActivity();
		activity.setTicketId(note.getParentId());
		activity.setModifiedTime((long) context.get(FacilioConstants.TicketActivity.MODIFIED_TIME));
		activity.setModifiedBy((long) context.get(FacilioConstants.TicketActivity.MODIFIED_USER));
		activity.setActivityType((ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE));
		activity.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		JSONObject info = new JSONObject();
		info.put("content", note.getBody());
		info.put("notifyRequester", note.getNotifyRequester());
		activity.setInfo(info);
		
		GenericInsertRecordBuilder insertActivityBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getTicketActivityModule().getTableName())
															.fields(FieldFactory.getTicketActivityFields())
															.addRecord(FieldUtil.getAsProperties(activity));
		insertActivityBuilder.save();
	}

}
