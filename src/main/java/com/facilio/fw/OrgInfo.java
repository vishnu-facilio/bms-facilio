package com.facilio.fw;

import java.util.Map;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.OrgApi;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrgInfo {
	
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

	public OrgInfo(long orgid, String orgName, String orgDomain, UserContext superAdmin)
	{
		this.orgid = orgid;
		this.orgName = orgName;
		this.orgDomain = orgDomain;
		this.superAdmin = superAdmin;
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
	
	private UserContext superAdmin;
	public UserContext getSuperAdmin() {
		return superAdmin;
	}
	public void setSuperAdmin(UserContext superAdmin) {
		this.superAdmin = superAdmin;
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

	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> properties = mapper.convertValue(this, Map.class);
		if (properties != null) {
			return properties.toString();
		}
		else {
			return "null";
		}
	}
}