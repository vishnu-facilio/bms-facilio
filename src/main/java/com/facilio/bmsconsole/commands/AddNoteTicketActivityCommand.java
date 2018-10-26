package com.facilio.bmsconsole.commands;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

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
		List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
		if(notes != null && !notes.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			if(FacilioConstants.ContextNames.TICKET_NOTES.equals(moduleName)) {
				
				for (NoteContext note : notes) {
					long parentId = note.getParentId();
					ActivityType activityType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
					if(parentId != -1 && activityType != null && activityType == ActivityType.ADD_TICKET_NOTE) {
						addActivity(note, activityType);
					}
				}
			}
		}
		return false;
	}
		
	private void addActivity(NoteContext note, ActivityType activityType) throws SQLException, RuntimeException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		TicketActivity activity = new TicketActivity();
		activity.setTicketId(note.getParentId());
		activity.setModifiedTime(System.currentTimeMillis());
		activity.setModifiedBy(AccountUtil.getCurrentUser().getId());
		activity.setActivityType(activityType);
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
