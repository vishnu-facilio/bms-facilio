package com.facilio.bmsconsoleV3.signup.fsmApp;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.IconType;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;

import java.util.*;

public class FSMDefaultTabsAndTabGroups {
    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(com.facilio.bmsconsoleV3.signup.maintenanceApp.DefaultTabsAndTabGroups.class.getName());
    public FSMDefaultTabsAndTabGroups(){
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
                new WebTabContext("Portfolio", "portfolio", WebTabContext.Type.CUSTOM, null, "{ \"type\": \"portfolio\" }", 1,null,appId),
                new WebTabContext("Assets", "assets", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ASSET)), null, 4,null,appId),
                new WebTabContext("Reports", "assetreport", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ASSET)), "{\"type\": \"module_reports\"}", 4,null,appId),
                new WebTabContext("Tenants", "tenants", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TENANT)), null, 16,null,appId),
                new WebTabContext("Tenant Contact", "tenantcontact", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TENANT_CONTACT)), null, 16,null,appId),
                new WebTabContext("Tenant Units", "tenantunit", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)), null, 16,null,appId),
                new WebTabContext("Quote", "quote", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.QUOTE)), null, 16,null,appId),
                new WebTabContext("Reports", "tenantreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TENANT,FacilioConstants.ContextNames.TENANT_CONTACT,FacilioConstants.ContextNames.TENANT_UNIT_SPACE,FacilioConstants.ContextNames.QUOTE)), "{\"type\": \"module_reports\"}", 16,null,appId),
                new WebTabContext("Workorder", "workorder", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER)), null, 1,null,appId),
                new WebTabContext("Approval", "approval", WebTabContext.Type.APPROVAL, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER,FacilioConstants.ContextNames.ASSET)), null, 11,null,appId),
                new WebTabContext("Survey", "workorder-survey", WebTabContext.Type.SURVEY, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE)), null, 64, null, appId),
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
                new WebTabContext("Resource Scheduler","resourcescheduler",WebTabContext.Type.TIMELINE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER)), null, 68,null,appId),
                new WebTabContext("Audience","audience",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Tenant.AUDIENCE)), null, 47,null,appId),
                new WebTabContext("Job Plans","jobplans",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.JOB_PLAN)), null, 81,null,appId),
                new WebTabContext("Safety Plans","safetyplans",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SAFETY_PLAN)), null, 32,null,appId),
                new WebTabContext("RFQ","rfq",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION)), null, 74,null,appId),
                new WebTabContext("Client","client",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CLIENT)), null, 33,null,appId),
                new WebTabContext("Client Contact","clientcontact",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CLIENT_CONTACT)), null, 33,null,appId),
                new WebTabContext("Reports", "clientreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CLIENT,FacilioConstants.ContextNames.CLIENT_CONTACT)), "{\"type\": \"module_reports\"}", 33,null,appId),
                new WebTabContext("Preventive Maintenance","preventive-maintenance",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.PLANNEDMAINTENANCE)), null, 81,null,appId),
                new WebTabContext("Shift","shift",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SHIFT)), null, 61,null,appId),
                new WebTabContext("Shift Planner","shiftplanner",WebTabContext.Type.SHIFT_PLANNER,null, null, 61,null,appId),
                new WebTabContext("Break","break",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.BREAK)), null, 120,null,appId),
                new WebTabContext("Attendance","attendance",WebTabContext.Type.ATTENDANCE,null, null, 120,null,appId),
                new WebTabContext("Service Order","serviceOrder",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SERVICE_ORDER)), null, null,null,appId),
                new WebTabContext("Service Appointments","serviceAppointment",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)), null, null,null,appId)

        );
        for(WebTabContext webTab : tabs){
            tabsMap.put(webTab.getRoute(),webTab);
        }
        return tabsMap;
    }


    public List<WebTabGroupContext> getWebTabGroups(long appId, long layoutId) throws Exception {
        Map<String,WebTabContext> tabsMap = getWebTabs(appId);
        List<WebTabGroupContext> webTabGroups = Arrays.asList(
                new WebTabGroupContext(Arrays.asList(tabsMap.get("dashboard"), tabsMap.get("portfolio"), tabsMap.get("approval"),tabsMap.get("workorder-survey")), "Home", "home", 1, 1, null, layoutId, IconType.home),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("assets")), "Assets", "assets", 6, 2, null,layoutId,IconType.asset),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("serviceOrder"),tabsMap.get("serviceAppointment"),tabsMap.get("servicerequest"),tabsMap.get("preventive-maintenance")), "Service", "service", 6, 3, null,layoutId,IconType.service_request),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("quote")), "Financials", "financials", 6, 4, null,layoutId,IconType.procurement),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("client"),tabsMap.get("clientcontact")), "Client", "client", 17, 5, null,layoutId,IconType.client),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("shift"),tabsMap.get("attendance")), "Workforce", "workforce", 17, 6, null,layoutId,IconType.people),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("items"),tabsMap.get("tools"),tabsMap.get("service"),tabsMap.get("storeroom"),tabsMap.get("itemtypes"),tabsMap.get("tooltypes")), "Inventory", "inventory", 17, 6, null,layoutId,IconType.inventory)
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
