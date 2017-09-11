package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.FileContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

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
	
	public static Long getStatusId(long orgId, String status) throws Exception
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
			return (Long) statuses.get(0).getId();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
