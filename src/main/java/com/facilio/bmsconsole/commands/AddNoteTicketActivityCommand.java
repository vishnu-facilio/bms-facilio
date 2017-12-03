package com.facilio.bmsconsole.commands;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddNoteTicketActivityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
		if(note != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			if(moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
				long parentId = note.getParentId();
				ActivityType eventType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
				if(parentId != -1 && eventType != null && eventType == ActivityType.ADD_TICKET_NOTE) {
					context.put(FacilioConstants.TicketActivity.MODIFIED_TIME, System.currentTimeMillis());
					context.put(FacilioConstants.TicketActivity.MODIFIED_USER,UserInfo.getCurrentUser().getOrgUserId());
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
		activity.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		
		JSONObject info = new JSONObject();
		info.put("content", note.getBody());
		activity.setInfo(info);
		
		GenericInsertRecordBuilder insertActivityBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getTicketActivityModule().getTableName())
															.fields(FieldFactory.getTicketActivityFields())
															.addRecord(FieldUtil.getAsProperties(activity));
		insertActivityBuilder.save();
	}

}
