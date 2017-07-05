package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.fields.FieldUtil;
import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class GetAllTicketsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			String sql = FieldUtil.constructSelectStatement(dataTableName, fields, null);
			
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			pstmt = conn.prepareStatement(sql);
			
			List<TicketContext> tickets = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				TicketContext tc = CommonCommandUtil.getTCObjectFromRS(rs, fields);
				tickets.add(tc);
			}
			
			context.put(FacilioConstants.ContextNames.TICKET_LIST, tickets);
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
		
		return false;
	}

}
