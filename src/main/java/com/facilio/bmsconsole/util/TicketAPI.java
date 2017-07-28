package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.FileContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.sql.DBUtil;

public class TicketAPI {
	
	private static Logger logger = Logger.getLogger(TicketAPI.class.getName());
	
	public static Long addTicketTask(Long ticketId, Long taskId, Connection conn) throws Exception
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long areaId = null;
		try
		{
			pstmt = conn.prepareStatement("INSERT INTO Ticket_Task (TICKET_ID, TASK_ID) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, ticketId);
			pstmt.setLong(2, taskId);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add ticket task");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				areaId = rs.getLong(1);
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while ticket task" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return areaId;
	}
	
	public static Long addTicketNote(Long ticketId, Long noteId, Connection conn) throws Exception
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long areaId = null;
		try
		{
			pstmt = conn.prepareStatement("INSERT INTO Ticket_Note (TICKET_ID, NOTE_ID) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, ticketId);
			pstmt.setLong(2, noteId);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add ticket note");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				areaId = rs.getLong(1);
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while ticket note" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return areaId;
	}

	public static List<TaskContext> getRelatedTasks(long ticketId, Connection conn) throws SQLException 
	{
		List<TaskContext> tasks = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT * FROM Tasks "
					+ " INNER JOIN Ticket_Task ON Tasks.ID = Ticket_Task.TASK_ID"
					+ " WHERE Ticket_Task.TICKET_ID = ?");
			pstmt.setLong(1, ticketId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				TaskContext tc = new TaskContext();
				tc.setId(rs.getLong("ID"));
				tc.setSubject(rs.getString("SUBJECT"));
				tc.setDescription(rs.getString("Description"));
//				tc.setStatusCode(rs.getInt("STATUS"));
//				tc.setAssignedToId(rs.getLong("ASSIGNED_TO_ID"));
				tasks.add(tc);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all tasks" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return tasks;
	}
	
	public static List<NoteContext> getRelatedNotes(long ticketId, Connection conn) throws SQLException 
	{
		List<NoteContext> notes = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT * FROM Notes "
					+ " INNER JOIN Ticket_Note ON Notes.NOTEID = Ticket_Note.NOTE_ID"
					+ " WHERE Ticket_Note.TICKET_ID = ?");
			pstmt.setLong(1, ticketId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				NoteContext nc = new NoteContext();
				nc.setNoteId(rs.getLong("NOTE_ID"));
				nc.setBody(rs.getString("BODY"));
				notes.add(nc);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all tasks" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return notes;
	}
	
	public static List<FileContext> getRelatedAttachments(long ticketId, Connection conn) throws SQLException 
	{
		List<FileContext> attachments = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT * FROM File "
					+ " INNER JOIN Ticket_Attachment ON File.FILE_ID = Ticket_Attachment.FILE_ID"
					+ " WHERE Ticket_Attachment.TICKETID = ?");
			pstmt.setLong(1, ticketId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				FileContext fc = new FileContext();
				fc.setFileId(rs.getLong("FILE_ID"));
				fc.setFileName(rs.getString("FILE_NAME"));
				attachments.add(fc);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all tasks" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return attachments;
	}
}
