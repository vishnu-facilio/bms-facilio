package com.facilio.fw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.facilio.transaction.FacilioConnectionPool;

public class OrgInfo {
	//public static final String ORGINFO = "ORGINFO";
	private static ThreadLocal<OrgInfo> orglocal = new ThreadLocal<OrgInfo>();

private long orgid;
public OrgInfo(long orgid)
{
	this.orgid = orgid;
}

public long getOrgid() {
	return orgid;
}

public void setOrgid(long orgid) {
	this.orgid = orgid;
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
	String sqlquery = "select Organizations.ORGID from Organizations, ORG_Users,Users where Organizations.ORGID =ORG_Users.ORGID and ORG_Users.USERID=Users.USERID and Organizations.FACILIODOMAINNAME =? and EMAIL=?";
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
		OrgInfo.setCurrentOrgInfo(new OrgInfo(rs.getLong(1)));
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
