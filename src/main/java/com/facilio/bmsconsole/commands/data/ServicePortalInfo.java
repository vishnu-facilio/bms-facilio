package com.facilio.bmsconsole.commands.data;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

//import redis.clients.jedis.Connection;

public class ServicePortalInfo {
	public ServicePortalInfo()
	{
		// default values
	//	samlinfo.put("loginurl", "1");
		//samlinfo.put("logouturl", "2");

		//samlinfo.put("changepasswordurl", 3);

	}
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
	
	private HashMap samlinfo = new HashMap();
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
		
		if(samlinfo.size()==3)
		{
			this.samlinfo= samlinfo;
			
		}
	}
	

	
	
	@Override
	public String toString() {
		return "ServicePortalInfo [signupAllowed=" + signupAllowed + ", gmailLoginAllowed=" + gmailLoginAllowed
				+ ", ticketAlloedForPublic=" + ticketAlloedForPublic + ", anyDomain=" + anyDomain + ", samlEnabled="
				+ samlEnabled + ", samlinfo=" + samlinfo + "]";
	}
	private  static ServicePortalInfo getServicePortalInfoFromRS(ResultSet rs) throws SQLException {
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
			pstmt = conn.prepareStatement("select * from PortalInfo where ORGID=? and PORTALTYPE=0");
			pstmt.setLong(1, AccountUtil.getCurrentOrg().getOrgId());
			
		rs = pstmt.executeQuery();
		ServicePortalInfo oc = null;
		if(rs.next()) {
			oc = ServicePortalInfo.getServicePortalInfoFromRS(rs);
			System.out.println("set service portal info ===========> "+oc);
		}
		else
		{
			oc = new ServicePortalInfo();
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
	public static Object updatePortalInfo (ServicePortalInfo data) throws Exception{
		
	
		PreparedStatement psmt = null;
		Connection conn=null;
		try {
			if (conn == null) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
			}
			
			String updatequery = "update PortalInfo set SIGNUP_ALLOWED=? , GMAILLOGIN_ALLOWED=? ,IS_PUBLIC_CREATE_ALLOWED=?, IS_ANYDOMAIN_ALLOWED=? where ORGID=?";
			psmt = conn.prepareStatement(updatequery);
			psmt.setBoolean(1, data.getSignupAllowed());
			psmt.setBoolean(2, data.getGmailLoginAllowed());
			psmt.setBoolean(3, data.getTicketAlloedForPublic());
			psmt.setBoolean(4, data.getAnyDomain());
			
			psmt.setLong(5, AccountUtil.getCurrentOrg().getOrgId());
			
			psmt.executeUpdate();
			psmt.close();
			
			if(data.getSamlInfo()!=null)
			{
			String samlupdatequery = "update PortalInfo set LOGIN_URL=? , LOGOUT_URL=? ,PASSWORD_URL=? where ORGID=?";
			psmt = conn.prepareStatement(samlupdatequery);
			psmt.setString(1, ((String[])data.getSamlInfo().get("loginurl"))[0]);
			psmt.setString(2, ((String[])data.getSamlInfo().get("logouturl"))[0]);

			psmt.setString(3, ((String[])data.getSamlInfo().get("changepasswordurl"))[0]);
            psmt.setLong(4, AccountUtil.getCurrentOrg().getOrgId());
			
			psmt.executeUpdate();
			psmt.close();
			psmt=null;
			}
			
			return true;
	
			}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (conn!=null) {
				DBUtil.closeAll(conn, null);
			}
			if (psmt != null) {
				DBUtil.closeAll(null, psmt);
			}
		}
	}
}
