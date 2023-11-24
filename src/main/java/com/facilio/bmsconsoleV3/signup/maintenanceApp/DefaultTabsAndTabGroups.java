package com.facilio.bmsconsoleV3.signup.maintenanceApp;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.IconType;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.view.FacilioView;
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
    public DefaultTabsAndTabGroups(){
    }

    private List<Long> getModuleIdsListFromModuleNames(List<String> moduleNames) throws Exception {
        List<Long> moduleIds = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(CollectionUtils.isNotEmpty(moduleNames)){
            for (String moduleName : moduleNames) {
                moduleIds.add(modBean.getModule(moduleName).getModuleId());
            }
        }
        return moduleIds;
    }


    private Map<String, WebTabContext> getWebTabs(long appId) throws Exception {
        Map<String, WebTabContext> tabsMap = new HashMap<String, WebTabContext>();

        List<WebTabContext> tabs = Arrays.asList(
            new WebTabContext("Dashboard", "dashboard", WebTabContext.Type.DASHBOARD, null, null, null,null,appId),
            new WebTabContext("Portfolio", "portfolio", WebTabContext.Type.PORTFOLIO, null, "{ \"type\": \"portfolio\" }", 1,null,appId),
            new WebTabContext("Assets", "assets", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ASSET)), null, 4,null,appId),
            new WebTabContext("Reports", "assetreport", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ASSET)), "{\"type\": \"module_reports\"}", 4,null,appId),
            new WebTabContext("Tenants", "tenants", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TENANT)), null, 16,null,appId),
            new WebTabContext("Tenant Contact", "tenantcontact", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TENANT_CONTACT)), null, 16,null,appId),
            new WebTabContext("Tenant Units", "tenantunit", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)), null, 16,null,appId),
            new WebTabContext("Quote", "quote", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.QUOTE)), null, 16,null,appId),
            new WebTabContext("Reports", "tenantreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TENANT,FacilioConstants.ContextNames.TENANT_CONTACT,FacilioConstants.ContextNames.TENANT_UNIT_SPACE,FacilioConstants.ContextNames.QUOTE)), "{\"type\": \"module_reports\"}", 16,null,appId),
            new WebTabContext("Workorder", "workorder", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER)), null, 1,null,appId),
            new WebTabContext("Approval", "approval", WebTabContext.Type.APPROVAL, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER,FacilioConstants.ContextNames.ASSET)), null, 11,null,appId),
            new WebTabContext("Survey", "survey", WebTabContext.Type.SURVEY, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Survey.SURVEY_RESPONSE)), null, 64, null, appId),
            new WebTabContext("KPI", "kpi", WebTabContext.Type.KPI, Arrays.asList(), null, 30,null,appId),
            new WebTabContext("Reports", "workorderreport", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER)), "{\"type\": \"module_reports\"}", 1,null,appId),
            new WebTabContext("Calendar", "calendar", WebTabContext.Type.CUSTOM, null, "{\"type\": \"pmCalendar\"}", 1,null,appId),
            new WebTabContext("Preventive Maintenance", "preventivemaintenance", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE)), null, 1,Collections.singletonList("preventivemaintenance"),appId),
            new WebTabContext("Storeroom","storeroom",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.STORE_ROOM)), null, 14,null,appId),
            new WebTabContext("Items","items",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ITEM)), null, 14,null,appId),
            new WebTabContext("Tools","tools",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TOOL)), null, 14,null,appId),
            new WebTabContext("Service","service",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SERVICE)), null, 14,null,appId),
            new WebTabContext("Item Types","itemtypes",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ITEM_TYPES)), null, 14,null,appId),
            new WebTabContext("Tool Types","tooltypes",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TOOL_TYPES)), null, 14,null,appId),
            new WebTabContext("Inventory Requests","inventoryrequests",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.INVENTORY_REQUEST)), null, 14,null,appId),
            new WebTabContext("Transfer Requests","transferrequests",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TRANSFER_REQUEST)), null, 63,null,appId),
            new WebTabContext("Shipments","shipments",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT)), null, 63,null,appId),
            new WebTabContext("Labour","labour",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.LABOUR)), null, 14,null,appId),
            new WebTabContext("Reports", "inventoryreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.STORE_ROOM,FacilioConstants.ContextNames.ITEM,FacilioConstants.ContextNames.TOOL,FacilioConstants.ContextNames.SERVICE,FacilioConstants.ContextNames.ITEM_TYPES,FacilioConstants.ContextNames.TOOL_TYPES,FacilioConstants.ContextNames.INVENTORY_REQUEST)), "{\"type\": \"module_reports\"}", 14,null,appId),
            new WebTabContext("Inspection","inspection",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Inspection.INSPECTION_RESPONSE)), null, 54,null,appId),
            new WebTabContext("Inspection Templates","inspectiontemplates",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Inspection.INSPECTION_TEMPLATE)), null, 54,null,appId),
            new WebTabContext("Reports","inspectionreport",WebTabContext.Type.REPORT,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Inspection.INSPECTION_RESPONSE,FacilioConstants.Inspection.INSPECTION_TEMPLATE)), "{\"type\": \"module_reports\"}", 54,null,appId),
            new WebTabContext("Purchase Request","purchaserequest",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.PURCHASE_REQUEST)), null, 56,null,appId),
            new WebTabContext("Purchase Order","purchaseorder",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.PURCHASE_ORDER)), null, 56,null,appId),
            new WebTabContext("Receivables","receivables",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.RECEIVABLE)), null, 56,null,appId),
            new WebTabContext("Reports", "purchasereports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.PURCHASE_REQUEST,FacilioConstants.ContextNames.PURCHASE_ORDER,FacilioConstants.ContextNames.RECEIVABLE,FacilioConstants.ContextNames.TERMS_AND_CONDITIONS,FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION)), "{\"type\": \"module_reports\"}", 56,null,appId),
            new WebTabContext("Purchase","purchase",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.PURCHASE_CONTRACTS)), null, 21,null,appId),
            new WebTabContext("Lease/Rental","leaserental",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS)), null, 21,null,appId),
            new WebTabContext("Warranty","warranty",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.WARRANTY_CONTRACTS)), null, 21,null,appId),
            new WebTabContext("Labour Contracts","labourcontracts",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.LABOUR_CONTRACTS)), null, 21,null,appId),
            new WebTabContext("Terms & Conditions","termsandconditions",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS)), null, 21,null,appId),
            new WebTabContext("Reports", "contractreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.PURCHASE_CONTRACTS,FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,FacilioConstants.ContextNames.WARRANTY_CONTRACTS,FacilioConstants.ContextNames.LABOUR_CONTRACTS,FacilioConstants.ContextNames.TERMS_AND_CONDITIONS)), "{\"type\": \"module_reports\"}", 21,null,appId),
            new WebTabContext("Vendors","vendors",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.VENDORS)), null, 57,null,appId),
            new WebTabContext("Vendor Contact","vendorcontact",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.VENDOR_CONTACT)), null, 57,null,appId),
            new WebTabContext("Certificate Of Insurance","certificateofinsurance",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.INSURANCE)), null, 57,null,appId),
            new WebTabContext("Reports", "vendorreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.VENDORS,FacilioConstants.ContextNames.VENDOR_CONTACT)), "{\"type\": \"module_reports\"}", 57,null,appId),
            new WebTabContext("Induction","induction",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Induction.INDUCTION_RESPONSE)), null, 55,null,appId),
            new WebTabContext("Induction Templates","inductiontemplates",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Induction.INDUCTION_TEMPLATE)), null, 55,null,appId),
            new WebTabContext("Reports","inductionreport",WebTabContext.Type.REPORT,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Induction.INDUCTION_RESPONSE,FacilioConstants.Induction.INDUCTION_TEMPLATE)), "{\"type\": \"module_reports\"}", 55,null,appId),
            new WebTabContext("Service Requests","servicerequest",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SERVICE_REQUEST)), null, 31,null,appId),
            new WebTabContext("Service Catalog","servicecatalog",WebTabContext.Type.SERVICE_CATALOG,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SERVICE_REQUEST)), "{\"type\": \"serviceCatalog\"}", 31,null,appId),
            new WebTabContext("Contact Directory","contactdirectory",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CONTACT_DIRECTORY)), null, 31,null,appId),
            new WebTabContext("Documents","documents",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ADMIN_DOCUMENTS)), null, 31,null,appId),
            new WebTabContext("Reports","servicereport",WebTabContext.Type.REPORT,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SERVICE_REQUEST,FacilioConstants.ContextNames.CONTACT_DIRECTORY,FacilioConstants.ContextNames.ADMIN_DOCUMENTS)), "{\"type\": \"module_reports\"}", 31,null,appId),
            new WebTabContext("Facility","facility",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.FacilityBooking.FACILITY)), null, 53,null,appId),
            new WebTabContext("Amenity","amenity",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.FacilityBooking.AMENITY)), null, 53,null,appId),
            new WebTabContext("Booking","facilitybooking",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING)), null, 53,null,appId),
            new WebTabContext("Reports","facilityreport",WebTabContext.Type.REPORT,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.FacilityBooking.FACILITY,FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING)), "{\"type\": \"module_reports\"}", 53,null,appId),
            new WebTabContext("Visitor","visitor",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.VISITOR)), null, 29,null,appId),
            new WebTabContext("Visits","visits",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.VISITOR_LOG)), null, 29,null,appId),
            new WebTabContext("Invites","invites",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.INVITE_VISITOR)), null, 29,null,appId),
            new WebTabContext("Reports","visitorreport",WebTabContext.Type.REPORT,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.VISITOR,FacilioConstants.ContextNames.VISITOR_LOG,FacilioConstants.ContextNames.INVITE_VISITOR)), "{\"type\": \"module_reports\"}", 29,null,appId),
            new WebTabContext("Budget","budget",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Budget.BUDGET)), null, 49,null,appId),
            new WebTabContext("Chart Of Accounts","chartofaccounts",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT)), null, 49,null,appId),
            new WebTabContext("Account Type","accounttype",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE)), null, 49,null,appId),
            new WebTabContext("Reports","budgetreport",WebTabContext.Type.REPORT,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Budget.BUDGET,FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT,FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE)), "{\"type\": \"module_reports\"}", 49,null,appId),
            new WebTabContext("Announcement","announcement",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT)), null, 47,null,appId),
            new WebTabContext("Neighbourhood","neighbourhood",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD)), null, 47,null,appId),
            new WebTabContext("Deals and Offers","dealsandoffers",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS)), null, 47,null,appId),
            new WebTabContext("News and Information","newsandinformation",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION)), null, 47,null,appId),
            new WebTabContext("Audience","audience",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Tenant.AUDIENCE)), null, 47,null,appId),
            new WebTabContext("Job Plans","jobplans",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.JOB_PLAN)), null, 81,null,appId),
            new WebTabContext("Safety Plans","safetyplans",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SAFETY_PLAN)), null, 32,null,appId),
            new WebTabContext("RFQ","rfq",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION)), null, 74,null,appId),
            new WebTabContext("Client","client",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CLIENT)), null, 33,null,appId),
            new WebTabContext("Client Contact","clientcontact",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CLIENT_CONTACT)), null, 33,null,appId),
            new WebTabContext("Reports", "clientreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CLIENT,FacilioConstants.ContextNames.CLIENT_CONTACT)), "{\"type\": \"module_reports\"}", 33,null,appId),
            new WebTabContext("Planned Maintenance","planned-maintenance",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.PLANNEDMAINTENANCE)), null, 81,null,appId),
            new WebTabContext("Shift","shift",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SHIFT)), null, 61,null,appId),
            new WebTabContext("Shift Planner","shiftplanner",WebTabContext.Type.SHIFT_PLANNER,null, null, 61,null,appId),
            new WebTabContext("Break","break",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.BREAK)), null, 120,null,appId),
            new WebTabContext("Attendance","attendance",WebTabContext.Type.ATTENDANCE,null, null, 120,null,appId)

        );
        for(WebTabContext webTab : tabs){
            tabsMap.put(webTab.getRoute(),webTab);
        }
        return tabsMap;
    }


    public List<WebTabGroupContext> getWebTabGroups(long appId,long layoutId) throws Exception {
        Map<String,WebTabContext> tabsMap = getWebTabs(appId);
        List<WebTabGroupContext> webTabGroups = Arrays.asList(
            new WebTabGroupContext(Arrays.asList(tabsMap.get("dashboard"), tabsMap.get("portfolio"), tabsMap.get("approval"),tabsMap.get("survey")), "Home", "home", 1, 1, null, layoutId, IconType.home),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("assets"),tabsMap.get("assetreport")), "Asset", "asset", 6, 2, 4l,layoutId,IconType.asset),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("storeroom"),tabsMap.get("itemtypes"),tabsMap.get("items"),tabsMap.get("tooltypes"),tabsMap.get("tools"),tabsMap.get("inventoryrequests"),tabsMap.get("inventoryreports")), "Inventory", "inventory", 27, 3, 14l,layoutId,IconType.inventory),
            //new WebTabGroupContext(Arrays.asList(tabsMap.get("workorder"),tabsMap.get("planned-maintenance"),tabsMap.get("calendar"),tabsMap.get("resourcescheduler"),tabsMap.get("workorderreport")), "Maintenance", "maintenance", 0, 4, 1l,layoutId,IconType.maintenance)
            new WebTabGroupContext(Arrays.asList(tabsMap.get("workorder"),tabsMap.get("planned-maintenance"),tabsMap.get("workorderreport")), "Maintenance", "maintenance", 0, 4, 1l,layoutId,IconType.maintenance),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("jobplans"),tabsMap.get("safetyplans")), "Plans", "plans", 7, 5, 81l,layoutId,IconType.safety_plan),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("servicerequest"),tabsMap.get("servicecatalog"),tabsMap.get("contactdirectory"),tabsMap.get("documents"),tabsMap.get("servicereport")), "Help Center", "helpcenter", 8, 6, 31l,layoutId,IconType.service_request),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("inspection"),tabsMap.get("inspectiontemplates"),tabsMap.get("inspectionreport")), "Inspection", "inspection", 34, 7, 54l,layoutId,IconType.inspection),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("purchaserequest"),tabsMap.get("rfq"),tabsMap.get("purchaseorder"),tabsMap.get("receivables"),tabsMap.get("termsandconditions"),tabsMap.get("purchasereports")), "Procurement", "procurement", 25, 8, 56l,layoutId,IconType.procurement),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("vendors"),tabsMap.get("vendorcontact"),tabsMap.get("certificateofinsurance"),tabsMap.get("vendorreports")), "Vendor", "vendor", 26, 9, 57l,layoutId,IconType.vendor),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("tenants"),tabsMap.get("tenantcontact"),tabsMap.get("tenantunit"),tabsMap.get("quote"),tabsMap.get("tenantreports")), "Tenant", "tenant", 12, 10, 16l,layoutId,IconType.tenant),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("client"),tabsMap.get("clientcontact"),tabsMap.get("clientreports")), "Client", "client", 17, 11, 33l,layoutId,IconType.client),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("shift"),tabsMap.get("shiftplanner")), "Shift & Planner", "shiftplanner", 17, 12, 61L,layoutId,IconType.client),
            new WebTabGroupContext(Arrays.asList(tabsMap.get("break"),tabsMap.get("attendance")), "Attendance", "attendance", 17, 13, 120L,layoutId,IconType.people)

//            new WebTabGroupContext(Arrays.asList(tabsMap.get("purchase"),tabsMap.get("leaserental"),tabsMap.get("warranty"),tabsMap.get("labourcontracts"),tabsMap.get("termsandconditions"),tabsMap.get("contractreports")), "Contracts", "contracts", 18, 4, 21l,layoutId),
//            new WebTabGroupContext(Arrays.asList(tabsMap.get("announcement"),tabsMap.get("newsandinformation"),tabsMap.get("neighbourhood"),tabsMap.get("dealsandoffers"),tabsMap.get("audience")), "Community", "community", 21, 8, 16l,layoutId),
//            new WebTabGroupContext(Arrays.asList(tabsMap.get("induction"),tabsMap.get("inductiontemplates"),tabsMap.get("inductionreport")), "Induction", "induction", 35, 9, 47l,layoutId),
//            new WebTabGroupContext(Arrays.asList(tabsMap.get("facility"),tabsMap.get("amenity"),tabsMap.get("facilitybooking"),tabsMap.get("facilityreport")), "Facility", "facility", 24, 11, 53l,layoutId),
//            new WebTabGroupContext(Arrays.asList(tabsMap.get("visitor"),tabsMap.get("visits"),tabsMap.get("invites"),tabsMap.get("visitorreport")), "Visit", "visit", 16, 12, 29l,layoutId),
//            new WebTabGroupContext(Arrays.asList(tabsMap.get("budget"),tabsMap.get("chartofaccounts"),tabsMap.get("accounttype"),tabsMap.get("budgetreport")), "Budget", "budget", 23, 13, 49l,layoutId)
        );
        return webTabGroups;
    }
    public Map<String, List<WebTabContext>> getGroupNameVsTabsMap(long appId,long layoutId) throws Exception {
        Map<String, List<WebTabContext>> groupNameVsTabsMap = new HashMap<>();
        for(WebTabGroupContext webTabGroup : getWebTabGroups(appId,layoutId)){
            groupNameVsTabsMap.put(webTabGroup.getRoute(),webTabGroup.getWebTabs());
        }
        return groupNameVsTabsMap;
    }
}