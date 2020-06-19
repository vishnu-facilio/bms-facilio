package com.facilio.report.context;

import java.util.List;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ReportFolderContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private List<Long> ids;
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	private SharingContext<SingleSharingContext> reportSharing = new SharingContext<SingleSharingContext>();
	
	
	public SharingContext<SingleSharingContext> getReportSharing() {
		return reportSharing;
	}
	public void setReportSharing(SharingContext<SingleSharingContext> reportSharing) {
		this.reportSharing = reportSharing;
	}


	private long parentFolderId = -1;
	public Long getParentFolderId() {
		return parentFolderId;
	}
	public void setParentFolderId(Long parentFolderId) {
		this.parentFolderId = parentFolderId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	List<ReportContext> reports;
	public List<ReportContext> getReports() {
		return reports;
	}
	public void setReports(List<ReportContext> reports) {
		this.reports = reports;
	}
	
	private long modifiedTime = -1;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
}
