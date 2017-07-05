package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.fields.FieldUtil;
import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;

public class GetTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long ticketId = (long) context.get(FacilioConstants.ContextNames.TICKET_ID);
		
		if( ticketId > 0) {
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
				String sql = FieldUtil.constructSelectStatement(dataTableName, fields, new String[] {"ticketId"});
				
				Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(1, ticketId);
				
				rs = pstmt.executeQuery();
				TicketContext ticket = null;
				while(rs.next()) {
					ticket = CommonCommandUtil.getTCObjectFromRS(rs, fields);
					break;
				}
				
				context.put(FacilioConstants.ContextNames.TICKET, ticket);
			}
			catch(SQLException e) {
				e.printStackTrace();
				throw e;
			}
			finally {
				DBUtil.closeAll(pstmt, rs);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Ticket ID : "+ticketId);
		}
		
		return false;
	}

}
