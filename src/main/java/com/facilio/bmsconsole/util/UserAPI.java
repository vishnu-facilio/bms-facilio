package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
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
			pstmt = conn.prepareStatement("SELECT ORG_USERID, EMAIL FROM ORG_Users, Users where ORG_Users.USERID = Users.USERID and Org_Users.ORGID = ? ORDER BY EMAIL");
			
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
	
	public static List<UserContext> getUsersOfOrg(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM ORG_Users, Users where ORG_Users.USERID = Users.USERID and Org_Users.ORGID = ? ORDER BY EMAIL");
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
	
	public static boolean addOrgUser(long orgId, String orgName, String emailId, String password, int role) throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			if (role != FacilioConstants.Role.REQUESTER) {
				AWSCognitoIdentityProvider idpProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();

				AdminCreateUserRequest createUserReq = new AdminCreateUserRequest()
						.withUserPoolId(FacilioConstants.CognitoUserPool.getUserPoolId())
						.withUsername(emailId)
						.withUserAttributes(new AttributeType().withName("email").withValue(emailId), new AttributeType().withName("email_verified").withValue("true"), new AttributeType().withName("custom:orgName").withValue(orgName))
						.withDesiredDeliveryMediums("EMAIL")
						.withTemporaryPassword(password);

				System.out.println("Create user request: "+createUserReq);
				AdminCreateUserResult result = idpProvider.adminCreateUser(createUserReq);
				System.out.println("Create user response: "+result);
			}

			conn = FacilioConnectionPool.INSTANCE.getConnection();

			String insertquery = "insert into Users (COGNITO_ID,USER_VERIFIED,EMAIL) values (?,?,?)"; 
			PreparedStatement ps = conn.prepareStatement(insertquery);
			ps.setString(1, "ID");
			ps.setBoolean(2, true);
			ps.setString(3, emailId);
			ps.addBatch();
			insertquery = "insert into ORG_Users (USERID,ORGID,INVITEDTIME,ISDEFAULT,INVITATION_ACCEPT_STATUS) values ((select USERID from Users where EMAIL='"+emailId+"'),(select ORGID from Organizations where FACILIODOMAINNAME='"+orgName+"'),UNIX_TIMESTAMP() ,true,true)";
			ps.addBatch(insertquery);
			ps.executeBatch();
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
		return true;
	}
	
	private static UserContext getUserObjectFromRS(ResultSet rs) throws SQLException {
		
		UserContext uc = new UserContext();
		uc.setOrgId(rs.getLong("ORGID"));
		uc.setUserId(rs.getLong("USERID"));
		uc.setEmail(rs.getString("EMAIL"));
		uc.setInvitedTime(rs.getLong("INVITEDTIME"));
		uc.setInviteAcceptStatus(rs.getBoolean("INVITATION_ACCEPT_STATUS"));
		uc.setRole(rs.getInt("ROLE"));
		
		return uc;
	}
}
