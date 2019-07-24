package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;

public class AssignTicketCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Long ticketId = (Long) context.get(FacilioConstants.ContextNames.TICKET_ID);
		Long assignedTo = (Long) context.get(FacilioConstants.ContextNames.ASSIGNED_TO_ID);
		
		if(ticketId != null) {
			context.get(FacilioConstants.ContextNames.MODULE_NAME);
			context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			Connection conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement("UPDATE Tickets SET ASSIGNED_TO_ID=? WHERE TICKETID=?");
				if (assignedTo <= 0) {
					pstmt.setNull(1, Types.BIGINT);
				}
				else {
					pstmt.setLong(1, assignedTo);
				}
				pstmt.setLong(2, ticketId);
				
				if (pstmt.executeUpdate() < 1) {
					return false;
				}
				return true;
			}
			catch(SQLException e) {
				throw e;
			}
			finally {
				DBUtil.closeAll(conn, pstmt);
			}
		}
		else {
			throw new IllegalArgumentException("Ticket ID cannot be null");
		}
	}

}
