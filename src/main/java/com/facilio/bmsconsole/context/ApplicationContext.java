package com.facilio.bmsconsole.context;

import com.facilio.bacnet.BACNetUtil;
import com.facilio.modules.FacilioEnum;

import java.io.Serializable;
import java.util.List;

public class ApplicationContext implements Serializable{
	
	public ApplicationContext() {
		
	}
	
	/*public ApplicationContext(long orgId, String name, boolean isDefault, long appDomainId) {
		this.name = name;
		this.isDefault = isDefault;
		this.appDomainId = appDomainId;
		this.orgId = orgId;
	}*/

	public ApplicationContext(long orgId, String name, Boolean isDefault, long appDomainId, String linkName, int layoutType) {
		this.orgId = orgId;
		this.name = name;
		this.isDefault = isDefault;
		this.appDomainId = appDomainId;
		this.linkName = linkName;
		this.layoutType = layoutType;
	}

	private long orgId = -1;
	
	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private Boolean isDefault;
	
	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isDefault() {
		if (isDefault != null) {
			return isDefault.booleanValue();
		}
		return false;
	}

	private List<WebTabGroupContext> webTabGroups;
	public List<WebTabGroupContext> getWebTabGroups() {
		return webTabGroups;
	}
	public void setWebTabGroups(List<WebTabGroupContext> webTabGroups) {
		this.webTabGroups = webTabGroups;
	}

	private long appDomainId;
	public long getAppDomainId() {
		return appDomainId;
	}
	public void setAppDomainId(long appDomainId) {
		this.appDomainId = appDomainId;
	}

	private String linkName;

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	private int layoutType;

	public int getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(int layoutType) {
		this.layoutType = layoutType;
	}

	/*public int getType() {
            if (layoutType != null) {
                return layoutType.getIndex();
            }
            return -1;
        }
        public void setType(int typeInt) {
            this.layoutType = AppLayoutType.valueOf(typeInt);
        }
        public AppLayoutType getTypeEnum() {
            return layoutType;
        }
        public void setType(AppLayoutType type) {
            this.layoutType = type;
        }
    */
	public enum AppLayoutType implements FacilioEnum{
		SINGLE("Single"),
		DUAL("Dual");

		String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		AppLayoutType(String name) {
			this.name = name;
		}

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return getName();
		}
		public static AppLayoutType valueOf (int value) {
			if (value >= 0 && value < values().length) {
				return values() [value];
			}
			return null;
		}
	}
	
	private ScopingContext scoping;

	public ScopingContext getScoping() {
		return scoping;
	}

	public void setScoping(ScopingContext scoping) {
		this.scoping = scoping;
	}
	
	
}
