package com.facilio.incidents.tickets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.facilio.transaction.FacilioConnectionPool;

public class TicketStore {
	public static long addTicket(TicketContext ticketContext) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Tickets (ORGID, REQUESTOR, SUBJECT, DESCRIPTION, STATUS, AGENTID, FAILED_ASSET, DUE_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
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
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
