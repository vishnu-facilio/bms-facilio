package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.customfields.FacilioCustomField;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class TaskAPI {
	
	private static final String[] DEFAULT_TASK_FIELDS = new String[] {"PARENT", "SUBJECT", "DESCRIPTION", "ASSIGNED_TO_ID", "SCHEDULE_ID"};
	
	public static long addTask(TaskContext context) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			List<FacilioCustomField> customFields = CFUtil.getCustomFields("Tasks_Objects", "Tasks_Fields", context.getOrgId());
			String sql = CFUtil.constuctInsertStatement("Tasks_Objects", "Tasks_Data", DEFAULT_TASK_FIELDS, customFields, context.getOrgId());
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, context.getParent());
			pstmt.setString(2, context.getSubject());
			pstmt.setString(3, context.getDescription());
			
			if(context.getAssignedToId() != 0) {
				pstmt.setLong(4, context.getAssignedToId());
			}
			else {
				pstmt.setNull(4, Types.BIGINT);
			}
			
			if(context.getScheduleId() != 0) {
				pstmt.setLong(5, context.getScheduleId());
			}
			else {
				pstmt.setNull(5, Types.BIGINT);
			}
			
			CFUtil.appendCustomFieldValues(customFields, DEFAULT_TASK_FIELDS.length, context, pstmt);
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add task");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long taskId = rs.getLong(1);
				System.out.println("Added task with task id : "+taskId);
				return taskId;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static List<NoteContext> getNotesOfTask(long taskId, long orgId) throws SQLException {
		return NoteApi.getNotesOfModule("Tasks_Notes", "TASKID", taskId, orgId);
	}
	
	public static List<TaskContext> getTasksOfTicket(long orgId, long ticketId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			List<FacilioCustomField> customFields = CFUtil.getCustomFields("Tasks_Objects", "Tasks_Fields", orgId);
			String sql = CFUtil.constructSelectStatement("Tasks_Objects", "Tasks_Data", new String[] {"TASKID",  "PARENT", "SUBJECT", "DESCRIPTION", "ASSIGNED_TO_ID", "SCHEDULE_ID"}, customFields, new String[] {"ORGID", "PARENT"});
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement(sql+" ORDER BY SUBJECT");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, ticketId);
			
			List<TaskContext> tasks = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				TaskContext tc = getTaskObjectFromRS(rs, customFields);
				tasks.add(tc);
			}
			
			return tasks;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static long addTaskNotes(long taskId, long noteId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Tasks_Notes (TASKID, NOTEID) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, taskId);
			pstmt.setLong(2, noteId);
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add Task Note");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long taskNoteId = rs.getLong(1);
				System.out.println("Added Task Note with id : "+taskNoteId);
				return taskNoteId;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<TaskContext> getAllTasksOfOrg(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			List<FacilioCustomField> customFields = CFUtil.getCustomFields("Tasks_Objects", "Tasks_Fields", orgId);
			String sql = CFUtil.constructSelectStatement("Tasks_Objects", "Tasks_Data", new String[] {"TASKID",  "PARENT", "SUBJECT", "DESCRIPTION", "ASSIGNED_TO_ID", "SCHEDULE_ID"}, customFields, new String[] {"ORGID"});
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement(sql+" ORDER BY SUBJECT");
			pstmt.setLong(1, orgId);
			
			List<TaskContext> tasks = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				TaskContext tc = getTaskObjectFromRS(rs, customFields);
				tasks.add(tc);
			}
			
			return tasks;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static TaskContext getTaskDetails(long taskId, long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			List<FacilioCustomField> customFields = CFUtil.getCustomFields("Tasks_Objects", "Tasks_Fields", orgId);
			String sql = CFUtil.constructSelectStatement("Tasks_Objects", "Tasks_Data", new String[] {"TASKID",  "PARENT", "SUBJECT", "DESCRIPTION", "ASSIGNED_TO_ID", "SCHEDULE_ID"}, customFields, new String[] {"ORGID", "TASKID"});
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, taskId);
			
			rs = pstmt.executeQuery();
			TaskContext tc = null;
			while(rs.next()) {
				tc = getTaskObjectFromRS(rs, customFields);
				break;
			}
			
			return tc;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private static TaskContext getTaskObjectFromRS(ResultSet rs, List<FacilioCustomField> customFields) throws SQLException {
		TaskContext tc = new TaskContext();
		tc.setTaskId(rs.getLong("TASKID"));
		tc.setOrgId(rs.getLong("ORGID"));
		tc.setParent(rs.getLong("PARENT"));
		tc.setSubject(rs.getString("SUBJECT"));
		tc.setDescription(rs.getString("DESCRIPTION"));
		tc.setAssignedToId(rs.getLong("ASSIGNED_TO_ID"));
		tc.setScheduleId(rs.getLong("SCHEDULE_ID"));
		
		if(customFields != null) {
			for(FacilioCustomField field : customFields) {
				tc.put(field.getFieldName(), rs.getString(field.getFieldName()));
			}
		}
		
		return tc;
	}
}
