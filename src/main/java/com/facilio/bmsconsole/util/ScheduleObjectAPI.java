package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class ScheduleObjectAPI {
	public static long addScheduleObject(ScheduleContext scheduleObj, Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement("INSERT INTO ScheduleObjects (ORGID, SCHEDULED_START, ESTIMATED_END, ACTUAL_WORK_START, ACTUAL_WORK_END) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, OrgInfo.getCurrentOrgInfo().getOrgid());
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
				return scheduleId;
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
	
	public static ScheduleContext getScheduleObject(long scheduleId, Connection conn) throws SQLException {
		if(scheduleId > 0) {
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = conn.prepareStatement("SELECT * FROM SCHEDULEOBJECTS WHERE ORGID = ? AND SCHEDULEID = ?");
				pstmt.setLong(1, orgId);
				pstmt.setLong(2, scheduleId);
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					ScheduleContext sc = CommonCommandUtil.getScheduleContextFromRS(rs);
					return sc;
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
		return null;
	}
}
