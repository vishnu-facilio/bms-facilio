package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.List;

public class ReportFolderContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Long parentFolderId;
	String name;
	List<ReportContext> reports;
	Long buildingId;
	public Long getParentFolderId() {
		return parentFolderId;
	}
	public List<ReportContext> getReports() {
		return reports;
	}
	public void setReports(List<ReportContext> reports) {
		this.reports = reports;
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
	public Long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(Long buildingId) {
		this.buildingId = buildingId;
	}
}
