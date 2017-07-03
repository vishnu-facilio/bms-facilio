package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.WorkflowRuleContext;
import com.facilio.bmsconsole.context.WorkflowRuleContext.EventType;
import com.facilio.fw.UserInfo;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class WorkflowAPI {
	
	public static long addWorkflowRule(WorkflowRuleContext rule) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			String insertQuery = "insert into WorkflowRules set ORGID=?, NAME=?, DESCRIPTION=?, MODULE_ID=?, EVENT_TYPE=?, CONDITIONS=?, IS_ACTIVE=?, EXECUTION_ORDER=?, CREATED_BY=?, CREATED_TIME=?";
			
			pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, rule.getOrgId());
			pstmt.setString(2, rule.getName());
			pstmt.setString(3, rule.getDescription());
			pstmt.setString(4, rule.getModule());
			pstmt.setInt(5, rule.getEventType().getValue());
			if (rule.getConditions() != null) {
				pstmt.setString(6, rule.getConditions().toJSONString());
			}
			else {
				pstmt.setNull(6, Types.BIGINT);
			}
			pstmt.setBoolean(7, rule.isActive());
			pstmt.setInt(8, rule.getExecutionOrder());
			pstmt.setLong(9, UserInfo.getCurrentUser().getUserId());
			pstmt.setLong(10, System.currentTimeMillis());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add workflow rule");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long ruleId = rs.getLong(1);
				System.out.println("Added workflow rule with id : "+ruleId);
				return ruleId;
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
	
	public static List<WorkflowRuleContext> getWorkflowRules(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			String sql = "select * from WorkflowRules where ORGID=? ORDER BY EXECUTION_ORDER";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, orgId);
			
			List<WorkflowRuleContext> rules = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				WorkflowRuleContext rc = getRuleObjectFromRS(rs);
				rules.add(rc);
			}
			
			return rules;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(long orgId, String module) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			String sql = "select * from WorkflowRules where ORGID=? and MODULE_ID=? ORDER BY EXECUTION_ORDER";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, orgId);
			pstmt.setString(2, module);
			
			List<WorkflowRuleContext> rules = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				WorkflowRuleContext rc = getRuleObjectFromRS(rs);
				rules.add(rc);
			}
			
			return rules;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<WorkflowRuleContext> getWorkflowRules(long orgId, String module, int eventType) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			String sql = "select * from WorkflowRules where ORGID=? and MODULE_ID=? and ((EVENT_TYPE & ?) == ?) ORDER BY EXECUTION_ORDER";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, orgId);
			pstmt.setString(2, module);
			pstmt.setInt(3, eventType);
			pstmt.setInt(4, eventType);
			
			List<WorkflowRuleContext> rules = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				WorkflowRuleContext rc = getRuleObjectFromRS(rs);
				rules.add(rc);
			}
			
			return rules;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static WorkflowRuleContext getWorkflowRule(long orgId, long ruleId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			String sql = "select * from WorkflowRules where WORKFLOW_ID=? and ORGID=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, ruleId);
			pstmt.setLong(2, orgId);
			
			rs = pstmt.executeQuery();
			WorkflowRuleContext rc = null;
			while(rs.next()) {
				rc = getRuleObjectFromRS(rs);
				break;
			}
			
			return rc;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private static WorkflowRuleContext getRuleObjectFromRS(ResultSet rs) throws SQLException {
		WorkflowRuleContext rc = new WorkflowRuleContext();
		rc.setOrgId(rs.getLong("ORGID"));
		rc.setWorkflowId(rs.getLong("WORKFLOW_ID"));
		rc.setName(rs.getString("NAME"));
		rc.setDescription(rs.getString("DESCRIPTION"));
		rc.setModule(rs.getString("MODULE_ID"));
		rc.setEventType(EventType.valueOf(rs.getInt("EVENT_TYPE")));
		rc.setActive(rs.getBoolean("IS_ACTIVE"));
		rc.setExecutionOrder(rs.getInt("EXECUTION_ORDER"));
		
		return rc;
	}
}
