package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

public class DashboardFolderContext {

	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String string) {
		this.moduleName = string;
	}
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	Long parentFolderId;
	String name;
	List<DashboardContext> dashboards;
	public Long getParentFolderId() {
		return parentFolderId;
	}
	public void setParentFolderId(Long parentFolderId) {
		this.parentFolderId = parentFolderId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<DashboardContext> getDashboards() {
		return dashboards;
	}
	public void setDashboards(List<DashboardContext> dashboards) {
		this.dashboards = dashboards;
	}
	
	public void addDashboards(DashboardContext dashboard) {
		this.dashboards = (this.dashboards == null) ? new ArrayList<>() : this.dashboards; 
		this.dashboards.add(dashboard);
	}
}
