package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.RequesterContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class RequesterAPI {
	
	public static Map<Long, String> getAllRequesters(long orgId) throws Exception
	{
		Connection conn = FacilioConnectionPool.getInstance().getConnection();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.connection(conn)
				.table("Requester")
				.select(FieldFactory.getRequesterFields())
				.andCustomWhere("ORGID = ?", orgId);
		List<Map<String, Object>> requesters = builder.get();
		
		Map<Long, String> requesterMap = new LinkedHashMap<>();
		for(Map<String, Object> requester : requesters)
		{
			requesterMap.put((Long) requester.get("requesterId"), (String) requester.get("email"));
		}
		return requesterMap;
	}
	
	public static Map<String, Object> getRequester(long orgId, String email) throws Exception
	{
		Connection conn = FacilioConnectionPool.getInstance().getConnection();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.connection(conn)
				.table("Requester")
				.select(FieldFactory.getRequesterFields())
				.andCustomWhere("ORGID = ? AND EMAIL = ?", orgId, email);
		List<Map<String, Object>> requesters = builder.get();
		
		return !requesters.isEmpty()?requesters.get(0):null;
	}
	
	public static RequesterContext getRequesterFromId(long requesterId)throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Requester where Requester.REQUESTER_ID = ?");
			pstmt.setLong(1, requesterId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				RequesterContext rc = getRequesterObjectFromRS(rs);
				return rc;
			}
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return null;
	}
	
	private static RequesterContext getRequesterObjectFromRS(ResultSet rs) throws SQLException {
		
		RequesterContext rc = new RequesterContext();
		rc.setEmail(rs.getString("EMAIL"));
		rc.setName(rs.getString("NAME"));
		
		return rc;
	}
}