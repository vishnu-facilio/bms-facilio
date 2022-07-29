package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.bmsconsole.localization.fetchtranslationfields.TranslationTypeEnum;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class WebTabContext implements Serializable {
    public WebTabContext(String name, String route, WebTabContext.Type type, List<Long> moduleIds, String config, Integer featureLicense,List<String> specialTypeModules,long appId) {
        this.name = name;
        this.route = route;
        this.type = type;
        this.moduleIds = moduleIds;
        try {
            JSONParser parser = new JSONParser();
            this.config = (JSONObject) parser.parse(config);
        } catch (Exception e) {}
        if(featureLicense != null){
            this.featureLicense = featureLicense;
        }
        this.specialTypeModules = specialTypeModules;
        this.applicationId = appId;
    }
    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private int iconType = -1;
    public int getIconType() {
        return iconType;
    }
    public void setIconType(int iconType) {
        this.iconType = iconType;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private long applicationId;
    public long getApplicationId() {
        return applicationId;
    }
    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    private String route;
    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }

    private Type type;
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public void setType(int typeInt) {
        this.type = Type.valueOf(typeInt);
    }
    public Type getTypeEnum() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
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

    public void validateConfig() {
        if (config == null) {
            throw new IllegalArgumentException("Invalid config");
        }

        switch (type) {
            case MODULE:
                break;

            default:
                throw new IllegalArgumentException("Invalid type in validating");
        }
    }

    public enum TabType implements FacilioIntEnum{
        NORMAL("NormalTab"),
        SETUP("SetUptab");

        TabType(String name) {
            this.name=name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;
        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public static TabType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return getName();
        }

    }
    public enum Type implements FacilioIntEnum {
        MODULE ("Module", false, TabType.NORMAL),
        APPROVAL ("Approval", false, TabType.NORMAL),
        CALENDAR ("Calendar", false, TabType.NORMAL),
        REPORT ("Report", false, TabType.NORMAL),
        ANALYTICS ("Analytics", true, TabType.NORMAL),
        KPI ("Kpi", false,TabType.NORMAL),
        DASHBOARD ("Dashboard", false,TabType.NORMAL),
        CUSTOM ("Custom",false, TabType.NORMAL),
        APPS ("Apps",false,TabType.NORMAL),
        SETTINGS("Settings",true,TabType.NORMAL),
        TIMELINE("Timeline", false, TabType.NORMAL),
        PORTAL_OVERVIEW("Portal Overview", false, TabType.NORMAL),
        NOTIFICATION("Notification", false,TabType.NORMAL),
        INDOOR_FLOORPLAN("Indoor Floorplan", false, TabType.NORMAL);



        public void setName(String name) {
            this.name = name;
        }

        public TabType getTabType() {
            return tabType;
        }

        public void setTabType(TabType tabType) {
            this.tabType = tabType;
        }

        ;

        private String name;
        private boolean isGroupedType;

        private TabType tabType;

        public String getName() {
            return name;
        }

        Type(String name, boolean isGroupedType, TabType tabType) {
            this.name = name;
            this.isGroupedType = isGroupedType;
            this.tabType = tabType;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return getName();
        }

        public boolean isGroupedType() {
            return isGroupedType;
        }

        public void setGroupedType(boolean groupedType) {
            isGroupedType = groupedType;
        }
    }
    
    private List<NewPermission> permissions;
    public List<NewPermission> getPermissions() {
		return permissions;
	}
    public void setPermissions(List<NewPermission> permissions) {
		this.permissions = permissions;
	}
    
    private List<Long> moduleIds;
    public List<Long> getModuleIds() {
		return moduleIds;
	}
    public void setModuleIds(List<Long> moduleIds) {
		this.moduleIds = moduleIds;
	}

	private List<FacilioModule> modules;
    public List<FacilioModule> getModules() {
        return modules;
    }
    public void setModules(List<FacilioModule> modules) {
        this.modules = modules;
    }

    private long permissionVal;
    public long getPermissionVal() {
		return permissionVal;
	}
    public void setPermissionVal(long permissionVal) {
		this.permissionVal = permissionVal;
	}
    
    private List<Permission> permission;
    public List<Permission> getPermission() {
		return permission;
	}
    public void setPermission(List<Permission> permission) {
		this.permission = permission;
	}

//    private long featureLicense;
//
//    public long getFeatureLicense() {
//        return featureLicense;
//    }
//
//    public void setFeatureLicense(long featureLicense) {
//        this.featureLicense = featureLicense;
//    }
    
    private int featureLicense;
    
    public int getFeatureLicense() {
		return featureLicense;
	}
	public void setFeatureLicense(int featureLicense) {
		this.featureLicense = featureLicense;
	}
    

    public WebTabContext(String name, String route, Type type, List<Long> moduleIds, long appId, JSONObject config) {
        this.name = name;
        this.route = route;
        this.type = type;
        this.moduleIds = moduleIds;
        this.applicationId = appId;
        this.config = config;
    }

    public WebTabContext() {
    }

    public WebTabContext(String name, String route, Type type, List<Long> moduleIds, long appId, JSONObject config, int featureLicense) {
        this.name = name;
        this.route = route;
        this.type = type;
        this.moduleIds = moduleIds;
        this.applicationId = appId;
        this.config = config;
        this.featureLicense = featureLicense;
    }

    private List<String> specialTypeModules;

    public List<String> getSpecialTypeModules() {
        return specialTypeModules;
    }

    public void setSpecialTypeModules(List<String> specialTypeModules) {
        this.specialTypeModules = specialTypeModules;
    }

    private int order = -1;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Setter @Getter
    private List<TranslationTypeEnum.ClientColumnTypeEnum> typeVsColumns;
}
