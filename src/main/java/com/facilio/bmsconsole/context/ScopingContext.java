package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;

public class ScopingContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long orgId;
	private String scopeName;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public String getScopeName() {
		return scopeName;
	}
	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}
	
	private List<ScopingConfigContext> scopingConfigList;
	public List<ScopingConfigContext> getScopingConfigList() {
		return scopingConfigList;
	}
	public void setScopingConfigList(List<ScopingConfigContext> scopingConfigList) {
		this.scopingConfigList = scopingConfigList;
	}
	
}
