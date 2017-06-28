package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class ScheduleObjectAPI {
	public static long addScheduleObject(ScheduleContext scheduleObj) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
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
				return scheduleId;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static ScheduleContext getScheduleObjectDetails(long orgId, long scheduleId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM SCHEDULEOBJECTS WHERE ORGID = ? AND SCHEDULEID = ?");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, scheduleId);
			
			rs = pstmt.executeQuery();
			ScheduleContext sc = null;
			
			if(rs.next()) {
				sc = getScheduleContextFromRS(rs);
			}
			
			return sc;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private static ScheduleContext getScheduleContextFromRS(ResultSet rs) throws SQLException {
		ScheduleContext sc = new ScheduleContext();
		sc.setScheduleId(rs.getLong("SCHEDULEID"));
		sc.setOrgId(rs.getLong("ORGID"));
		sc.setScheduledStart(rs.getLong("SCHEDULED_START"));
		sc.setEstimatedEnd(rs.getLong("ESTIMATED_END"));
		sc.setActualWorkStart(rs.getLong("ACTUAL_WORK_START"));
		sc.setActualWorkEnd(rs.getLong("ACTUAL_WORK_END"));
		
		return sc;
	}
}
