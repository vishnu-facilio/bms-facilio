package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class UserAPI {
	public static Map<Long, String> getOrgUsers(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, String> orgUsers = new LinkedHashMap<>();
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ORG_USERID, EMAIL FROM ORG_Users, Users where ORG_Users.USERID = Users.USERID and Org_Users.ORGID = ? ORDER BY EMAIL");
			
			pstmt.setLong(1, orgId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				orgUsers.put(rs.getLong("ORG_USERID"), rs.getString("EMAIL"));
			}
			
			return orgUsers;
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
}
