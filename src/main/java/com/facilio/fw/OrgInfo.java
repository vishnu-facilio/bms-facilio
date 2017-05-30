package com.facilio.fw;

public class OrgInfo {
	public static final String ORGINFO = "ORGINFO";
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
}
