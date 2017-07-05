package com.facilio.bmsconsole.commands.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.fields.FieldUtil;
import com.facilio.bmsconsole.fields.FacilioField;

public class CommonCommandUtil {
	public static ScheduleContext getScheduleContextFromRS(ResultSet rs) throws SQLException {
		ScheduleContext sc = new ScheduleContext();
		sc.setScheduleId(rs.getLong("SCHEDULEID"));
		sc.setOrgId(rs.getLong("ORGID"));
		sc.setScheduledStartFromTimestamp(rs.getLong("SCHEDULED_START"));
		sc.setEstimatedEndFromTimestamp(rs.getLong("ESTIMATED_END"));
		sc.setActualWorkStartFromTimestamp(rs.getLong("ACTUAL_WORK_START"));
		sc.setActualWorkEndFromTimestamp(rs.getLong("ACTUAL_WORK_END"));
		
		return sc;
	}
	
	public static NoteContext getNoteContextFromRS(ResultSet rs) throws SQLException {
		NoteContext context = new NoteContext();
		
		context.setNoteId(rs.getLong("NOTEID"));
		context.setOrgId(rs.getLong("ORGID"));
		context.setOwnerId(rs.getLong("OWNERID"));
		context.setCreationTime(rs.getLong("CREATION_TIME"));
		context.setTitle(rs.getString("TITLE"));
		context.setBody(rs.getString("BODY"));
		
		return context;
	}
	
	public static TaskContext getTaskObjectFromRS(ResultSet rs, List<FacilioField> fields) throws SQLException {
		TaskContext tc = new TaskContext();
		tc.setOrgId(rs.getLong("ORGID"));
		tc.setTaskId(rs.getLong("taskId"));
		tc.setParent(rs.getLong("parent"));
		tc.setSubject(rs.getString("subject"));
		tc.setDescription(rs.getString("description"));
		tc.setAssignmentGroupId(rs.getLong("assignmentGroupId"));
		tc.setAssignedToId(rs.getLong("assignedToId"));
		tc.setScheduleId(rs.getLong("scheduleId"));
		
		for(int i=TaskContext.DEFAULT_TASK_FIELDS.length; i<fields.size(); i++) {
			FacilioField field = fields.get(i);
			tc.setCustomProp(field.getName(), FieldUtil.getValueAsPerType(field, rs));
		}
		
		return tc;
	}
	
	public static TicketContext getTCObjectFromRS(ResultSet rs, List<FacilioField> fields) throws SQLException {
		TicketContext tc = new TicketContext();
		tc.setOrgId(rs.getLong("ORGID"));
		tc.setTicketId(rs.getLong("ticketId"));
		tc.setRequester(rs.getString("requester"));
		tc.setSubject(rs.getString("subject"));
		tc.setDescription(rs.getString("description"));
		tc.setStatusCode(rs.getInt("status"));
		tc.setAssignedToId(rs.getLong("agentId"));
		tc.setFailedAssetId(rs.getLong("assetId"));
		tc.setDueTimeFromTimestamp(rs.getLong("dueDate"));
		
		for(int i=TicketContext.DEFAULT_TICKET_FIELDS.length; i<fields.size(); i++) {
			FacilioField field = fields.get(i);
			tc.setCustomProp(field.getName(), FieldUtil.getValueAsPerType(field, rs));
		}
		
		return tc;
	}
}
