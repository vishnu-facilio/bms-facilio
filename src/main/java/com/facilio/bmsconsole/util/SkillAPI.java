package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class SkillAPI {
	
	public static Map<Long, String> getSkills(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, String> skills = new LinkedHashMap<>();
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Skills WHERE ORGID=? ORDER BY NAME");
			
			pstmt.setLong(1, orgId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				skills.put(rs.getLong("ID"), rs.getString("NAME"));
			}
			
			return skills;
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<SkillContext> getAllSkill(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Skills WHERE ORGID=? ORDER BY NAME");
			pstmt.setLong(1, orgId);
			
			List<SkillContext> skills = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				SkillContext lc = getSkillObjectFromRS(rs);
				skills.add(lc);
			}
			
			return skills;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static long addSkill(SkillContext skillContext) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Skills (ORGID, NAME, DESCRIPTION, IS_ACTIVE) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, skillContext.getOrgId());
			pstmt.setString(2, skillContext.getName());
			pstmt.setString(3, skillContext.getDescription());
			pstmt.setBoolean(4, skillContext.isActive());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add skill");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long skillId = rs.getLong(1);
				System.out.println("Added skill with id : "+skillId);
				return skillId;
			}
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static boolean updateSkill(SkillContext skillContext) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Skills SET NAME=?, DESCRIPTION=?, IS_ACTIVE=? WHERE ID=? AND ORGID=?");
			
			pstmt.setString(1, skillContext.getName());
			pstmt.setString(2, skillContext.getDescription());
			pstmt.setBoolean(3, skillContext.isActive());
			pstmt.setLong(4, skillContext.getId());
			pstmt.setLong(5, skillContext.getOrgId());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to update skill");
			}
			return true;
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private static SkillContext getSkillObjectFromRS(ResultSet rs) throws SQLException {
		
		SkillContext sc = new SkillContext();
		sc.setId(rs.getLong("ID"));
		sc.setOrgId(rs.getLong("ORGID"));
		sc.setName(rs.getString("NAME"));
		sc.setDescription(rs.getString("DESCRIPTION"));
		sc.setIsActive(rs.getBoolean("IS_ACTIVE"));
		
		return sc;
	}
}