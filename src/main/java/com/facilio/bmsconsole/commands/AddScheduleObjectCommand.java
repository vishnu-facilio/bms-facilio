package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;

public class AddScheduleObjectCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ScheduleContext scheduleObj = (ScheduleContext) context.get(FacilioConstants.ContextNames.SCHEDULE_OBJECT);

		if(scheduleObj != null) {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
				pstmt = conn.prepareStatement("INSERT INTO ScheduleObjects (ORGID, SCHEDULED_START, ESTIMATED_END, ACTUAL_WORK_START, ACTUAL_WORK_END) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
				
				pstmt.setLong(1, scheduleObj.getOrgId());
				pstmt.setLong(2, scheduleObj.getScheduledStart());
				pstmt.setLong(3, scheduleObj.getEstimatedEnd());
				
				if(scheduleObj.getActualWorkStart() != 0) {
					pstmt.setLong(4, scheduleObj.getActualWorkStart());
				}
				else {
					pstmt.setNull(4, Types.BIGINT);
				}
				
				if(scheduleObj.getActualWorkEnd() != 0) {
					pstmt.setLong(5, scheduleObj.getActualWorkEnd());
				}
				else {
					pstmt.setNull(5, Types.BIGINT);
				}
				
				if(pstmt.executeUpdate() < 1) {
					throw new RuntimeException("Unable to add ScheduleObject");
				}
				else {
					rs = pstmt.getGeneratedKeys();
					rs.next();
					long scheduleId = rs.getLong(1);
					System.out.println("Added ScheduleObject with id : "+scheduleId);
					scheduleObj.setScheduleId(scheduleId);
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
		
		return false;
	}
}
