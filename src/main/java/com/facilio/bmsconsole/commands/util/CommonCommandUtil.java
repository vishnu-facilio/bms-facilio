package com.facilio.bmsconsole.commands.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.view.FacilioView;

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
	
	public static FacilioField getFieldFromRS(ResultSet rs) throws SQLException {
		FacilioField field = new FacilioField();
		field.setFieldId(rs.getLong("FIELDID"));
		field.setOrgId(rs.getLong("ORGID"));
		field.setModuleId(rs.getLong("MODULEID"));
		field.setName(rs.getString("NAME"));
		field.setDisplayName(rs.getString("DISPLAY_NAME"));
		field.setColumnName(rs.getString("COLUMN_NAME"));
		field.setSequenceNumber(rs.getInt("SEQUENCE_NUMBER"));
		field.setDataType(FieldType.getCFType(rs.getInt("DATA_TYPE")));
		field.setDataTypeCode(rs.getInt("DATA_TYPE"));
		
		return field;
	}
	
	public static FacilioView getViewFromRS(ResultSet rs) throws SQLException {
		FacilioView view = new FacilioView();
		view.setViewId(rs.getLong("VIEWID"));
		view.setOrgId(rs.getLong("ORGID"));
		view.setName(rs.getString("NAME"));
		view.setDisplayName(rs.getString("DISPLAY_NAME"));
		view.setModuleId(rs.getLong("MODULEID"));
		view.setCriteriaId(rs.getLong("CRITERIAID"));
		return view;
	}
}
