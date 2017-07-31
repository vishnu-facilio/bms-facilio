package com.facilio.bmsconsole.util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

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
				
				String insertquery1 = "insert into Role (ORGID,NAME,PERMISSIONS) values (?,?,?)";
				PreparedStatement ps1 = conn.prepareStatement(insertquery1);
				ps1.setLong(1, orgId);
				ps1.setString(2, "Administrator");
				ps1.setString(3, "0");
				ps1.addBatch();
				ps1.setLong(1, orgId);
				ps1.setString(2, "Manager");
				ps1.setString(3, "0");
				ps1.addBatch();
				ps1.setLong(1, orgId);
				ps1.setString(2, "Dispatcher");
				ps1.setString(3, "0");
				ps1.addBatch();
				ps1.setLong(1, orgId);
				ps1.setString(2, "Technician");
				ps1.setString(3, "0");
				ps1.addBatch();
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
}
