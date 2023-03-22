package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class DashboardPublishContext extends ModuleBaseWithCustomFields {
	
	/*
	 * this file used for dashboard publish
	 * */
	
	private long orgId = -1;
	private long appId = -1;
	private long dashboardId = -1;
	
	private long orgUserId = -1;
	public long getOrgUserId() {
		return orgUserId;
	}
	public void setOrgUserId(long orgUserId) {
		this.orgUserId = orgUserId;
	}
	
	
	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public long getDashboardId() {
		return dashboardId;
	}

	public void setDashboardId(long dashboardId) {
		this.dashboardId = dashboardId;
	}
	
	private PublishingType publishingType;


	
	public int getPublishingType() {
		if(publishingType != null) {
			return publishingType.getIntVal();
		}
		else {
			return -1;
		}
	}
	public String getPublishingTypeVal() {
		if(publishingType != null) {
			return publishingType.getStringVal();
		}
		return null;
	}
	public void setPublishingType(int type) {
		this.publishingType = publishingType.typeMap.get(type);
	}
	public void setPublishingType(PublishingType type) {
		this.publishingType = type;
	}
	public PublishingType getPublishingTypeEnum() {
		return publishingType;
	}
	private long featureLicense;

    public long getFeatureLicense() {
        return featureLicense;
    }

    public void setFeatureLicense(long featureLicense) {
        this.featureLicense = featureLicense;
    }
	
	public static enum PublishingType {
		
		ALL_USER(1, "All_Users"),
		USER(2, "User"),
		PORTAL(3,"Portal");

		
		private int intVal;
		private String strVal;
		
		private PublishingType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, PublishingType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, PublishingType> initTypeMap() {
			Map<Integer, PublishingType> typeMap = new HashMap<>();
			
			for(PublishingType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, PublishingType> getAllTypes() {
			return typeMap;
		}
	}
	
	
}
