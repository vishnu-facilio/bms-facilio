package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.TicketContext;
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
			pstmt.setString(2, ticketContext.getRequester());
			pstmt.setString(3,  ticketContext.getSubject());
			pstmt.setString(4, ticketContext.getDescription());
			pstmt.setInt(5, ticketContext.getStatusCode());
			
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
	
	public static List<TicketContext> getTicketsOfOrg(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Tickets WHERE ORGID = ? ORDER BY SUBJECT");
			pstmt.setLong(1, orgId);
			
			List<TicketContext> tickets = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				TicketContext tc = getTCObjectFromRS(rs);
				tickets.add(tc);
			}
			
			return tickets;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static TicketContext getTicketDetails(long ticketId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Tickets WHERE TICKETID = ?");
			pstmt.setLong(1, ticketId);
			
			rs = pstmt.executeQuery();
			TicketContext tc = null;
			while(rs.next()) {
				tc = getTCObjectFromRS(rs);
				break;
			}
			
			return tc;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private static TicketContext getTCObjectFromRS(ResultSet rs) throws SQLException {
		TicketContext tc = new TicketContext();
		tc.setTicketId(rs.getLong("TICKETID"));
		tc.setRequester(rs.getString("REQUESTOR"));
		tc.setSubject(rs.getString("SUBJECT"));
		tc.setDescription(rs.getString("DESCRIPTION"));
		tc.setStatusCode(rs.getInt("STATUS"));
		tc.setAgentId(rs.getLong("AGENTID"));
		tc.setFailedAssetId(rs.getLong("FAILED_ASSET_ID"));
		tc.setDueTime(rs.getLong("DUE_DATE"));
		tc.setOrgId(rs.getLong("ORGID"));
		
		return tc;
	}
}
