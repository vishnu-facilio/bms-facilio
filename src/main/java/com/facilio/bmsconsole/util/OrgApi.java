package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class OrgApi {
	
	public static long getOrgIdFromDomain(String domain) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ORGID FROM Organizations WHERE FACILIODOMAINNAME = ?");
			
			pstmt.setString(1, domain);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				return rs.getLong("ORGID");
			}
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
		return -1;
	}
	
	public static String getOrgDomainFromId(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT FACILIODOMAINNAME FROM Organizations WHERE ORGID = ?");
			
			pstmt.setLong(1, id);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				return rs.getString("FACILIODOMAINNAME");
			}
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
		return null;
	}
}
