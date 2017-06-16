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
import com.facilio.bmsconsole.customfields.CFType;
import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.customfields.FacilioCustomField;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class TicketApi {
	
	private static final String[] DEFAULT_TICKET_FIELDS = new String[] {"REQUESTOR", "SUBJECT", "DESCRIPTION", "STATUS", "AGENTID", "FAILED_ASSET_ID", "DUE_DATE"};
	
	public static long addTicket(TicketContext ticketContext) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			List<FacilioCustomField> customFields = CFUtil.getCustomFields("Tickets_Objects", "Tickets_Fields", "Tickets", ticketContext.getOrgId());
			String sql = CFUtil.constuctInsertStatement("Tickets_Objects", "Tickets_Data", "Tickets", DEFAULT_TICKET_FIELDS, customFields, ticketContext.getOrgId());
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, ticketContext.getRequester());
			pstmt.setString(2,  ticketContext.getSubject());
			pstmt.setString(3, ticketContext.getDescription());
			pstmt.setInt(4, ticketContext.getStatusCode());
			
			if(ticketContext.getAgentId() != null) {
				pstmt.setLong(5, ticketContext.getAgentId());	
			}
			else {
				pstmt.setNull(5, Types.BIGINT);
			}
			if(ticketContext.getFailedAssetId() != null) {
				pstmt.setLong(6, ticketContext.getFailedAssetId());
			}
			else {
				pstmt.setNull(6, Types.BIGINT);
			}
			pstmt.setLong(7, ticketContext.getDueTime());
			
			CFUtil.appendCustomFieldValues(customFields, DEFAULT_TICKET_FIELDS.length, ticketContext, pstmt);
			
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
			e.printStackTrace();
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
			List<FacilioCustomField> customFields = CFUtil.getCustomFields("Tickets_Objects", "Tickets_Fields", "Tickets", orgId);
			String sql = CFUtil.constructSelectStatmenet("Tickets_Objects", "Tickets_Data", "Tickets", new String[] {"TICKETID", "REQUESTOR", "SUBJECT", "DESCRIPTION", "STATUS", "AGENTID", "FAILED_ASSET_ID", "DUE_DATE"}, customFields, new String[] {"ORGID"}, orgId);
			
			System.out.println(sql);
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement(sql+" ORDER BY SUBJECT");
			pstmt.setLong(1, orgId);
			
			List<TicketContext> tickets = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				TicketContext tc = getTCObjectFromRS(rs, customFields);
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
	
	public static TicketContext getTicketDetails(long ticketId, long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			List<FacilioCustomField> customFields = CFUtil.getCustomFields("Tickets_Objects", "Tickets_Fields", "Tickets", orgId);
			String sql = CFUtil.constructSelectStatmenet("Tickets_Objects", "Tickets_Data", "Tickets", new String[] {"TICKETID", "REQUESTOR", "SUBJECT", "DESCRIPTION", "STATUS", "AGENTID", "FAILED_ASSET_ID", "DUE_DATE"}, customFields, new String[] {"TICKETID"}, orgId);
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, ticketId);
			
			rs = pstmt.executeQuery();
			TicketContext tc = null;
			while(rs.next()) {
				tc = getTCObjectFromRS(rs, customFields);
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
	
	private static TicketContext getTCObjectFromRS(ResultSet rs, List<FacilioCustomField> customFields) throws SQLException {
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
		
		if(customFields != null) {
			for(FacilioCustomField field : customFields) {
				tc.put(field.getFieldName(), rs.getString(field.getFieldName()));
			}
		}
		
		return tc;
	}
}
