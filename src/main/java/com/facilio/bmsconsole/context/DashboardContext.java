package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class DashboardContext extends ModuleBaseWithCustomFields implements Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getLogger(DashboardContext.class.getName());

	private String dashboardName;
	
	private Long dashboardFolderId = -1l;
	
	public Object clone() throws CloneNotSupportedException { 
		return super.clone(); 
	}
	
	public Long getDashboardFolderId() {
		return dashboardFolderId;
	}
	public void setDashboardFolderId(Long dashboardFolderId) {
		this.dashboardFolderId = dashboardFolderId;
	}
	
	public List<SpaceFilteredDashboardSettings> spaceFilteredDashboardSettings;
	
	public List<SpaceFilteredDashboardSettings> getSpaceFilteredDashboardSettings() {
		return spaceFilteredDashboardSettings;
	}
	public void setSpaceFilteredDashboardSettings(List<SpaceFilteredDashboardSettings> spaceFilteredDashboardSettings) {
		this.spaceFilteredDashboardSettings = spaceFilteredDashboardSettings;
	}
	
	public void addSpaceFilteredDashboardSettings(SpaceFilteredDashboardSettings spaceFilteredDashboardSetting) {
		this.spaceFilteredDashboardSettings = spaceFilteredDashboardSettings != null ? spaceFilteredDashboardSettings : new ArrayList<>();
		spaceFilteredDashboardSettings.add(spaceFilteredDashboardSetting);
	}

	int dateOperator;
	String dateValue; 

	public int getDateOperator() {
		return dateOperator;
	}
	public void setDateOperator(int dateOperator) {
		this.dateOperator = dateOperator;
	}
	public String getDateValue() {
		return dateValue;
	}
	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}

	private ReportSpaceFilterContext reportSpaceFilterContext;
	
	public ReportSpaceFilterContext getReportSpaceFilterContext() {
		return reportSpaceFilterContext;
	}
	public void setReportSpaceFilterContext(ReportSpaceFilterContext reportSpaceFilterContext) {
		this.reportSpaceFilterContext = reportSpaceFilterContext;
	}
	public String getDashboardName() {
		return dashboardName;
	}
	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}
	
	public Long baseSpaceId;
	
	public Long getBaseSpaceId() {
		return baseSpaceId;
	}
	public void setBaseSpaceId(Long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}

	public List<Long> buildingExcludeList;
	public List<Long> getBuildingExcludeList() {
		return buildingExcludeList;
	}
	public void setBuildingExcludeList(List<Long> buildingExcludeList) {
		this.buildingExcludeList = buildingExcludeList;
	}
	
	List<DashboardSharingContext> dashboardSharingContext;

	public List<DashboardSharingContext> getDashboardSharingContext() {
		return dashboardSharingContext;
	}

	public void setDashboardSharingContext(List<DashboardSharingContext> dashboardSharingContext) {
		this.dashboardSharingContext = dashboardSharingContext;
	}
	
	public void addDashboardSharingContext(DashboardSharingContext dashboardSharingContext) {
		
		this.dashboardSharingContext = this.dashboardSharingContext == null ? new ArrayList<>() : this.dashboardSharingContext; 
		this.dashboardSharingContext.add(dashboardSharingContext);
	}

	private String linkName;
	
	private Integer displayOrder;
	
	public boolean mobileEnabled;
	
	public boolean getMobileEnabled() {
		return mobileEnabled;
	}
	public void setMobileEnabled(boolean mobileEnabled) {
		this.mobileEnabled = mobileEnabled;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	
	private Long createdByUserId;
	
	public Long getCreatedByUserId() {
		return createdByUserId;
	}
	public void setCreatedByUserId(Long createdByUser) {
		this.createdByUserId = createdByUser;
	}
	
	private Integer publishStatus;

	public Integer getPublishStatus() {
		return publishStatus;
	}
	public void setPublishStatus(Integer publishStatus) {
		this.publishStatus = publishStatus;
	}
	
	private String dashboardUrl;
	
	public String getDashboardUrl() {
		return dashboardUrl;
	}
	public void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}
	
	List<DashboardWidgetContext> dashboardWidgets;

	public List<DashboardWidgetContext> getDashboardWidgets() {
		return dashboardWidgets;
	}
	public void setDashboardWidgets(List<DashboardWidgetContext> dashboardWidgets) {
		this.dashboardWidgets = dashboardWidgets;
	}
	
	public void addDashboardWidget(DashboardWidgetContext dashboardWidgetContext) {
		if(this.dashboardWidgets == null) {
			this.dashboardWidgets = new ArrayList<>();
		}
		dashboardWidgets.add(dashboardWidgetContext);
	}
	
	private String moduleName;
	
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getModuleName() {
		return this.moduleName;
	}
	
	public static enum DashboardPublishStatus {
		NONE,
		SUBMITTED,
		REJECTED,
		PUBLISHED
	}
	public static enum DashboardType {
		SITE_PORTFOLIO(1,"Site Portfolio"),
		BUILDING_PORTFOLIO(2,"Building Portfolio"),
		BUILDING(3,"Building"),
		ZONE(4,"Zone");
		
		private int intVal;
		private String strVal;
		
		private DashboardType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		public static DashboardType getType(int val) {
			return typeMap.get(val);
		}
		
		private static final Map<Integer, DashboardType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, DashboardType> initTypeMap() {
			Map<Integer, DashboardType> typeMap = new HashMap<>();
			
			for(DashboardType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, DashboardType> getAllTypes() {
			return typeMap;
		}
	}
	//	@Override
	//	public int compareTo(DashboardContext o) {
	//		if(this.getId() < o.getId()) {
	//			return -1;
	//		}
	//		else if(this.getId() > o.getId()) {
	//			return 1;
	//		}
	//		return 0;
	//	}
}
