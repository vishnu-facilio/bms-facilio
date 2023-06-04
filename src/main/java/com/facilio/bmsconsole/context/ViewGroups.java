package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.view.FacilioView;
import lombok.Getter;
import lombok.Setter;

public class ViewGroups {

	public ViewGroups() {}

	public ViewGroups(String name, String displayName, String moduleName, long moduleId, long appId, int sequenceNumber, int groupType) {
		this.name = name;
		this.appId = appId;
		this.moduleId = moduleId;
		this.groupType = groupType;
		this.moduleName = moduleName;
		this.displayName = displayName;
		this.sequenceNumber = sequenceNumber;
	}
	
	private String name;
	
	private String displayName;
			
	private List<FacilioView> views;
	
	private String moduleName = "";
	
	private int sequenceNumber = -1;
	private Long sysCreatedBy;
	private Long sysModifiedBy;
	private Long sysCreatedTime;
	private Long viewCount;

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public Long getSysCreatedBy() {
		return sysCreatedBy;
	}

	public void setSysCreatedBy(Long sysCreatedBy) {
		this.sysCreatedBy = sysCreatedBy;
	}

	public Long getSysModifiedBy() {
		return sysModifiedBy;
	}

	public void setSysModifiedBy(Long sysModifiedBy) {
		this.sysModifiedBy = sysModifiedBy;
	}

	public Long getSysCreatedTime() {
		return sysCreatedTime;
	}

	public void setSysCreatedTime(Long sysCreatedTime) {
		this.sysCreatedTime = sysCreatedTime;
	}

	public Long getSysModifiedTime() {
		return sysModifiedTime;
	}

	public void setSysModifiedTime(Long sysModifiedTime) {
		this.sysModifiedTime = sysModifiedTime;
	}

	private Long sysModifiedTime;

	@Setter @Getter
	private String translatedDisplayName;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

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

	long id = -1;
	
	long moduleId = -1;
	
	long orgId = -1;
	
	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	long appId = -1;
	
	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public List<FacilioView> getViews() {
		return views;
	}

	public void setViews(List<FacilioView> views) {
		this.views = views;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	private Integer groupType;
	
	public Integer getGroupType() {
		return groupType;
	}

	public void setGroupType(Integer groupType) {
		this.groupType = groupType;
	}

	public static enum ViewGroupType{
		TABLE_GROUP(1),
		TIMELINEVIEW_GROUP(2);

		private int intVal;
		private ViewGroupType(int val) {
			this.intVal = val;
		}
		public int getIntVal() {
			return intVal;
		}

		public static ViewGroupType getGroupType(int val){
			return TYPE_MAP.get(val);
		}
		public static final Map<Integer, ViewGroupType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, ViewGroupType> initTypeMap() {
			Map<Integer, ViewGroupType> typeMap = new HashMap<>();
			for (ViewGroupType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
	}

}
