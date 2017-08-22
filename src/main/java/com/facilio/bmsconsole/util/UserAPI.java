package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.RoleContext;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.fw.auth.CognitoUtil;
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
			pstmt = conn.prepareStatement("SELECT ORG_USERID, EMAIL FROM ORG_Users, Users where ORG_Users.USERID = Users.USERID and ORG_Users.ORGID = ? ORDER BY EMAIL");
			
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
	
	public static RoleContext getRole(String name) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Role where Role.NAME = ?");
			pstmt.setString(1, name);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				RoleContext tc = getRoleObjectFromRS(rs);
				return tc;
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
	
	public static RoleContext getRole(long roleId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Role where Role.ROLE_ID = ?");
			pstmt.setLong(1, roleId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				RoleContext tc = getRoleObjectFromRS(rs);
				return tc;
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
	
	public static List<RoleContext> getRolesOfOrg(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Role where Role.ORGID = ?");
			pstmt.setLong(1, orgId);
			
			List<RoleContext> roles = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				RoleContext tc = getRoleObjectFromRS(rs);
				roles.add(tc);
			}
			
			return roles;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	/*public static List<LocationContext> getLocationsOfOrg(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Locations where Locations.ORGID = ?");
			pstmt.setLong(1, orgId);
			
			List<LocationContext> locations = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				LocationContext tc = getLocationObjectFromRS(rs);
				locations.add(tc);
			}
			
			return locations;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}*/
	
	public static Map<Long, String> getRolesOfOrgMap(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Role where Role.ORGID = ?");
			pstmt.setLong(1, orgId);
			
			Map<Long, String> roles = new HashMap<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				long roleId = rs.getLong("ROLE_ID");
				String roleName = rs.getString("NAME");
				roles.put(roleId, roleName);
			}
			return roles;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<UserContext> getUsersOfOrg(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ORG_Users.*, Users.*, Role.ROLE_ID, Role.NAME AS ROLE_NAME, Role.DESCRIPTION AS ROLE_DESC, Role.PERMISSIONS AS ROLE_PERMISSIONS FROM ORG_Users, Users, Role where ORG_Users.USERID = Users.USERID and ORG_Users.ORGID = ? and ORG_Users.ROLE_ID = Role.ROLE_ID ORDER BY EMAIL");
			pstmt.setLong(1, orgId);
			
			List<UserContext> users = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				UserContext tc = getUserObjectFromRS1(rs);
				users.add(tc);
			}
			
			return users;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static UserContext getUser(long userId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM ORG_Users, Users where ORG_Users.USERID = Users.USERID and Users.USERID = ?");
			pstmt.setLong(1, userId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				UserContext tc = getUserObjectFromRS(rs);
				return tc;
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
	
	public static UserContext getUserFromOrgUserId(long orgUserId)throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM ORG_Users, Users where ORG_Users.USERID = Users.USERID and ORG_Users.ORG_USERID  = ?");
			pstmt.setLong(1, orgUserId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				UserContext tc = getUserObjectFromRS(rs);
				return tc;
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
	
	public static UserContext getUser(String email) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM ORG_Users, Users, Role where ORG_Users.USERID = Users.USERID and Users.EMAIL = ? and ORG_Users.ROLE_ID = Role.ROLE_ID");
			pstmt.setString(1, email);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				UserContext tc = getUserObjectFromRS(rs);
				return tc;
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
	
	public static long addUser(UserContext context, Connection conn) throws Exception {

		boolean isLocalConn = false;
		try {
			String orgName = OrgApi.getOrgDomainFromId(context.getOrgId());
			
			boolean createUserStatus = CognitoUtil.createUser(context.getName(), context.getEmail(), context.getEmail(), true, context.getPassword(), context.getPhone(), orgName);
			
			if (createUserStatus) {
				if (conn == null) {
					conn = FacilioConnectionPool.INSTANCE.getConnection();
					isLocalConn = true;
				}
				
				String insertquery1 = "insert into Users (NAME,COGNITO_ID,USER_VERIFIED,EMAIL) values (?, ?, ?, ?)";
				PreparedStatement ps1 = conn.prepareStatement(insertquery1, Statement.RETURN_GENERATED_KEYS);
				ps1.setString(1, context.getName());
				ps1.setString(2, null);
				ps1.setBoolean(3, true);
				ps1.setString(4, context.getEmail());
				ps1.executeUpdate();
				ResultSet rs1 = ps1.getGeneratedKeys();
				rs1.next();
				long userId = rs1.getLong(1);
				ps1.close();
				
				String insertquery2 = "insert into ORG_Users (USERID,ORGID,INVITEDTIME,ISDEFAULT,USER_STATUS,INVITATION_ACCEPT_STATUS,ROLE_ID) values (?,?,?,?,?,?,?)";
				PreparedStatement ps2 = conn.prepareStatement(insertquery2, Statement.RETURN_GENERATED_KEYS);
				ps2.setLong(1,userId);
				ps2.setLong(2, context.getOrgId());
				ps2.setLong(3, System.currentTimeMillis());
				ps2.setBoolean(4, false);
				ps2.setBoolean(5, true);
				ps2.setBoolean(6, true);
				ps2.setLong(7, context.getRoleId());
				ps2.executeUpdate();
				ResultSet rs2 = ps2.getGeneratedKeys();
				rs2.next();
				long orgUserId = rs2.getLong(1);
				ps2.close();

				context.setUserId(userId);
				context.setOrgUserId(orgUserId);
				return userId;
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (isLocalConn) {
				DBUtil.closeAll(conn, null);
			}
		}
		return -1;
	}

	public static boolean changeUserStatus(UserContext context, Connection conn) throws Exception {

		boolean isLocalConn = false;
		PreparedStatement psmt = null;
		try {
			if (conn == null) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				isLocalConn = true;
			}

			String insertquery = "update ORG_Users set USER_STATUS=? where ORG_USERID=?";
			psmt = conn.prepareStatement(insertquery);
			psmt.setBoolean(1, context.getUserStatus());
			psmt.setLong(2, context.getOrgUserId());
			psmt.executeUpdate();

			return true;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (isLocalConn) {
				DBUtil.closeAll(conn, null);
			}
			if (psmt != null) {
				DBUtil.closeAll(null, psmt);
			}
		}
	}
	
	public static boolean updateUser(UserContext context, Connection conn) throws Exception {

		boolean isLocalConn = false;
		try {
			if (conn == null) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				isLocalConn = true;
			}
			
			// updating user attributes in cognito 
			CognitoUtil.updateUserAttributes(context.getEmail(), context.getPhone());

			String insertquery1 = "update Users set NAME=? where USERID=?";
			PreparedStatement ps1 = conn.prepareStatement(insertquery1);
			ps1.setString(1, context.getName());
			ps1.setLong(2, context.getUserId());
			ps1.executeUpdate();
			ps1.close();
			
			String insertquery2 = "update ORG_Users set ROLE_ID=? where ORG_USERID=?";
			PreparedStatement ps2 = conn.prepareStatement(insertquery2);
			ps2.setLong(1, context.getRoleId());
			ps2.setLong(2, context.getOrgUserId());
			ps2.executeUpdate();
			ps2.close();
			
			return true;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (isLocalConn) {
				DBUtil.closeAll(conn, null);
			}
		}
	}
	
	private static UserContext getUserObjectFromRS(ResultSet rs) throws SQLException {
		
		UserContext uc = new UserContext();
		uc.setOrgUserId(rs.getLong("ORG_USERID"));
		uc.setOrgId(rs.getLong("ORGID"));
		uc.setUserId(rs.getLong("USERID"));
		uc.setName(rs.getString("NAME"));
		uc.setEmail(rs.getString("EMAIL"));
		uc.setInvitedTime(rs.getLong("INVITEDTIME"));
		uc.setUserStatus(rs.getBoolean("USER_STATUS"));
		uc.setInviteAcceptStatus(rs.getBoolean("INVITATION_ACCEPT_STATUS"));
		uc.setRoleId(rs.getLong("ROLE_ID"));
		
		return uc;
	}
	
	private static UserContext getUserObjectFromRS1(ResultSet rs) throws SQLException {
		
		UserContext uc = new UserContext();
		uc.setOrgUserId(rs.getLong("ORG_USERID"));
		uc.setOrgId(rs.getLong("ORGID"));
		uc.setUserId(rs.getLong("USERID"));
		uc.setName(rs.getString("NAME"));
		uc.setEmail(rs.getString("EMAIL"));
		uc.setInvitedTime(rs.getLong("INVITEDTIME"));
		uc.setUserStatus(rs.getBoolean("USER_STATUS"));
		uc.setInviteAcceptStatus(rs.getBoolean("INVITATION_ACCEPT_STATUS"));
		uc.setRoleId(rs.getLong("ROLE_ID"));
		
		RoleContext rc = new RoleContext();
		rc.setRoleId(rs.getLong("ROLE_ID"));
		rc.setName(rs.getString("ROLE_NAME"));
		rc.setDescription(rs.getString("ROLE_DESC"));
		rc.setPermissions(rs.getLong("ROLE_PERMISSIONS"));
		uc.setRole(rc);
		
		return uc;
	}
	
	private static RoleContext getRoleObjectFromRS(ResultSet rs) throws SQLException {
		
		RoleContext rc = new RoleContext();
		rc.setOrgId(rs.getLong("ORGID"));
		rc.setRoleId(rs.getLong("ROLE_ID"));
		rc.setName(rs.getString("NAME"));
		rc.setDescription(rs.getString("DESCRIPTION"));
		rc.setPermissions(rs.getLong("PERMISSIONS"));
		
		return rc;
	}
	

}