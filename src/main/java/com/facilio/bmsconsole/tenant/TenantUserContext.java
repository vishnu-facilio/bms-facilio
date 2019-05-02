package com.facilio.bmsconsole.tenant;

import com.facilio.accounts.dto.User;

public class TenantUserContext  {

	private long tenantId = -1;
	private long orgid = -1;
	private long ouid = -1;
	private User orgUser;
	public long getTenantId() {
		return tenantId;
	}
	public void setTenantId(long tenantId) {
		this.tenantId = tenantId;
	}
	
	public long getOrgid() {
		return orgid;
	}
	public void setOrgid(long orgid) {
		this.orgid = orgid;
	}
	
	public User getOrgUser() {
		return orgUser;
	}
	public void setOrgUser(User orgUser) {
		this.orgUser = orgUser;
	}
	public long getOuid() {
		return ouid;
	}
	public void setOuid(long ouid) {
		this.ouid = ouid;
	}
}
