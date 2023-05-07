package com.facilio.accounts.dto;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsoleV3.context.budget.ChartOfAccountContext;

import java.io.Serializable;

public class OrgUserApp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long ouid;
	private long applicationId;
	private long roleId;
	private long scopingId;
	private Boolean isDefaultApp;
	private ApplicationContext application;
	private Boolean isDefaultMobileApp;

	private Role role;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public ApplicationContext getApplication() {
		return application;
	}

	public void setApplication(ApplicationContext application) {
		this.application = application;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOuid() {
		return ouid;
	}
	public void setOuid(long ouid) {
		this.ouid = ouid;
	}
	public long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(long applicationId) {
		this.applicationId = applicationId;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public long getScopingId() {
		return scopingId;
	}

	public void setScopingId(long scopingId) {
		this.scopingId = scopingId;
	}

	public Boolean getIsDefaultApp() {
		return isDefaultApp;
	}

	public void setIsDefaultApp(Boolean isDefaultApp) {
		this.isDefaultApp = isDefaultApp;
	}

	public Boolean getIsDefaultMobileApp() {
		return isDefaultMobileApp;
	}

	public void setIsDefaultMobileApp(Boolean isDefaultMobileApp) {
		this.isDefaultMobileApp = isDefaultMobileApp;
	}
}
