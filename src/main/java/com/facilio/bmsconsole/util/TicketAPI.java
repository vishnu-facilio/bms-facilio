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

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class TicketAPI {
	
	private static Logger logger = Logger.getLogger(TicketAPI.class.getName());
	
	public static List<AttachmentContext> getRelatedAttachments(long ticketId, Connection conn) throws SQLException 
	{
		List<AttachmentContext> attachments = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT * FROM File "
					+ " INNER JOIN Ticket_Attachment ON File.FILE_ID = Ticket_Attachment.FILE_ID"
					+ " WHERE Ticket_Attachment.TICKET_ID = ?");
			pstmt.setLong(1, ticketId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				AttachmentContext ac = new AttachmentContext();
				ac.setAttachmentId(rs.getLong("TICKET_ATTACHMENT_ID"));
				ac.setFileId(rs.getLong("FILE_ID"));
				ac.setOrgId(rs.getLong("ORGID"));
				ac.setFileName(rs.getString("FILE_NAME"));
				ac.setFileSize(rs.getLong("FILE_SIZE"));
				ac.setContentType(rs.getString("CONTENT_TYPE"));
				ac.setUploadedBy(rs.getLong("UPLOADED_BY"));
				ac.setUploadedTime(rs.getLong("UPLOADED_TIME"));
				attachments.add(ac);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting related attachments." +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return attachments;
	}
	
	public static TicketStatusContext getStatus(long orgId, String status) throws Exception
	{
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection())
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
																.connection(conn)
																.table("TicketStatus")
																.moduleName("ticketstatus")
																.beanClass(TicketStatusContext.class)
																.select(modBean.getAllFields("ticketstatus"))
																.andCustomWhere("STATUS = ?", status)
																.orderBy("ID");
			List<TicketStatusContext> statuses = builder.get();
			return statuses.get(0);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static List<TaskContext> getRelatedTasks(long ticketId, Connection conn) throws Exception 
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", OrgInfo.getCurrentOrgInfo().getOrgid());
		List<FacilioField> fields = modBean.getAllFields("task");
		
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
				.connection(conn)
				.table("Tasks")
				.moduleName(FacilioConstants.ContextNames.TASK)
				.beanClass(TaskContext.class)
				.select(fields)
				.andCustomWhere("PARENT_TICKET_ID = ?", ticketId)
				.orderBy("ID");

		List<TaskContext> tasks = builder.get();	
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
				nc.setCreationTime(rs.getLong("CREATION_TIME"));
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
	
	public static long addWorkOrderNote(long ticketId, long noteId, Connection conn) throws Exception
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
				throw new RuntimeException("Unable to add Ticket note");
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
			logger.log(Level.SEVERE, "Exception while adding work order note" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return areaId;
	}
	
	public static void loadRelatedModules(TicketContext ticket, Connection conn) throws Exception {
		if(ticket != null) {
			ticket.setTasks(getRelatedTasks(ticket.getId(), conn));
			ticket.setNotes(getRelatedNotes(ticket.getId(), conn));
			ticket.setAttachments(getRelatedAttachments(ticket.getId(), conn));
		}
	}
}
