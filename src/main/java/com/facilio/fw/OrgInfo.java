package com.facilio.fw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.facilio.bmsconsole.util.OrgApi;
import com.facilio.transaction.FacilioConnectionPool;

public class OrgInfo {
	//public static final String ORGINFO = "ORGINFO";
	private static ThreadLocal<OrgInfo> orglocal = new ThreadLocal<OrgInfo>();

private long orgid;
private String orgName;
private String orgDomain;

public OrgInfo(long orgid)
{
	this.orgid = orgid;
	try {
		this.orgDomain = OrgApi.getOrgDomainFromId(orgid);
		this.orgName = this.orgDomain;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public OrgInfo(long orgid, String orgName, String orgDomain)
{
	this.orgid = orgid;
	this.orgName = orgName;
	this.orgDomain = orgDomain;
}

public long getOrgid() {
	return orgid;
}

public void setOrgid(long orgid) {
	this.orgid = orgid;
}

public String getOrgName() {
	return orgName;
}

public void setOrgName(String orgName) {
	this.orgName = orgName;
}

public String getOrgDomain() {
	return orgDomain;
}

public void setOrgDomain(String orgDomain) {
	this.orgDomain = orgDomain;
}

public static OrgInfo getCurrentOrgInfo()
{
    return orglocal.get();
}
public static void setCurrentOrgInfo(OrgInfo orginfo)
{
     orglocal.set(orginfo);
}

public static void cleanup()
{
	orglocal.remove();
}
public static void validateOrgInfo(String domainname,String username) throws Exception
{
	//TODO Caching to be done
	String sqlquery = "select Organizations.* from Organizations, ORG_Users,Users where Organizations.ORGID =ORG_Users.ORGID and ORG_Users.USERID=Users.USERID and Organizations.FACILIODOMAINNAME =? and EMAIL=?";
	java.sql.Connection con = FacilioConnectionPool.getInstance().getConnection();
	PreparedStatement ps = con.prepareStatement(sqlquery);
	ResultSet rs = null;
	try {
		System.out.println(sqlquery);
		System.out.println(domainname);
		ps.setString(1, domainname);
		ps.setString(2, username);
		 rs = ps.executeQuery();
		if(rs.next())
		{
		OrgInfo.setCurrentOrgInfo(new OrgInfo(rs.getLong("ORGID"), rs.getString("ORGNAME"), rs.getString("FACILIODOMAINNAME")));
		UserInfo.setCurrentUser(new UserInfo(username));
	      System.out.println("orginfo");

		}
		else
		{
			throw new Exception("no org created");
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw e;
	}
	finally{
		if(rs!=null)rs.close();
		ps.close();
		con.close();
	}
	
	// no records
		// throw exception for unauthorized access

    return ;
}

public static String getDefaultOrgInfo(String username) throws Exception
{
	//TODO Caching to be done
	String sqlquery = "select Organizations.FACILIODOMAINNAME from Organizations, ORG_Users,Users where Organizations.ORGID =ORG_Users.ORGID and ORG_Users.USERID=Users.USERID  and EMAIL=?";
	java.sql.Connection con = FacilioConnectionPool.getInstance().getConnection();
	PreparedStatement ps = con.prepareStatement(sqlquery);
	ResultSet rs = null;
	try {
		System.out.println(sqlquery);
		//System.out.println(domainname);
		//ps.setString(1, domainname);
		ps.setString(1, username);
		 rs = ps.executeQuery();
		if(rs.next())
		{
		 return rs.getString(1);
		//UserInfo.setCurrentUser(new UserInfo(username));
	      

		}
		else
		{
			throw new Exception("no org created");
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw e;
	}
	finally{
		if(rs!=null)rs.close();
		ps.close();
		con.close();
	}
	
	// no records
		// throw exception for unauthorized access

    
}

}
