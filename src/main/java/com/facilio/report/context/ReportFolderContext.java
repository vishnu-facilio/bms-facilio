package com.facilio.report.context;

import java.util.List;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReportFolderContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
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


	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private long moduleId = -1;
	
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
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
	
	public static enum FolderType {
		MODULE,
		READING,
		PIVOT
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static FolderType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
	
	private FolderType folderType;

	public int getFolderType() {
		if (folderType != null) {
			return folderType.getValue();
		}
		return -1;
	}

	public FolderType getFolderTypeEnum() {
		return folderType;
	}

	public void setFolderType(int type) {
		this.folderType = FolderType.valueOf(type);
	}

	public void setFolderType(FolderType folderType) {
		this.folderType = folderType;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public Long appId;
	private String linkName;
	private String appName;
	private String moduleName;
}
