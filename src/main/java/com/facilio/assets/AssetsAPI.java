package com.facilio.assets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class AssetsAPI {
	public static Map<Long, String> getOrgAssets(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, String> orgAssets = new LinkedHashMap<>();
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ASSETID, NAME FROM Assets where ORGID = ? ORDER BY NAME");
			
			pstmt.setLong(1, orgId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				orgAssets.put(rs.getLong("ASSETID"), rs.getString("NAME"));
			}
			
			return orgAssets;
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
}
