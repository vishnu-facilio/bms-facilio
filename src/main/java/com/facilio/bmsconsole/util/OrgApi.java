package com.facilio.bmsconsole.util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.OrgContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.UserContext;
//import com.facilio.bmsconsole.context.servicePortalContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.facilio.sql.SQLScriptRunner;
import com.facilio.transaction.FacilioConnectionPool;

public class OrgApi {
	
	public static OrgInfo createOrganization(Connection conn, String orgName, String orgSubdomain, String userName, String email, String cognitoId, boolean populateDefaultModules) throws Exception {
		
		boolean localConn = false;
		try {
			if (conn == null) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				localConn = true;
			}
			
			long orgId = getOrgIdFromDomain(orgSubdomain);
			
			if (orgId == -1) {
				String insertquery = "insert into Organizations (ORGNAME,FACILIODOMAINNAME) values (?,?)";
				PreparedStatement ps = conn.prepareStatement(insertquery, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, orgName);
				ps.setString(2, orgSubdomain);
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				rs.next();
				orgId = rs.getLong(1);
				ps.close();
				
				Map<String, Long> defaultRoles = FacilioConstants.Role.DEFAULT_ROLES;
				Iterator<String> keys = defaultRoles.keySet().iterator();
				
				String insertquery1 = "insert into Role (ORGID,NAME,PERMISSIONS) values (?,?,?)";
				PreparedStatement ps1 = conn.prepareStatement(insertquery1);
				
				while (keys.hasNext()) {
					String key = keys.next();
					long permission = defaultRoles.get(key);
					
					ps1.setLong(1, orgId);
					ps1.setString(2, key);
					ps1.setLong(3, permission);
					ps1.addBatch();
				}
				ps1.executeBatch();
				ps1.close();
			}
			
			String insertquery2 = "insert into Users (NAME,COGNITO_ID,USER_VERIFIED,EMAIL) values (?, ?, ?, ?)";
			PreparedStatement ps2 = conn.prepareStatement(insertquery2, Statement.RETURN_GENERATED_KEYS);
			ps2.setString(1, userName);
			ps2.setString(2, cognitoId);
			ps2.setBoolean(3, true);
			ps2.setString(4, email);
			ps2.executeUpdate();
			ResultSet rs1 = ps2.getGeneratedKeys();
			rs1.next();
			long userId = rs1.getLong(1);
			ps2.close();
			
			String insertquery3 = "insert into ORG_Users (USERID,ORGID,INVITEDTIME,ISDEFAULT,INVITATION_ACCEPT_STATUS,ROLE_ID) values (?,?,?,true,true,(select ROLE_ID from Role where NAME='Administrator' limit 1))";
			PreparedStatement ps3 = conn.prepareStatement(insertquery3, Statement.RETURN_GENERATED_KEYS);
			ps3.setLong(1,userId);
			ps3.setLong(2, orgId);
			ps3.setLong(3, System.currentTimeMillis());
			ps3.executeUpdate();
			ps3.close();
			
			if (populateDefaultModules) {
				File insertModulesSQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/defaultModules.sql").getFile());
				
				Map<String, String> paramValues = new HashMap<>(); 
				//paramValues.put("orgId", "(SELECT ORGID from Organizations where FACILIODOMAINNAME='"+signupInfo.get("domainname")+"')");
				paramValues.put("orgId", String.valueOf(orgId));
				SQLScriptRunner scriptRunner = new SQLScriptRunner(insertModulesSQL, true, paramValues);
				scriptRunner.runScript(conn);
			}
			
			OrgInfo orgInfo = new OrgInfo(orgId, orgName, orgSubdomain);
			return orgInfo;
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			if (localConn) {
				DBUtil.closeAll(conn, null);
			}			
		}
	}
	
	public static long getOrgIdFromDomain(String domain) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ORGID FROM Organizations WHERE FACILIODOMAINNAME = ?");
			
			pstmt.setString(1, domain);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				return rs.getLong("ORGID");
			}
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
		return -1;
	}
	
	public static String getOrgDomainFromId(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT FACILIODOMAINNAME FROM Organizations WHERE ORGID = ?");
			
			pstmt.setLong(1, id);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				return rs.getString("FACILIODOMAINNAME");
			}
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
		return null;
	}
	
	public static OrgInfo getOrgInfo(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Organizations WHERE ORGID = ?");
			
			pstmt.setLong(1, id);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				String orgName = rs.getString("ORGNAME");
				String orgSubdomain = rs.getString("FACILIODOMAINNAME");
				
				return new OrgInfo(id, orgName, orgSubdomain);
			}
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
		return null;
	}
	
public static Object updateOrgsettings (OrgContext context, Connection conn) throws Exception{
	

	boolean isLocalConn = false;
	PreparedStatement psmt = null;
	try {
		if (conn == null) {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			isLocalConn = true;
		}

		String insertquery = "update Organizations set PHONE=? , MOBILE=? ,ORGNAME=?,FAX=?,STREET=?,CITY=?,STATE=?,ZIP=?,COUNTRY=?,TIMEZONE=?, LOGO_ID=? where ORGID=?";
		psmt = conn.prepareStatement(insertquery);
		psmt.setLong(1, context.getPhone());
		psmt.setLong(2, context.getMobile());
		psmt.setString(3, context.getName());
		psmt.setString(4, context.getFax());
		psmt.setString(5, context.getStreet());
		psmt.setString(6, context.getCity());
		psmt.setString(7, context.getState());
		psmt.setLong(8, context.getZipcode());
		psmt.setString(9, context.getCountry());
		psmt.setLong(10, context.getTimezone());
		psmt.setLong(11, context.getLogoId());
		psmt.setLong(12, OrgInfo.getCurrentOrgInfo().getOrgid());
		psmt.executeUpdate();
		System.out.println("org logo id "+context.getLogoId());
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

	public static OrgContext getOrgContext() throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("select * from organizations where ORGID=?");
			pstmt.setLong(1, OrgInfo.getCurrentOrgInfo().getOrgid());
			
		rs = pstmt.executeQuery();
		OrgContext oc = null;
		while(rs.next()) {
			oc = getOrgContextFromRS(rs);
			break;
		}
		
		return oc;
	}
	catch(SQLException e) {
		throw e;
	}
	finally {
		DBUtil.closeAll(conn, pstmt, rs);
	}
}
private static OrgContext getOrgContextFromRS(ResultSet rs) throws SQLException {
	OrgContext getorgContext = new OrgContext();
	getorgContext.setName(rs.getString("ORGNAME"));
	getorgContext.setCity(rs.getString("CITY"));
	getorgContext.setCountry(rs.getString("COUNTRY"));
	getorgContext.setFax(rs.getString("FAX"));
	getorgContext.setState(rs.getString("STATE"));
	getorgContext.setStreet(rs.getString("STREET"));
	getorgContext.setMobile(rs.getLong("MOBILE"));
	getorgContext.setPhone(rs.getLong("PHONE"));
	getorgContext.setTimezone(rs.getLong("TIMEZONE"));
	getorgContext.setZipcode(rs.getLong("ZIP"));
	getorgContext.setLogoId(rs.getLong("LOGO_ID"));
	
	return getorgContext;
}

public static Object updatePortalInfo (SetupLayout<ServicePortalInfo> set, Connection conn) throws Exception{
	

	boolean isLocalConn = false;
	PreparedStatement psmt = null;
	try {
		if (conn == null) {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			isLocalConn = true;
		}
		
		String insertquery = "update PortalInfo set SIGNUP_ALLOWED=? , GMAILLOGIN_ALLOWED=? ,IS_PUBLIC_CREATE_ALLOWED=?, IS_ANYDOMAIN_ALLOWED=? where ORGID=?";
		psmt = conn.prepareStatement(insertquery);
		psmt.setBoolean(1, set.getData().getSignupAllowed());
		psmt.setBoolean(2, set.getData().getGmailLoginAllowed());
		psmt.setBoolean(3, set.getData().getTicketAlloedForPublic());
		psmt.setBoolean(4, set.getData().getAnyDomain());
		
		psmt.setLong(5, OrgInfo.getCurrentOrgInfo().getOrgid());
		
//		System.out.println("signup **************** "+set.getData().getSignupAllowed());
//		System.out.println("gmail ****************"+set.getData().getGmailLoginAllowed());
//		System.out.println("public ****************"+set.getData().getTicketAlloedForPublic());
//		System.out.println("any ****************"+set.getData().getAnyDomain());
//		System.out.println("orgid ****************"+OrgInfo.getCurrentOrgInfo().getOrgid());
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

public static ServicePortalInfo getServicePortalInfo() throws Exception {
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	try {
		conn = FacilioConnectionPool.INSTANCE.getConnection();
		pstmt = conn.prepareStatement("select * from PortalInfo where ORGID=?");
		pstmt.setLong(1, OrgInfo.getCurrentOrgInfo().getOrgid());
		
	rs = pstmt.executeQuery();
	ServicePortalInfo oc = null;
	while(rs.next()) {
		oc = getServicePortalInfoFromRS(rs);
		System.out.println("set service portal info ===========> "+oc);
		break;
	}
	
	return oc;
}
catch(SQLException e) {
	throw e;
}
finally {
	DBUtil.closeAll(conn, pstmt, rs);
}
}


private static ServicePortalInfo getServicePortalInfoFromRS(ResultSet rs) throws SQLException {
	ServicePortalInfo getorgContext = new ServicePortalInfo();
	getorgContext.setAnyDomain(rs.getBoolean("IS_ANYDOMAIN_ALLOWED"));
	getorgContext.setSignupAllowed(rs.getBoolean("SIGNUP_ALLOWED"));
	getorgContext.setGmailLoginAllowed(rs.getBoolean("GMAILLOGIN_ALLOWED"));
	getorgContext.setTicketAlloedForPublic(rs.getBoolean("IS_PUBLIC_CREATE_ALLOWED"));

	
	return getorgContext;
}

}
