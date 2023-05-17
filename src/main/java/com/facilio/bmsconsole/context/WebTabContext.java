package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.annotations.AnnotationEnums;
import com.facilio.annotations.ImmutableChildClass;
import com.facilio.bmsconsole.context.webtab.*;
import com.facilio.bmsconsole.context.webtab.ModuleTypeHandler;
import com.facilio.bmsconsole.context.webtab.ReadingKpiTypeHandler;
import com.facilio.bmsconsole.context.webtab.SetupTypeHandler;
import com.facilio.bmsconsole.context.webtab.WebTabHandler;
import com.facilio.bmsconsole.localization.fetchtranslationfields.TranslationTypeEnum;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.Serializable;
import java.util.List;

@ImmutableChildClass(className = "WebTabCacheContext",constructorPolicy = AnnotationEnums.ConstructorPolicy.REQUIRE_COPY_CONSTRUCTOR)
public class WebTabContext implements Serializable {
    public WebTabContext(WebTabContext object) {
        if(object != null) {
            this.id = object.id;
            this.iconType = object.iconType;
            this.iconTypeEnum = object.iconTypeEnum;
            this.name = object.name;
            this.applicationId = object.applicationId;
            this.route = object.route;
            this.type = object.type;
            this.config = object.config;
            this.permissions = object.permissions;
            this.moduleIds = object.moduleIds;
            this.modules = object.modules;
            this.permissionVal = object.permissionVal;
            this.permissionVal2 = object.permissionVal2;
            this.permission = object.permission;
            this.featureLicense = object.featureLicense;
            this.specialTypeModules = object.specialTypeModules;
            this.order = object.order;
            this.typeVsColumns = object.typeVsColumns;
        }
    }

    public WebTabContext(String name, String route, WebTabContext.Type type, List<Long> moduleIds, String config, Integer featureLicense, List<String> specialTypeModules, long appId) {
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

    public IconType getIconTypeEnum() {
        return iconTypeEnum;
    }

    public void setIconTypeEnum(IconType iconTypeEnum) {
        this.iconTypeEnum = iconTypeEnum;
    }
    @JSON(deserialize = false)
    public void setIconTypeEnum(String iconTypeEnum) {
        if(StringUtils.isNotEmpty(iconTypeEnum)){
            this.iconTypeEnum = IconType.valueOf(iconTypeEnum);
        }

    }
    public void setIconType(int iconType) {
        this.iconType = iconType;
    }

    private IconType iconTypeEnum;


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
        MODULE ("Module", false, TabType.NORMAL,new ModuleTypeHandler()), // 1
        APPROVAL ("Approval", false, TabType.NORMAL,null), // 2
        CALENDAR ("Calendar", false, TabType.NORMAL,null), // 3
        REPORT ("Report", false, TabType.NORMAL,null), // 4
        ANALYTICS ("Analytics", true, TabType.NORMAL,null), // 5
        KPI ("Kpi", false,TabType.NORMAL,null), // 6
        DASHBOARD ("Dashboard", false,TabType.NORMAL,new DashboardHandler()), // 7
        CUSTOM ("Custom",false, TabType.NORMAL,new CustomTypehandler()), // 8
        APPS ("Apps",false,TabType.NORMAL,null), // 9
        SETTINGS("Settings",true,TabType.NORMAL,null), // 10
        TIMELINE("Timeline", false, TabType.NORMAL,null), // 11
        PORTAL_OVERVIEW("Portal Overview", false, TabType.NORMAL,null), // 12
        NOTIFICATION("Notification", false,TabType.NORMAL,null), // 13
        INDOOR_FLOORPLAN("Indoor Floorplan", false, TabType.NORMAL,null), // 14
        HOMEPAGE("Home Page", false,TabType.NORMAL,null), // 15
        SERVICE_CATALOG("Service Catalog", false,TabType.NORMAL,null), // 16
        COMPANY_PROFILE("Company Profile", false,TabType.SETUP,new SetupTypeHandler()), // 17
        PORTALS("Portals", false,TabType.SETUP,new SetupTypeHandler()), // 18
        VISITOR_SETTINGS("Visitor Settings", false,TabType.SETUP,new SetupTypeHandler()), // 19
        FEEDBACK_COMPLAINTS("Feedback & Complaints", false,TabType.SETUP,new SetupTypeHandler()),// 20
        SMART_CONTROLS("Smart Controls",false,TabType.SETUP,new SetupTypeHandler()), // 21
        SERVICE_CATALOGS("Service Catalogs",false,TabType.SETUP,new SetupTypeHandler()), // 22
        TAX("Tax",false,TabType.SETUP,new SetupTypeHandler()), // 23
        USERS("Users",false,TabType.SETUP,new SetupTypeHandler()), // 24
        TEAMS("Teams",false,TabType.SETUP,new SetupTypeHandler()), // 25
        ROLES("Roles",false,TabType.SETUP,new SetupTypeHandler()), // 26
        LABOUR("Labour",false,TabType.SETUP,new SetupTypeHandler()), // 27
        CRAFTS("Crafts",false,TabType.SETUP,new SetupTypeHandler()), // 28
        PEOPLE("People",false,TabType.SETUP,new SetupTypeHandler()), // 29
        SINGLE_SIGN_ON("Single Sign-On",false,TabType.SETUP,new SetupTypeHandler()), // 30
        SECURITY_POLICY("Security Policy",false,TabType.SETUP,new SetupTypeHandler()), // 31
        SCOPE("Scope",false,TabType.SETUP,new SetupTypeHandler()), // 32
        EMAIL_SETTINGS("Email Settings",false,TabType.SETUP,new SetupTypeHandler()), // 33
        CATEGORY("Category",false,TabType.SETUP,new SetupTypeHandler()), // 34
        WORKORDER_CUSTOMIZATION("Customization",false,TabType.SETUP,new SetupTypeHandler()), // 35
        PRIORITY("Priority",false,TabType.SETUP,new SetupTypeHandler()), // 36
        TYPES("Types",false,TabType.SETUP,new SetupTypeHandler()), // 37
        SURVEY("Survey",false,TabType.NORMAL,new SetupTypeHandler()), // 38
        SPACE_ASSET_CUSTOMIZATION("Customization",false,TabType.SETUP,new SetupTypeHandler()), // 39
        READINGS("Readings",false,TabType.SETUP,new SetupTypeHandler()), // 40
        ASSET_DEPRECIATION("Asset Depreciation",false,TabType.SETUP,new SetupTypeHandler()), // 41
        SPACE_CATEGORIES("Space categories",false,TabType.SETUP,new SetupTypeHandler()), // 42
        OPERATING_HOURS("Operating Hours",false,TabType.SETUP,new SetupTypeHandler()), // 43
        DEPARTMENT("Department",false,TabType.SETUP,new SetupTypeHandler()), // 44
        TYPE("Type",false,TabType.SETUP,new SetupTypeHandler()), // 45
        WEATHER_STATION("Weather station",false,TabType.SETUP,new SetupTypeHandler()), // 46
        WORKFLOWS("Workflows", false,TabType.SETUP,new SetupTypeHandler()), // 47
        NOTIFICATIONS("Notifications",false,TabType.SETUP,new SetupTypeHandler()), // 48
        TRIGGERS("Triggers",false,TabType.SETUP,new SetupTypeHandler()), // 49
        CONDITION_MANAGER("Condition Manager",false,TabType.SETUP,new SetupTypeHandler()), // 50
        SCHEDULER("Scheduler",false,TabType.SETUP,new SetupTypeHandler()), // 51
        VARIABLES("Variables",false,TabType.SETUP,new SetupTypeHandler()), // 52
        SLA_POLICIES("SLA Policies",false,TabType.SETUP,new SetupTypeHandler()), // 53
        ASSIGNMENT_RULES("Assignment Rules",false,TabType.SETUP,new SetupTypeHandler()), // 54
        BMS_EVENT_FILTERING("BMS Event Filtering",false,TabType.SETUP,new SetupTypeHandler()), // 55
        SCORING_RULES("Scoring Rules",false,TabType.SETUP,new SetupTypeHandler()), // 56
        TRANSACTION_RULES("Transaction Rules",false,TabType.SETUP,new SetupTypeHandler()), // 57
        STATEFLOWS("Stateflows",false,TabType.SETUP,new SetupTypeHandler()), // 58
        APPROVALS("Approvals",false,TabType.SETUP,new SetupTypeHandler()), // 59
        MODULES("Modules",false,TabType.SETUP,new SetupTypeHandler()), // 60
        TABS_AND_LAYOUTS("Tabs and Layouts",false,TabType.SETUP,new SetupTypeHandler()), // 61
        CONNECTED_APPS("Connected Apps",false,TabType.SETUP,new SetupTypeHandler()), // 62
        CONNECTORS("Connectors",false,TabType.SETUP,new SetupTypeHandler()), // 63
        FUNCTIONS("Functions",false,TabType.SETUP,new SetupTypeHandler()), // 64
        EMAIL_TEMPLATES("Email Templates",false,TabType.SETUP,new SetupTypeHandler()), // 65
        LOCALIZATION("Localization",false,TabType.SETUP,new SetupTypeHandler()), // 66
        USER_SCOPES("User Scopes",false,TabType.SETUP,new SetupTypeHandler()), // 67
        ENERGY_METERS("Energy Meters",false,TabType.SETUP,new SetupTypeHandler()), // 68
        BASELINE("Baseline",false,TabType.SETUP,new SetupTypeHandler()), // 69
        FAULT_IMPACT_TEMPLATE("Fault Impact Template",false,TabType.SETUP,new SetupTypeHandler()), // 70
        ENPI("EnPI",false,TabType.SETUP,new SetupTypeHandler()), // 71
        API_SETUP("API Setup",false,TabType.SETUP,new SetupTypeHandler()), // 72
        BUNDLE("Bundle",false,TabType.SETUP,new SetupTypeHandler()), // 73
        INSTALL_BUNDLE("Install Bundle",false,TabType.SETUP,new SetupTypeHandler()), // 74
        EMAIL_LOGS("Email Logs",false,TabType.SETUP,new SetupTypeHandler()), // 75
        AUDIT_LOGS("Audit Logs",false,TabType.SETUP,new SetupTypeHandler()), // 76
        BOOKING_POLICY("Booking Policy",false,TabType.SETUP,new SetupTypeHandler()), // 77
        PIVOT("Pivot",false,TabType.NORMAL), // 78
        SCOPING("Scoping",false,TabType.SETUP,new SetupTypeHandler()), // 79
        ORGANIZATION_SETTINGS("Organization", false, TabType.SETUP,new SetupTypeHandler()), // 80
        CLASSIFICATIONS("Classifications",false,TabType.SETUP), // 81
        SCRIPT_LOGS("Script Logs",false,TabType.SETUP), // 82
        SHIFT_PLANNER("Shift Planner", false, TabType.NORMAL, new ShiftPlannerHandler()), // 83
        MY_ATTENDANCE("My Attendance", false, TabType.NORMAL, new MyAttendanceHandler()), // 84
        ATTENDANCE("Attendance", false, TabType.NORMAL, new AttendanceHandler()), // 85
        SETUP_SURVEY("Survey",false,TabType.SETUP,new SetupTypeHandler()), // 86
        NEW_KPI ("New Kpi", false,TabType.NORMAL, new ReadingKpiTypeHandler()), // 87
        RULES("ReadingRule",false,TabType.NORMAL,new ModuleTypeHandler()), // 88
        NEW_KPI_TEMPLATES ("KPI Templates", false,TabType.SETUP,new SetupTypeHandler()), // 89
        DATA_SHARING("Data Sharing", false, TabType.SETUP,new SetupTypeHandler()),
        IMPORT_DATA("Import Data",false,TabType.SETUP,new SetupTypeHandler()), //91
        DECOMMISSION("Decommission",false,TabType.SETUP,new SetupTypeHandler()); // 92

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

        @Getter
        private WebTabHandler handler;

        public String getName() {
            return name;
        }

        Type(String name, boolean isGroupedType, TabType tabType) {
            this.name = name;
            this.isGroupedType = isGroupedType;
            this.tabType = tabType;
        }

        Type(String name, boolean isGroupedType, TabType tabType, WebTabHandler handler) {
            this.name = name;
            this.isGroupedType = isGroupedType;
            this.tabType = tabType;
            this.handler = handler;
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
    private long permissionVal2;
    public long getPermissionVal2() {
        return permissionVal2;
    }
    public void setPermissionVal2(long permissionVal2) {
        this.permissionVal2 = permissionVal2;
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