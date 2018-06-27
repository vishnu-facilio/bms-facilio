package com.facilio.bmsconsole.context;

public class ModuleLocalIdContext {
	
	long orgId;
	long lastLocalId;
	String moduleName;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getLastLocalId() {
		return lastLocalId;
	}
	public void setLastLocalId(long lastLocalId) {
		this.lastLocalId = lastLocalId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

}
