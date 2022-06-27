package com.facilio.bmsconsoleV3.signup.maintenanceApp;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.cb.util.ChatBotMLUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.jcraft.jsch.Logger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.*;
import java.util.logging.Level;
public class DefaultTabsAndTabGroups {
    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(DefaultTabsAndTabGroups.class.getName());
    private long appId;
    private long layoutId;
    public long getLayoutId() {
        return layoutId;
    }
    public void setLayoutId(long layoutId) {
        this.layoutId = layoutId;
    }
    public long getAppId() {
        return appId;
    }
    public void setAppId(long appId) {
        this.appId = appId;
    }
    public DefaultTabsAndTabGroups(long appId, long layoutId) {
        this.appId = appId;
        this.layoutId = layoutId;
    }
    private enum Tabs {
        DASHBOARD("Dashboard", "dashboard", WebTabContext.Type.DASHBOARD, null, null, null,null),
        PORTFOLIO("Portfolio", "portfolio", WebTabContext.Type.CUSTOM, null, "{ \"type\": \"portfolio\" }", 1,null),
        ASSET("Assets", "assets", WebTabContext.Type.MODULE, Collections.singletonList(FacilioConstants.ContextNames.ASSET), null, 4,null),
        ASSET_REPORTS("Reports", "assetreport", WebTabContext.Type.REPORT, Collections.singletonList(FacilioConstants.ContextNames.ASSET), "{\"type\": \"module_reports\"}", 4,null),
        TENANTS("Tenants", "tenants", WebTabContext.Type.MODULE, Collections.singletonList(FacilioConstants.ContextNames.TENANT), null, 16,null),
        TENANT_CONTACT("Tenant Contact", "tenantcontact", WebTabContext.Type.MODULE, Collections.singletonList(FacilioConstants.ContextNames.TENANT_CONTACT), null, 16,null),
        TENANT_UNIT_SPACE("Tenant Units", "tenantunit", WebTabContext.Type.MODULE, Collections.singletonList(FacilioConstants.ContextNames.TENANT_UNIT_SPACE), null, 16,null),
        QUOTE("Quote", "quote", WebTabContext.Type.MODULE, Collections.singletonList(FacilioConstants.ContextNames.QUOTE), null, 16,null),
        TENANT_REPORTS("Reports", "tenantreports", WebTabContext.Type.REPORT, Arrays.asList(FacilioConstants.ContextNames.TENANT,FacilioConstants.ContextNames.TENANT_CONTACT,FacilioConstants.ContextNames.TENANT_UNIT_SPACE,FacilioConstants.ContextNames.QUOTE), "{\"type\": \"module_reports\"}", 16,null),
        WORKORDER("Workorder", "workorder", WebTabContext.Type.MODULE, Collections.singletonList(FacilioConstants.ContextNames.WORK_ORDER), null, 1,null),
        APPROVAL("Approval", "approval", WebTabContext.Type.APPROVAL, Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER,FacilioConstants.ContextNames.ASSET), null, 11,null),
        KPI("KPI", "kpi", WebTabContext.Type.KPI, Arrays.asList(), null, 30,null),
        WORKORDER_REPORTS("Reports", "workorderreport", WebTabContext.Type.REPORT, Collections.singletonList(FacilioConstants.ContextNames.WORK_ORDER), "{\"type\": \"module_reports\"}", 1,null),
        PM_CALENDER("Calender", "calender", WebTabContext.Type.CUSTOM, null, "{\"type\": \"pmCalendar\"}", 1,null),
        PREVENTIVE_MAINTENANCE("Preventive Maintenance", "preventivemaintenance", WebTabContext.Type.MODULE, Collections.singletonList(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE), null, 1,Collections.singletonList("preventivemaintenance")),
        STOREROOM("Storeroom","storeroom",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.STORE_ROOM), null, 14,null),
        ITEMS("Items","items",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.ITEM), null, 14,null),
        TOOLS("Tools","tools",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.TOOL), null, 14,null),
        SERVICE("Service","service",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.SERVICE), null, 14,null),
        ITEM_TYPES("Item Types","itemtypes",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.ITEM_TYPES), null, 14,null),
        TOOL_TYPES("Tool Types","tooltypes",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.TOOL_TYPES), null, 14,null),
        INVENTORY_REQUESTS("Inventory Requests","inventoryrequests",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.INVENTORY_REQUEST), null, 14,null),
        TRANSFER_REQUESTS("Transfer Requests","transferrequests",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.TRANSFER_REQUEST), null, 63,null),
        SHIPMENTS("Shipments","shipments",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT), null, 63,null),
        LABOURS("Labour","labour",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.LABOUR), null, 14,null),
        INVENTORY_REPORTS("Reports", "inventoryreports", WebTabContext.Type.REPORT, Arrays.asList(FacilioConstants.ContextNames.STORE_ROOM,FacilioConstants.ContextNames.ITEM,FacilioConstants.ContextNames.TOOL,FacilioConstants.ContextNames.SERVICE,FacilioConstants.ContextNames.ITEM_TYPES,FacilioConstants.ContextNames.TOOL_TYPES,FacilioConstants.ContextNames.INVENTORY_REQUEST,FacilioConstants.ContextNames.TRANSFER_REQUEST,FacilioConstants.ContextNames.SHIPMENT,FacilioConstants.ContextNames.LABOUR,FacilioConstants.ContextNames.ITEM_TRANSACTIONS,FacilioConstants.ContextNames.TOOL_TRANSACTIONS), "{\"type\": \"module_reports\"}", 14,null),
        INSPECTION("Inspection","inspection",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.Inspection.INSPECTION_RESPONSE), null, 54,null),
        INSPECTION_TEMPLATES("Inspection Templates","inspectiontemplates",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.Inspection.INSPECTION_TEMPLATE), null, 54,null),
        INSPECTION_REPORTS("Reports","inspectionreport",WebTabContext.Type.REPORT,Arrays.asList(FacilioConstants.Inspection.INSPECTION_RESPONSE,FacilioConstants.Inspection.INSPECTION_TEMPLATE), "{\"type\": \"module_reports\"}", 54,null),
        PURCHASE_REQUEST("Purchase Request","purchaserequest",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.PURCHASE_REQUEST), null, 56,null),
        PURCHASE_ORDER("Purchase Order","purchaseorder",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.PURCHASE_ORDER), null, 56,null),
        RECEIVABLES("Receivables","receivables",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.RECEIVABLE), null, 56,null),
        PURCHASE_REPORTS("Reports", "purchasereports", WebTabContext.Type.REPORT, Arrays.asList(FacilioConstants.ContextNames.PURCHASE_REQUEST,FacilioConstants.ContextNames.PURCHASE_ORDER,FacilioConstants.ContextNames.RECEIVABLE,FacilioConstants.ContextNames.TERMS_AND_CONDITIONS), "{\"type\": \"module_reports\"}", 56,null),
        PURCHASE("Purchase","purchase",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.PURCHASE_CONTRACTS), null, 21,null),
        LEASE_RENTAL("Lease/Rental","leaserental",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS), null, 21,null),
        WARRANTY("Warranty","warranty",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.WARRANTY_CONTRACTS), null, 21,null),
        LABOUR_CONTRACTS("Labour Contracts","labourcontracts",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.LABOUR_CONTRACTS), null, 21,null),
        TERMS_AND_CONDITIONS("Terms & Conditions","termsandconditions",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS), null, 21,null),
        CONTRACT_REPORTS("Reports", "contractreports", WebTabContext.Type.REPORT, Arrays.asList(FacilioConstants.ContextNames.PURCHASE_CONTRACTS,FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,FacilioConstants.ContextNames.WARRANTY_CONTRACTS,FacilioConstants.ContextNames.LABOUR_CONTRACTS,FacilioConstants.ContextNames.TERMS_AND_CONDITIONS), "{\"type\": \"module_reports\"}", 21,null),
        VENDOR("Vendors","vendors",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.VENDORS), null, 57,null),
        VENDOR_CONTACT("Vendor Contact","vendorcontact",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.VENDOR_CONTACT), null, 57,null),
        CERTIFICATE_OF_INSURANCE("Certificate Of Insurance","certificateofinsurance",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.ContextNames.INSURANCE), null, 57,null),
        VENDOR_REPORTS("Reports", "vendorreports", WebTabContext.Type.REPORT, Arrays.asList(FacilioConstants.ContextNames.VENDORS,FacilioConstants.ContextNames.VENDOR_CONTACT), "{\"type\": \"module_reports\"}", 57,null),
        INDUCTION("Induction","induction",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.Induction.INDUCTION_RESPONSE), null, 55,null),
        INDUCTION_TEMPLATES("Induction Templates","inductiontemplates",WebTabContext.Type.MODULE,Collections.singletonList(FacilioConstants.Induction.INDUCTION_TEMPLATE), null, 55,null),
        INDUCTION_REPORTS("Reports","inductionreport",WebTabContext.Type.REPORT,Arrays.asList(FacilioConstants.Induction.INDUCTION_RESPONSE,FacilioConstants.Induction.INDUCTION_TEMPLATE), "{\"type\": \"module_reports\"}", 55,null),
        SERVICE_REQUESTS("Service Requests","servicerequest",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.SERVICE_REQUEST), null, 31,null),
        SERVICE_CATALOG("Service Catalog","servicecatalog",WebTabContext.Type.CUSTOM,Collections.singletonList(FacilioConstants.ContextNames.SERVICE_REQUEST), "{\"type\": \"serviceCatalog\"}", 31,null),
        CONTACT_DIRECTORY("Contact Directory","contactdirectory",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.CONTACT_DIRECTORY), null, 31,null),
        ADMIN_DOCUMENTS("Documents","documents",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.ADMIN_DOCUMENTS), null, 31,null),
        SERVICE_REPORTS("Reports","servicereport",WebTabContext.Type.REPORT,Arrays.asList(FacilioConstants.ContextNames.SERVICE_REQUEST,FacilioConstants.ContextNames.CONTACT_DIRECTORY,FacilioConstants.ContextNames.ADMIN_DOCUMENTS), "{\"type\": \"module_reports\"}", 31,null),
        FACILITIES("Facility","facility",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.FacilityBooking.FACILITY), null, 53,null),
        AMENITY("Amenity","amenity",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.FacilityBooking.AMENITY), null, 53,null),
        FACILITY_BOOKING("Booking","facilitybooking",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING), null, 53,null),
        FACILITY_REPORTS("Reports","facilityreport",WebTabContext.Type.REPORT,Arrays.asList(FacilioConstants.ContextNames.FacilityBooking.FACILITY,FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING), "{\"type\": \"module_reports\"}", 53,null),
        VISITOR("Visitor","visitor",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.VISITOR), null, 29,null),
        VISITS("Visits","visits",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.VISITOR_LOG), null, 29,null),
        INVITES("Invites","invites",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.INVITE_VISITOR), null, 29,null),
        VISITOR_REPORTS("Reports","visitorreport",WebTabContext.Type.REPORT,Arrays.asList(FacilioConstants.ContextNames.VISITOR,FacilioConstants.ContextNames.VISITOR_LOG,FacilioConstants.ContextNames.INVITE_VISITOR), "{\"type\": \"module_reports\"}", 29,null),
        BUDGET("Budget","budget",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.Budget.BUDGET), null, 49,null),
        CHART_OF_ACCOUNTS("Chart Of Accounts","chartofaccounts",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT), null, 49,null),
        ACCOUNT_TYPE("Account Type","accounttype",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE), null, 49,null),
        BUDGET_REPORTS("Reports","budgetreport",WebTabContext.Type.REPORT,Arrays.asList(FacilioConstants.ContextNames.Budget.BUDGET,FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT,FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE), "{\"type\": \"module_reports\"}", 49,null),
        ANNOUNCEMENT("Announcement","announcement",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT), null, 47,null),
        NEIGHBOURHOOD("Neighbourhood","neighbourhood",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD), null, 47,null),
        DEALSANDOFFERS("Deals and Offers","dealsandoffers",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS), null, 47,null),
        NEWSANDINFORMATION("News and Information","newsandinformation",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION), null, 47,null),
        AUDIENCE("Audience","audience",WebTabContext.Type.MODULE,Arrays.asList(FacilioConstants.ContextNames.Tenant.AUDIENCE), null, 47,null);
        public String displayName;
        public String name;
        private WebTabContext.Type type;
        private List<String> moduleNames;
        private JSONObject config;
        private Integer featureLicense;
        private List<String> specialTypeModules;
        Tabs(String displayName, String name, WebTabContext.Type type, List<String> moduleNames, String config, Integer featureLicense,List<String> specialTypeModules) {
            try {
                this.displayName = displayName;
                this.name = name;
                this.type = type;
                this.moduleNames = moduleNames;
                if(config != null){
                    this.config = (JSONObject) new JSONParser().parse(config);
                }
                this.featureLicense = featureLicense;
                this.specialTypeModules = specialTypeModules;
            }catch (Exception e){
                LOGGER.info(e.getMessage());
            }
        }
        public String getDisplayName() {
            return displayName;
        }
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
        public String getName() {
            return name;
        }
        public Integer getFeatureLicense() {
            return featureLicense;
        }
        public void setFeatureLicense(Integer featureLicense) {
            this.featureLicense = featureLicense;
        }
        public void setName(String name) {
            this.name = name;
        }
        public WebTabContext.Type getType() {
            return type;
        }
        public void setType(WebTabContext.Type type) {
            this.type = type;
        }
        public List<String> getModuleNames() {
            return moduleNames;
        }
        public void setModuleNames(List<String> moduleNames) {
            this.moduleNames = moduleNames;
        }
        public JSONObject getConfig() {
            return config;
        }
        public void setConfig(JSONObject config) {
            this.config = config;
        }
        public List<String> getSpecialTypeModules() {
            return specialTypeModules;
        }
        public void setSpecialTypeModules(List<String> specialTypeModules) {
            this.specialTypeModules = specialTypeModules;
        }
    }
    private enum TabGroups {
        HOME(Arrays.asList(Tabs.DASHBOARD,Tabs.PORTFOLIO,Tabs.APPROVAL,Tabs.KPI), "Home", "home", 1, 1, null),
        ASSETS(Arrays.asList(Tabs.ASSET,Tabs.ASSET_REPORTS), "Asset", "asset", 6, 2, 4l),
        INVENTORY(Arrays.asList(Tabs.STOREROOM,Tabs.ITEM_TYPES,Tabs.ITEMS,Tabs.TOOL_TYPES,Tabs.TOOLS,Tabs.INVENTORY_REQUESTS,Tabs.TRANSFER_REQUESTS,Tabs.SHIPMENTS,Tabs.SERVICE,Tabs.LABOURS,Tabs.INVENTORY_REPORTS), "Inventory", "inventory", 27, 3, 14l),
        MAINTENANCE(Arrays.asList(Tabs.WORKORDER,Tabs.PREVENTIVE_MAINTENANCE,Tabs.PM_CALENDER,Tabs.WORKORDER_REPORTS), "Maintenance", "maintenance", 0, 3, 1l),
        INSPECTION(Arrays.asList(Tabs.INSPECTION,Tabs.INSPECTION_TEMPLATES,Tabs.INSPECTION_REPORTS), "Inspection", "inspection", 34, 3, 54l),
        PURCHASE(Arrays.asList(Tabs.PURCHASE_REQUEST,Tabs.PURCHASE_ORDER,Tabs.RECEIVABLES,Tabs.TERMS_AND_CONDITIONS,Tabs.PURCHASE_REPORTS), "Purchase", "purchase", 25, 4, 56l),
        CONTRACTS(Arrays.asList(Tabs.PURCHASE,Tabs.LEASE_RENTAL,Tabs.WARRANTY,Tabs.LABOUR_CONTRACTS,Tabs.TERMS_AND_CONDITIONS,Tabs.CONTRACT_REPORTS), "Contracts", "contracts", 18, 4, 21l),
        TENANT(Arrays.asList(Tabs.TENANTS,Tabs.TENANT_CONTACT,Tabs.TENANT_UNIT_SPACE,Tabs.QUOTE,Tabs.TENANT_REPORTS), "Tenant", "tenant", 12, 6, 16l),
        VENDOR(Arrays.asList(Tabs.VENDOR,Tabs.VENDOR_CONTACT,Tabs.CERTIFICATE_OF_INSURANCE,Tabs.VENDOR_REPORTS), "Vendor", "vendor", 26, 7, 57l),
        COMMUNITY(Arrays.asList(Tabs.ANNOUNCEMENT,Tabs.NEWSANDINFORMATION,Tabs.NEIGHBOURHOOD,Tabs.DEALSANDOFFERS,Tabs.AUDIENCE), "Community", "community", 21, 8, 16l),
        INDUCTION(Arrays.asList(Tabs.INDUCTION,Tabs.INDUCTION_TEMPLATES,Tabs.INDUCTION_REPORTS), "Induction", "induction", 35, 9, 47l),
        SERVICE(Arrays.asList(Tabs.SERVICE_REQUESTS,Tabs.SERVICE_CATALOG,Tabs.CONTACT_DIRECTORY,Tabs.ADMIN_DOCUMENTS,Tabs.SERVICE_REPORTS), "Service", "service", 8, 10, 31l),
        FACILITY(Arrays.asList(Tabs.FACILITIES,Tabs.AMENITY,Tabs.FACILITY_BOOKING,Tabs.FACILITY_REPORTS), "Facility", "facility", 24, 11, 53l),
        VISIT(Arrays.asList(Tabs.VISITOR,Tabs.VISITS,Tabs.INVITES,Tabs.VISITOR_REPORTS), "Visit", "visit", 16, 12, 29l),
        BUDGET(Arrays.asList(Tabs.BUDGET,Tabs.CHART_OF_ACCOUNTS,Tabs.ACCOUNT_TYPE,Tabs.BUDGET_REPORTS), "Budget", "budget", 23, 13, 49l);

        //        ,Tabs.RECEIVABLES,Tabs.TERMS_AND_CONDITIONS,Tabs.CERTIFICATE_OF_INSURANCE,Tabs.SERVICE_CATALOG,Visitors,Watchlist
//        ONLY_TABS(Arrays.asList(Tabs.STOREROOM,Tabs.TENANTS,Tabs.TENANT_CONTACT,Tabs.TENANT_UNIT_SPACE,Tabs.ITEMS,Tabs.TOOLS,Tabs.ITEM_TYPES,Tabs.TOOL_TYPES,Tabs.SERVICE,Tabs.INVENTORY_REQUESTS,Tabs.TRANSFER_REQUESTS,Tabs.SHIPMENTS,Tabs.INVENTORY_REPORTS,Tabs.QUOTE,Tabs.TENANT_REPORTS), "ONLY_TABS", "onlytabs", 201, 3, null);
        private List<Tabs> tabs;
        private String name;
        private String route;
        private int iconType;
        private int order;
        private Long featureLicense;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getRoute() {
            return route;
        }
        public void setRoute(String route) {
            this.route = route;
        }
        public int getIconType() {
            return iconType;
        }
        public void setIconType(int iconType) {
            this.iconType = iconType;
        }
        public int getOrder() {
            return order;
        }
        public void setOrder(int order) {
            this.order = order;
        }
        public Long getFeatureLicense() {
            return featureLicense;
        }
        public void setFeatureLicense(Long featureLicense) {
            this.featureLicense = featureLicense;
        }
        TabGroups(List<Tabs> tabs, String name, String route, int iconType, int order, Long featureLicense) {
            this.tabs = tabs;
            this.name = name;
            this.route = route;
            this.iconType = iconType;
            this.order = order;
            this.featureLicense = featureLicense;
        }
        public List<Tabs> getTabs() {
            return tabs;
        }
        public void setTabs(List<Tabs> tabs) {
            this.tabs = tabs;
        }
    }
    private Map<String, List<WebTabContext>> groupNameVsTabsMap = new HashMap<>();
    public List<WebTabGroupContext> getWebTabGroups() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<WebTabGroupContext> webTabGroups = new ArrayList<>();
        for (TabGroups tabGroup : TabGroups.values()) {
            List<WebTabContext> webTabs = new ArrayList<>();
            for (Tabs tab : tabGroup.getTabs()) {
                List<Long> moduleIds = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(tab.getModuleNames())){
                    for (String moduleName : tab.getModuleNames()) {
                        moduleIds.add(modBean.getModule(moduleName).getModuleId());
                    }
                }
                WebTabContext webTab;
                if (tab.getFeatureLicense() != null) {
                    webTab = new WebTabContext(tab.getDisplayName(), tab.getName(), tab.getType(), moduleIds, getAppId(), tab.getConfig(), tab.getFeatureLicense());
                } else {
                    webTab = new WebTabContext(tab.getDisplayName(), tab.getName(), tab.getType(), moduleIds, getAppId(), tab.getConfig());
                }
                if (tab.getSpecialTypeModules() != null) {
                    webTab.setSpecialTypeModules(tab.getSpecialTypeModules());
                }
                webTabs.add(webTab);
            }
            if (tabGroup.getFeatureLicense() != null) {
                webTabGroups.add(new WebTabGroupContext(tabGroup.getName(), tabGroup.getRoute(), getLayoutId(), tabGroup.getIconType(), tabGroup.getOrder(), tabGroup.getFeatureLicense()));
            } else {
                webTabGroups.add(new WebTabGroupContext(tabGroup.getName(), tabGroup.getRoute(), getLayoutId(), tabGroup.getIconType(), tabGroup.getOrder()));
            }
            groupNameVsTabsMap.put(tabGroup.getRoute(), webTabs);
        }
        return webTabGroups;
    }
    public Map<String, List<WebTabContext>> getGroupNameVsTabsMap() throws Exception {
        return groupNameVsTabsMap;
    }
}