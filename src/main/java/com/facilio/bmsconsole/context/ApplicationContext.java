package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

	//isdefault is used to identify the default app in specific app category

	public ApplicationContext(long orgId, String name, Boolean isDefault, int domainType, String linkName, int layoutType, String desc, int appCategory) {
		this.orgId = orgId;
		this.name = name;
		this.isDefault = isDefault;
		this.domainType = domainType;
		this.linkName = linkName;
		this.description = desc;
		this.layoutType = layoutType;
		this.setAppCategory(appCategory);
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

	//domain specific isDefault
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
	private AppCategory appCategory;

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
	public enum AppLayoutType implements FacilioIntEnum {
		SINGLE("Single"),
		DUAL("Dual"),
		DUAL_BETA("Dual Beta");

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
		public Integer getIndex() {
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

	public static enum AppCategory implements FacilioIntEnum {
		PORTALS,
		WORK_CENTERS,
		FEATURE_GROUPING,
		TOOLS;

		private AppCategory() {
		}

		public Integer getIndex() {
			return this.ordinal() + 1;
		}

		public String getValue() {
			return this.name();
		}

		public static AppCategory valueOf(int value) {
			return value > 0 && value <= values().length ? values()[value - 1] : null;
		}
	}

	public int getAppCategory() {
		return this.appCategory != null ? this.appCategory.getIndex() : -1;
	}

	public void setAppCategory(int appCategory) {
		this.appCategory = AppCategory.valueOf(appCategory);
	}

	public AppCategory getAppCategoryEnum() {
		return this.appCategory;
	}

	public void setAppCategory(AppCategory appCategory) {
		this.appCategory = appCategory;
	}

	private AppDomain appDomain;

	public AppDomain getAppDomain() {
		return appDomain;
	}

	public void setAppDomain(AppDomain appDomain) {
		this.appDomain = appDomain;
	}

	private JSONObject config;
	public JSONObject getConfigJSON() {
		return config;
	}
	public void setConfig(JSONObject config) {
		this.config = config;
	}
	public String getConfig() {
		if (config != null) {
			return config.toJSONString();
		}
		return null;
	}
	public void setConfig(String configString) {
		try {
			JSONParser parser = new JSONParser();
			this.config = (JSONObject) parser.parse(configString);
		} catch (Exception e) {}
	}

	public void addToConfig(String key, Object value) {
		if (this.config == null) {
			this.config = new JSONObject();
		}
		this.config.put(key, value);
	}
	private long scopingId;

	public long getScopingId() {
		return scopingId;
	}

	public void setScopingId(long scopingId) {
		this.scopingId = scopingId;
	}

	@Getter @Setter
	private boolean hasSetupPermission;
}
