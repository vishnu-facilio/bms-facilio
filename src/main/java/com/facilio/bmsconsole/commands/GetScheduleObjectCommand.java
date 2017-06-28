package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class GetScheduleObjectCommand implements Command {

	public static final String SCHEDULE_ID = "scheduleId";
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long scheduleId = (long) context.get(SCHEDULE_ID);
		
		if(scheduleId > 0) {
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
				pstmt = conn.prepareStatement("SELECT * FROM SCHEDULEOBJECTS WHERE ORGID = ? AND SCHEDULEID = ?");
				pstmt.setLong(1, orgId);
				pstmt.setLong(2, scheduleId);
				
				rs = pstmt.executeQuery();
				ScheduleContext sc = null;
				
				if(rs.next()) {
					sc = CommonCommandUtil.getScheduleContextFromRS(rs);
				}
				
				context.put(FacilioConstants.ContextNames.SCHEDULE_OBJECT, sc);
			}
			catch(SQLException e) {
				e.printStackTrace();
				throw e;
			}
			finally {
				DBUtil.closeAll(pstmt, rs);
			}
		}
		
		return false;
	}
}
