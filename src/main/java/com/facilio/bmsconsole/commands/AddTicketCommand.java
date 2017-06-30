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
import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.customfields.FacilioCustomField;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;

public class AddTicketCommand implements Command {

	private static final String[] DEFAULT_TICKET_FIELDS = new String[] {"REQUESTOR", "SUBJECT", "DESCRIPTION", "STATUS", "AGENTID", "FAILED_ASSET_ID", "DUE_DATE"};
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TicketContext ticket = (TicketContext) context.get(FacilioConstants.ContextNames.TICKET);
		String objectTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_OBJECTS_TABLE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			List<FacilioCustomField> customFields = (List<FacilioCustomField>) context.get(FacilioConstants.ContextNames.CUSTOM_FIELDS);
			String sql = CFUtil.constuctInsertStatement(objectTableName, dataTableName, DEFAULT_TICKET_FIELDS, customFields, ticket.getOrgId());
			
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
			
			CFUtil.appendCustomFieldValues(customFields, DEFAULT_TICKET_FIELDS.length, ticket.getCustomProps(), pstmt);
			
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
