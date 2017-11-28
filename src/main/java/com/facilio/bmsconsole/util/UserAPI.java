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

//import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.RoleContext;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.UserType;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class UserAPI {
	
	public static Map<Long, String> getOrgUsers(long orgId, int type) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, String> orgUsers = new LinkedHashMap<>();
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ORG_USERID, EMAIL, NAME FROM ORG_Users, Users where ORG_Users.USERID = Users.USERID and ORG_Users.ORGID = ? and ? & ORG_Users.USER_TYPE = ? ORDER BY EMAIL");
			
			pstmt.setLong(1, orgId);
			pstmt.setInt(2, type);
			pstmt.setInt(3, type);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				orgUsers.put(rs.getLong("ORG_USERID"), rs.getString("NAME"));
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
			//pstmt = conn.prepareStatement("SELECT ORG_Users.*, Users.*, Locations.*, Role.ROLE_ID, Role.NAME AS ROLE_NAME, Role.DESCRIPTION AS ROLE_DESC, Role.PERMISSIONS AS ROLE_PERMISSIONS FROM ORG_Users join Users on ORG_Users.USERID=Users.USERID join Role on ORG_Users.ROLE_ID=Role.ROLE_ID left join Locations on Locations.ID=Users.LOCATION_ID where ORG_Users.ORGID=? ORDER BY EMAIL;");
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
			pstmt = conn.prepareStatement("SELECT ORG_Users.*, Users.*, Role.ROLE_ID, Role.NAME AS ROLE_NAME, Role.DESCRIPTION AS ROLE_DESC, Role.PERMISSIONS AS ROLE_PERMISSIONS FROM ORG_Users, Users, Role where ORG_Users.USERID = Users.USERID and ORG_Users.ROLE_ID = Role.ROLE_ID and Users.USERID=?");
			pstmt.setLong(1, userId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				UserContext tc = getUserObjectFromRS1(rs);
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
	
	public static UserContext getOrgSuperAdmin(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ORG_Users.ORG_USERID, ORG_Users.USER_TYPE, ORG_Users.ORGID, Users.USERID, Users.NAME, Users.EMAIL, ORG_Users.INVITEDTIME, ORG_Users.USER_STATUS, ORG_Users.INVITATION_ACCEPT_STATUS, ORG_Users.ROLE_ID, Users.PHOTO_ID FROM Users INNER JOIN ORG_Users ON ORG_Users.USERID = Users.USERID INNER JOIN Role ON ORG_Users.ROLE_ID = Role.ROLE_ID WHERE ORG_Users.ORGID = ? AND Role.NAME = ?");
			pstmt.setLong(1, orgId);
			pstmt.setString(2, FacilioConstants.Role.SUPER_ADMIN);
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
			pstmt = conn.prepareStatement("SELECT ORG_Users.ORG_USERID, ORG_Users.USER_TYPE, ORG_Users.ORGID, Users.USERID, Users.NAME, Users.EMAIL, ORG_Users.INVITEDTIME, ORG_Users.USER_STATUS, ORG_Users.INVITATION_ACCEPT_STATUS, ORG_Users.ROLE_ID, Users.PHOTO_ID FROM Users INNER JOIN ORG_Users ON ORG_Users.USERID = Users.USERID WHERE ORG_Users.ORG_USERID  = ?");
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
	
	public static UserContext getRequester(String email, long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM ORG_Users, Users where ORG_Users.USERID = Users.USERID and Users.EMAIL = ? AND ORG_Users.ORGID = ?");
			pstmt.setString(1, email);
			pstmt.setLong(2,  orgId);
//			pstmt.setInt(2, UserType.REQUESTER.getValue());
			
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
//			pstmt.setInt(2, UserType.USER.getValue());
			
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
	
	public static long addRequester(UserContext context) throws Exception {

		Connection conn =null;
		try {
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			long userId = -1;
			UserContext usr = getUser(context.getEmail());
			if (usr == null) {
				String insertquery1 = "insert into Users (NAME,COGNITO_ID,USER_VERIFIED,EMAIL) values (?, ?, ?, ?)";
				PreparedStatement ps1 = conn.prepareStatement(insertquery1, Statement.RETURN_GENERATED_KEYS);
				ps1.setString(1, context.getName());
				ps1.setString(2, null);
				ps1.setBoolean(3, true);
				ps1.setString(4, context.getEmail());
				ps1.executeUpdate();
				ResultSet rs1 = ps1.getGeneratedKeys();
				rs1.next();
				userId = rs1.getLong(1);
				ps1.close();
			}
			else {
				userId =usr.getUserId();
			}
			
			String insertquery2 = "insert into ORG_Users (USERID,ORGID,INVITEDTIME,ISDEFAULT,USER_STATUS,INVITATION_ACCEPT_STATUS,USER_TYPE) values (?,?,?,?,?,?,?)";
			PreparedStatement ps2 = conn.prepareStatement(insertquery2, Statement.RETURN_GENERATED_KEYS);
			ps2.setLong(1,userId);
			ps2.setLong(2, context.getOrgId());
			ps2.setLong(3, System.currentTimeMillis());
			ps2.setBoolean(4, false);
			ps2.setBoolean(5, true);
			ps2.setBoolean(6, true);
			ps2.setInt(7, UserType.REQUESTER.getValue());
			ps2.executeUpdate();
			ResultSet rs2 = ps2.getGeneratedKeys();
			rs2.next();
			long orgUserId = rs2.getLong(1);
			rs2.close();
			ps2.close();

			context.setUserId(userId);
			context.setOrgUserId(orgUserId);
			return userId;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, null);
		}
	}
	
	public static long addUser(UserContext context) throws Exception {
		boolean isLocalConn = false;
		Connection conn =null;
		try {
			String orgName = OrgApi.getOrgDomainFromId(context.getOrgId());
			
			boolean createUserStatus = CognitoUtil.createUser(context.getName(), context.getEmail(), context.getEmail(), true, context.getPassword(), context.getPhone(), orgName);
			
			if (createUserStatus) {
				if (conn == null) {
					conn = FacilioConnectionPool.INSTANCE.getConnection();
					isLocalConn = true;
				}
				
//				long locationId = -1;
//				if(context.getLocation() != null) {
////					context.getLocation().sett
//					locationId = LocationAPI.addLocation(context.getLocation());
//				}
//				
				String insertquery1 = "insert into Users (NAME, COGNITO_ID, USER_VERIFIED, EMAIL, TIMEZONE, LANGUAGE, PHONE, MOBILE, PHOTO_ID, STREET, CITY, STATE, ZIP, COUNTRY) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement ps1 = conn.prepareStatement(insertquery1, Statement.RETURN_GENERATED_KEYS);
				ps1.setString(1, context.getName());
				ps1.setString(2, null);
				ps1.setBoolean(3, true);
				ps1.setString(4, context.getEmail());
				ps1.setString(5, context.getTimezone());
				ps1.setString(6, context.getLanguage());
				ps1.setString(7, context.getPhone());
				ps1.setString(8, context.getMobile());
				ps1.setLong(9, context.getPhotoId());
				ps1.setString(10, context.getStreet());
				ps1.setString(11, context.getCity());
				ps1.setString(12, context.getState());
				ps1.setString(13, context.getZip());
				ps1.setString(14, context.getCountry());
				ps1.executeUpdate();
				ResultSet rs1 = ps1.getGeneratedKeys();
				rs1.next();
				long userId = rs1.getLong(1);
				ps1.close();
				
				String insertquery2 = "insert into ORG_Users (USERID,ORGID,INVITEDTIME,ISDEFAULT,USER_STATUS,INVITATION_ACCEPT_STATUS,ROLE_ID,USER_TYPE) values (?,?,?,?,?,?,?,?)";
				PreparedStatement ps2 = conn.prepareStatement(insertquery2, Statement.RETURN_GENERATED_KEYS);
				ps2.setLong(1,userId);
				ps2.setLong(2, context.getOrgId());
				ps2.setLong(3, System.currentTimeMillis());
				ps2.setBoolean(4, false);
				ps2.setBoolean(5, true);
				ps2.setBoolean(6, true);
				ps2.setLong(7, context.getRoleId());
				ps2.setInt(8, UserType.USER.getValue());
				ps2.executeUpdate();
				ResultSet rs2 = ps2.getGeneratedKeys();
				rs2.next();
				long orgUserId = rs2.getLong(1);
				rs2.close();
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
	
	public static boolean updateUser(UserContext context, long orgId) throws Exception {

			// updating user attributes in cognito 
			//CognitoUtil.updateUserAttributes(context.getEmail(), context.getPhone());
			
			Map<String, Object> userprops = FieldUtil.getAsProperties(context);
			try{
				List<FacilioField> fields= FieldFactory.getUserFields();
				GenericUpdateRecordBuilder builder= new GenericUpdateRecordBuilder().table("Users").fields(fields).andCustomWhere("USERID = ?", context.getUserId());
				builder.update(userprops);
				
				
				
				List<FacilioField> orgfields = FieldFactory.getOrgUserFields();
				GenericUpdateRecordBuilder orgBuilder = new GenericUpdateRecordBuilder().table("ORG_Users").fields(orgfields).andCustomWhere("ORGID = ? AND ORG_USERID = ?",orgId, context.getOrgUserId());
				orgBuilder.update(userprops);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			
//			String insertquery1 = "update Users set NAME=?  where USERID=?";
//			PreparedStatement ps1 = conn.prepareStatement(insertquery1);
//			ps1.setString(1, context.getName());
//			ps1.setLong(2, context.getUserId());
//			ps1.executeUpdate();
//			ps1.close();
			
//			String insertquery2 = "update ORG_Users set ROLE_ID=? where ORG_USERID=?";
//			PreparedStatement ps2 = conn.prepareStatement(insertquery2);
//			ps2.setLong(1, context.getRoleId());
//			ps2.setLong(2, context.getOrgUserId());
//			ps2.executeUpdate();
//			ps2.close();
//			
			return true;
	}
	
	public static int updateUserType(UserContext uc, UserType type) throws SQLException {
		
		if(uc == null || uc.getOrgUserId() == -1) {
			throw new IllegalArgumentException("Invalid UserContext during updation of user type");
		}
		
		if(type == null) {
			throw new IllegalArgumentException("Invalid UserType during updation of user type");
		}
		
		int newUserType = type.setUser(uc.getUserType());
		String sql = "UPDATE ORG_Users SET USER_TYPE = ? WHERE ORG_USERID = ?";
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, newUserType);
			pstmt.setLong(2, uc.getOrgUserId());
			
			return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static boolean updateUserPhoto(long userId, long photoId, Connection conn) throws Exception {

		boolean isLocalConn = false;
		try {
			if (conn == null) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				isLocalConn = true;
			}

			String insertquery1 = "update Users set PHOTO_ID=? where USERID=?";
			PreparedStatement ps1 = conn.prepareStatement(insertquery1);
			ps1.setLong(1, photoId);
			ps1.setLong(2, userId);
			ps1.executeUpdate();
			ps1.close();
			
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
		if(rs.getLong("ROLE_ID") != 0)
		{
			uc.setRoleId(rs.getLong("ROLE_ID"));
		}
		uc.setPhotoId(rs.getLong("PHOTO_ID"));
		uc.setUserType(rs.getInt("USER_TYPE"));
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
		uc.setPhotoId(rs.getLong("PHOTO_ID"));
		uc.setUserType(rs.getInt("USER_TYPE"));
		uc.setPhone(rs.getString("PHONE"));
		uc.setTimezone(rs.getString("TIMEZONE"));
		uc.setMobile(rs.getString("MOBILE"));
		uc.setLanguage(rs.getString("LANGUAGE"));
		uc.setStreet(rs.getString("STREET"));
		uc.setCity(rs.getString("CITY"));
		uc.setState(rs.getString("STATE"));
		uc.setZip(rs.getString("ZIP"));
		uc.setCountry(rs.getString("COUNTRY"));
		
		
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