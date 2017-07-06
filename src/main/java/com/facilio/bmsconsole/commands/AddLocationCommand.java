package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.fields.FieldUtil;
import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;

public class AddLocationCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TicketContext ticket = (TicketContext) context.get(FacilioConstants.ContextNames.TICKET);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			fields.remove(0);
			
			String sql = FieldUtil.constuctInsertStatement(moduleName, dataTableName, fields);
			
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, ticket.getRequester());
			pstmt.setString(2,  ticket.getSubject());
			pstmt.setString(3, ticket.getDescription());
			pstmt.setInt(4, ticket.getStatusCode());
			
			if(ticket.getAssignedToId() != 0) {
				pstmt.setLong(5, ticket.getAssignedToId());	
			}
			else {
				pstmt.setNull(5, Types.BIGINT);
			}
			if(ticket.getFailedAssetId() != 0) {
				pstmt.setLong(6, ticket.getFailedAssetId());
			}
			else {
				pstmt.setNull(6, Types.BIGINT);
			}
			pstmt.setLong(7, ticket.getDueTime());
			
			FieldUtil.appendCustomFieldValues(fields, TicketContext.DEFAULT_TICKET_FIELDS.length-1, ticket.getCustomProps(), pstmt);
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add ticket");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long ticketId = rs.getLong(1);
				System.out.println("Added ticket with ticket id : "+ticketId);
				ticket.setTicketId(ticketId);
			}
		}
		catch(SQLException | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
		
		return false;
	}

}
