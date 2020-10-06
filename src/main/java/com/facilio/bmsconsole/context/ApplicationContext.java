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

	public ApplicationContext(long orgId, String name, Boolean isDefault, int domainType, String linkName, int layoutType, String desc) {
		this.orgId = orgId;
		this.name = name;
		this.isDefault = isDefault;
		this.domainType = domainType;
		this.linkName = linkName;
		this.description = desc;
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
	
	private long scopingId;

	public long getScopingId() {
		return scopingId;
	}

	public void setScopingId(long scopingId) {
		this.scopingId = scopingId;
	}
	
	private int domainType;

	public int getDomainType() {
		return domainType;
	}

	public void setDomainType(int domainType) {
		this.domainType = domainType;
	}

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private List<ApplicationLayoutContext> layouts;

	public List<ApplicationLayoutContext> getLayouts() {
		return layouts;
	}

	public void setLayouts(List<ApplicationLayoutContext> layouts) {
		this.layouts = layouts;
	}
}
