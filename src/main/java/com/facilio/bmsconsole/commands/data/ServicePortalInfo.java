package com.facilio.bmsconsole.commands.data;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

//import redis.clients.jedis.Connection;

public class ServicePortalInfo {
	boolean signupAllowed;
	boolean gmailLoginAllowed;
	boolean ticketAlloedForPublic;
	boolean anyDomain;

	public boolean getSignupAllowed() {
		return signupAllowed;
	}
	public void setSignupAllowed(boolean signupAllowed) {
		this.signupAllowed = signupAllowed;
	}
	public boolean getGmailLoginAllowed() {
		return gmailLoginAllowed;
	}
	public void setGmailLoginAllowed(boolean gmailLoginAllowed) {
		this.gmailLoginAllowed = gmailLoginAllowed;
	}
	public boolean getTicketAlloedForPublic() {
		return ticketAlloedForPublic;
	}
	public void setTicketAlloedForPublic(boolean ticketAlloedForPublic) {
		this.ticketAlloedForPublic = ticketAlloedForPublic;
	}
	public boolean getAnyDomain() {
		return anyDomain;
	}
	public void setAnyDomain(boolean anyDomain) {
		this.anyDomain = anyDomain;
	}

	public boolean getSamlEnabled() {
		return samlEnabled;
	}
	public void setSamlEnabled(boolean samlEnabled) {
		this.samlEnabled = samlEnabled;
	}
	boolean samlEnabled;
	
	private HashMap samlinfo = null;
	public HashMap getSamlInfo()
	{
		if(!getSamlEnabled())
		{
		return samlinfo;
		}
		return null;
	}
	public void setSamInfo(String key, String value)
	{
		samlinfo.put(key, value);
	}
	public void setSamInfo(String key, File value)
	{
		samlinfo.put(key, value);
	}
	
	public void setSamInfo(HashMap samlinfo)
	{
		// Keys
		/*login_url
		logout_url
		forgot_pass_url
		certificate path
		*/
		if(samlinfo.size()==3)
		{
			this.samlinfo= samlinfo;
			
		}
	}
	

	
	public void save() throws Exception
	{
		/*OrgApi.updatePortalInfo(this, null);
		System.out.println("--------------> service"+this);*/
	}
	@Override
	public String toString() {
		return "ServicePortalInfo [signupAllowed=" + signupAllowed + ", gmailLoginAllowed=" + gmailLoginAllowed
				+ ", ticketAlloedForPublic=" + ticketAlloedForPublic + ", anyDomain=" + anyDomain + ", samlEnabled="
				+ samlEnabled + ", samlinfo=" + samlinfo + "]";
	}
	public static ServicePortalInfo getServicePortalInfoFromRS(ResultSet rs) throws SQLException {
		ServicePortalInfo getorgContext = new ServicePortalInfo();
		getorgContext.setAnyDomain(rs.getBoolean("IS_ANYDOMAIN_ALLOWED"));
		getorgContext.setSignupAllowed(rs.getBoolean("SIGNUP_ALLOWED"));
		getorgContext.setGmailLoginAllowed(rs.getBoolean("GMAILLOGIN_ALLOWED"));
		getorgContext.setTicketAlloedForPublic(rs.getBoolean("IS_PUBLIC_CREATE_ALLOWED"));
	
		
		return getorgContext;
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
			oc = ServicePortalInfo.getServicePortalInfoFromRS(rs);
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
}
