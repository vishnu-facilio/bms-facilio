package com.facilio.bmsconsole.commands.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;

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
	
}
