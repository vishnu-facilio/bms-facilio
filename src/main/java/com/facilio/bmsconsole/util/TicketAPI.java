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
