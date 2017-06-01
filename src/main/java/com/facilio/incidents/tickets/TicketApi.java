package com.facilio.incidents.tickets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class TicketApi {
	public static long addTicket(TicketContext ticketContext) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Tickets (ORGID, REQUESTOR, SUBJECT, DESCRIPTION, STATUS, AGENTID, FAILED_ASSET_ID, DUE_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, ticketContext.getOrgId());
			pstmt.setString(2, ticketContext.getRequestor());
			pstmt.setString(3,  ticketContext.getSubject());
			pstmt.setString(4, ticketContext.getDescription());
			pstmt.setInt(5, ticketContext.getStatus());
			
			if(ticketContext.getAgentId() != null) {
				pstmt.setLong(6, ticketContext.getAgentId());
			}
			else {
				pstmt.setNull(6, Types.BIGINT);
			}
			
			if(ticketContext.getFailedAssetId() != null) {
				pstmt.setLong(7, ticketContext.getFailedAssetId());
			}
			else {
				pstmt.setNull(7, Types.BIGINT);
			}
			
			pstmt.setLong(8, ticketContext.getDueTime());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add ticket");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long ticketId = rs.getLong(1);
				System.out.println("Added ticket with ticket id : "+ticketId);
				return ticketId;
			}
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static Map<Long, String> getTicketIdAndSubject(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT TICKETID, SUBJECT FROM Tickets WHERE ORGID = ? ORDER BY SUBJECT");
			pstmt.setLong(1, orgId);
			
			Map<Long, String> ticketsIdAndSubject = new LinkedHashMap<>();
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ticketsIdAndSubject.put(rs.getLong("TICKETID"), rs.getString("SUBJECT"));
			}
			
			return ticketsIdAndSubject;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static Map<String, String> getTicketDetails(long ticketId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Tickets WHERE TICKETID = ?");
			pstmt.setLong(1, ticketId);
			
			Map<String, String> ticketProps = new HashMap<>();
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ticketProps.put("TICKETID", rs.getString("TICKETID"));
				ticketProps.put("REQUESTOR", rs.getString("REQUESTOR"));
				ticketProps.put("SUBJECT", rs.getString("SUBJECT"));
				ticketProps.put("STATUS", rs.getString("STATUS"));
				ticketProps.put("AGENTID", rs.getString("AGENTID"));
				ticketProps.put("FAILED_ASSET_ID", rs.getString("FAILED_ASSET_ID"));
				ticketProps.put("DUE_DATE", rs.getString("DUE_DATE"));
			}
			
			return ticketProps;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
}
