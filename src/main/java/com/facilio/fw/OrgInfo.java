package com.facilio.fw;

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
	String sqlquery = "select * from Organizations, ORG_Users,Users where Organizations.ORGID =ORG_Users.ORGID and ORG_Users.USERID=Users.USERID and Organizations.FACILIODOMAINNAME =? and EMAIL=?";
	// if query returns resultset
	OrgInfo.setCurrentOrgInfo(new OrgInfo(100));
	UserInfo.setCurrentUser(new UserInfo(username));
	
	// no records
		// throw exception for unauthorized access

    return ;
}
public static void doSignup(String subdomain)
{
	
}
}
