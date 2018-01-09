package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ReportFolderContext extends ModuleBaseWithCustomFields {

	Long parentFolderId;
	String name;
	List<ReportContext1> reports;
	public Long getParentFolderId() {
		return parentFolderId;
	}
	public List<ReportContext1> getReports() {
		return reports;
	}
	public void setReports(List<ReportContext1> reports) {
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
}
