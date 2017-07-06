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
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.fields.FieldUtil;
import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class GetTasksOfTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long ticketId = (long) context.get(FacilioConstants.ContextNames.TICKET_ID);
		if(ticketId > 0) {
		
			PreparedStatement pstmt = null;
	  		ResultSet rs = null;
	  		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
	  		try {
	  			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
				String sql = FieldUtil.constructSelectStatement(dataTableName, fields, new String[] {"parent"});
				
	  			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
	  			pstmt = conn.prepareStatement(sql);
	  			pstmt.setLong(1, ticketId);
	  			
	  			List<TaskContext> tasks = new ArrayList<>();
	  			
	  			rs = pstmt.executeQuery();
	  			while(rs.next()) {
	  				TaskContext tc = CommonCommandUtil.getTaskObjectFromRS(rs, fields);
	  				tasks.add(tc);
	  			}
	  			
	  			if(tasks != null && tasks.size() > 0) {
					context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
				}
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
			throw new IllegalArgumentException("Invalid ticket ID : "+ticketId);
		}
		
		return false;
	}

}
