package com.facilio.report.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ReportFolderContext extends ModuleBaseWithCustomFields {

	Long parentFolderId;
	String name;
	
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
}
