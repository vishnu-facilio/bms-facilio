package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserResult;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.context.RoleContext;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.constants.FacilioConstants;
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
	
	public static List<UserContext> getUsersOfOrg(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM ORG_Users, Users, Role where ORG_Users.USERID = Users.USERID and ORG_Users.ORGID = ? and ORG_Users.RLE_ID = Role.ROLE_ID ORDER BY EMAIL");
			pstmt.setLong(1, orgId);
			
			List<UserContext> users = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				UserContext tc = getUserObjectFromRS(rs);
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
	
	public static long addUser(UserContext context) throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			String orgName = OrgApi.getOrgDomainFromId(context.getOrgId());
			
			if (context.getRole() != FacilioConstants.Role.TECHNICIAN) {
				
				BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
				
				AWSCognitoIdentityProvider idpProvider = AWSCognitoIdentityProviderClientBuilder.standard().withRegion("us-west-2").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
				
				AdminCreateUserRequest createUserReq = new AdminCreateUserRequest()
						.withUserPoolId(FacilioConstants.CognitoUserPool.getUserPoolId())
						.withUsername(context.getEmail())
						.withUserAttributes(new AttributeType().withName("email").withValue(context.getEmail()), new AttributeType().withName("email_verified").withValue("true"), new AttributeType().withName("name").withValue(context.getName()), new AttributeType().withName("custom:orgName").withValue(orgName))
						.withDesiredDeliveryMediums("EMAIL")
						.withTemporaryPassword(context.getPassword());

				System.out.println("Create user request: "+createUserReq);
				AdminCreateUserResult result = idpProvider.adminCreateUser(createUserReq);
				System.out.println("Create user response: "+result);
			}

			conn = FacilioConnectionPool.INSTANCE.getConnection();

			String insertquery = "insert into Users (USER_VERIFIED,EMAIL) values (?,?)"; 
			PreparedStatement ps = conn.prepareStatement(insertquery);
			ps.setBoolean(1, true);
			ps.setString(2, context.getEmail());
			ps.addBatch();
			insertquery = "insert into ORG_Users (USERID,ORGID,INVITEDTIME,ISDEFAULT,INVITATION_ACCEPT_STATUS,ROLE) values ((select USERID from Users where EMAIL='"+context.getEmail()+"'),(select ORGID from Organizations where FACILIODOMAINNAME='"+orgName+"'),UNIX_TIMESTAMP() ,true,true, "+context.getRole()+")";
			ps.addBatch(insertquery);
			ps.executeBatch();
			
			return getUser(context.getEmail()).getUserId();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public static boolean updateUser(UserContext context) throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			if (context.getRole() != FacilioConstants.Role.TECHNICIAN) {
				
				BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
				
				AWSCognitoIdentityProvider idpProvider = AWSCognitoIdentityProviderClientBuilder.standard().withRegion("us-west-2").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
				
				if (context.getInviteAcceptStatus()) {
					AdminEnableUserRequest enableUserReq = new AdminEnableUserRequest()
							.withUsername(context.getEmail())
							.withUserPoolId(FacilioConstants.CognitoUserPool.getUserPoolId());
					
					System.out.println("Enable user request: "+enableUserReq);
					AdminEnableUserResult enableUserResult = idpProvider.adminEnableUser(enableUserReq);
					System.out.println("Enable user result: "+enableUserResult);
				}
				else {
					AdminDisableUserRequest disableUserReq = new AdminDisableUserRequest()
							.withUsername(context.getEmail())
							.withUserPoolId(FacilioConstants.CognitoUserPool.getUserPoolId());
					
					System.out.println("Disable user request: "+disableUserReq);
					AdminDisableUserResult disableUserResult = idpProvider.adminDisableUser(disableUserReq);
					System.out.println("Disable user result: "+disableUserResult);
				}
			}

			conn = FacilioConnectionPool.INSTANCE.getConnection();

			String insertquery = "update ORG_Users set INVITATION_ACCEPT_STATUS=? where USERID=? and ORGID=?"; 
			
			PreparedStatement ps = conn.prepareStatement(insertquery);
			
			ps.setBoolean(1, context.getInviteAcceptStatus());
			ps.setLong(2, context.getUserId());
			ps.setLong(3, context.getOrgId());
			
			int cnt = ps.executeUpdate();
			if (cnt < 0) {
				return false;
			}
			else {
				return true;
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	private static UserContext getUserObjectFromRS(ResultSet rs) throws SQLException {
		
		UserContext uc = new UserContext();
		uc.setOrgUserId(rs.getLong("ORG_USERID"));
		uc.setOrgId(rs.getLong("ORGID"));
		uc.setUserId(rs.getLong("USERID"));
		uc.setEmail(rs.getString("EMAIL"));
		uc.setInvitedTime(rs.getLong("INVITEDTIME"));
		uc.setInviteAcceptStatus(rs.getBoolean("INVITATION_ACCEPT_STATUS"));
		uc.setRole(rs.getString("NAME"));
		
		return uc;
	}
	
	private static RoleContext getRoleObjectFromRS(ResultSet rs) throws SQLException {
		
		RoleContext rc = new RoleContext();
		rc.setOrgId(rs.getLong("ORGID"));
		rc.setRoleId(rs.getLong("ROLE_ID"));
		rc.setName(rs.getString("NAME"));
		rc.setDescription(rs.getString("DESCRIPTION"));
		
		return rc;
	}
}
