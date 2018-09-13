package com.facilio.bmsconsole.tenant;

import com.facilio.accounts.dto.User;

public class TenantUserContext extends User {

	Long tenantId;
	Long orgid;
	Long ouid;
	User orgUser;
	public Long getTenantId() {
		return tenantId;
	}
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
	
	public Long getOrgid() {
		return orgid;
	}
	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}
	
	public void setOuid(Long ouid) {
		this.ouid = ouid;
	}
	public User getOrgUser() {
		return orgUser;
	}
	public void setOrgUser(User orgUser) {
		this.orgUser = orgUser;
	}
}
