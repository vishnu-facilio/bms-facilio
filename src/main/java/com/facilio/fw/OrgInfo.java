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
	String sqlquery = "select Organizations.ORGID from Organizations, ORG_Users,Users where Organizations.ORGID =ORG_Users.ORGID and ORG_Users.USERID=Users.USERID and Organizations.FACILIODOMAINNAME =? and EMAIL=?";
	// if query returns resultset
	java.sql.Connection con = FacilioConnectionPool.getInstance().getConnection();
	PreparedStatement ps = con.prepareStatement(sqlquery);
	ps.setString(1, domainname);
	ps.setString(2, username);
	ResultSet rs = ps.executeQuery();
	while(rs.next())
	{
	OrgInfo.setCurrentOrgInfo(new OrgInfo(rs.getLong(1)));
	UserInfo.setCurrentUser(new UserInfo(username));
	}
	
	// no records
		// throw exception for unauthorized access

    return ;
}

}
