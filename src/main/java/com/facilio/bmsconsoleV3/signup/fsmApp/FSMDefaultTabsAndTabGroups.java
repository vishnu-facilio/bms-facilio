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
                new WebTabContext("Dispatch Console","dispatch",WebTabContext.Type.DISPATCHER_CONSOLE,null, null, null,null,appId),
                new WebTabContext("Approvals", "approval", WebTabContext.Type.APPROVAL, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.TimeOff.TIME_OFF)), null, null,null,appId),
                new WebTabContext("Portfolio", "portfolio", WebTabContext.Type.CUSTOM, null, "{ \"type\": \"portfolio\" }", 1,null,appId),
                new WebTabContext("Territory","territory",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Territory.TERRITORY)), null, null,null,appId),
                new WebTabContext("Assets", "assets", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ASSET)), null, 4,null,appId),
                new WebTabContext("Service Order","serviceOrder",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SERVICE_ORDER)), null, null,null,appId),
                new WebTabContext("Service Appointment","serviceAppointment",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)), null, null,null,appId),
                new WebTabContext("Quote", "quote", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.QUOTE)), null, 42,null,appId),
                new WebTabContext("Employees","employees",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.EMPLOYEE)), null, null,null,appId),
                new WebTabContext("Trips","trip",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Trip.TRIP)), null, null,null,appId),
                new WebTabContext("Time Sheet","timeSheet",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.TimeSheet.TIME_SHEET)), null, null,null,appId),
                new WebTabContext("Shift","shift",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SHIFT)), null, 61,null,appId),
                new WebTabContext("Shift Planner","shiftplanner",WebTabContext.Type.SHIFT_PLANNER,null, null, 61,null,appId),
                new WebTabContext("Attendance","attendance",WebTabContext.Type.ATTENDANCE,null, null, 120,null,appId),
                new WebTabContext("My Attendance", "myAttendance", WebTabContext.Type.MY_ATTENDANCE,null,null,null,null,appId),
                new WebTabContext("Time-off","timeOff",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.TimeOff.TIME_OFF)), null, null,null,appId),
                new WebTabContext("Vendor","vendors",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.VENDORS)), null, 57,null,appId),
                new WebTabContext("Vendor Contact","vendorcontact",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.VENDOR_CONTACT)), null, 57,null,appId),
                new WebTabContext("Client","client",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CLIENT)), null, 33,null,appId),
                new WebTabContext("Client Contact","clientcontact",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CLIENT_CONTACT)), null, 33,null,appId),
                new WebTabContext("Items","items",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ITEM)), null, 14,null,appId),
                new WebTabContext("Tools","tools",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TOOL)), null, 14,null,appId),
                new WebTabContext("Service","service",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SERVICE)), null, 14,null,appId),
                new WebTabContext("Storeroom","storeroom",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.STORE_ROOM)), null, 14,null,appId),
                new WebTabContext("Item Type","itemtypes",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ITEM_TYPES)), null, 14,null,appId),
                new WebTabContext("Tool Type","tooltypes",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TOOL_TYPES)), null, 14,null,appId),
                new WebTabContext("Inventory Requests","inventoryrequests",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.INVENTORY_REQUEST)), null, 14,null,appId),
                new WebTabContext("Transfer Requests","transferrequests",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TRANSFER_REQUEST)), null, 63,null,appId),
                new WebTabContext("Dashboard","homepage",WebTabContext.Type.HOMEPAGE,null,null,null,null,appId),
                new WebTabContext("Service Appointment", "serviceAppointment_mobile",  WebTabContext.Type.MODULE, null,null,null,null,appId),
                new WebTabContext("Time Off", "timeOff_mobile",  WebTabContext.Type.MODULE, null,null,null,null, appId ),
                new WebTabContext("Service Order", "serviceOrder_mobile",  WebTabContext.Type.MODULE, null,null,null,null, appId),
                new WebTabContext("Attendance", "attendance_mobile",  WebTabContext.Type.ATTENDANCE, null,null,null,null, appId),
                new WebTabContext("Time Sheet", "timeSheet_mobile",  WebTabContext.Type.MODULE, null,null,null,null, appId),
                new WebTabContext("Trip", "trip_mobile",  WebTabContext.Type.MODULE, null,null,null,null, appId )


//                new WebTabContext("Reports", "tenantreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TENANT,FacilioConstants.ContextNames.TENANT_CONTACT,FacilioConstants.ContextNames.TENANT_UNIT_SPACE,FacilioConstants.ContextNames.QUOTE)), "{\"type\": \"module_reports\"}", 16,null,appId),
//                new WebTabContext("Reports", "assetreport", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ASSET,FacilioConstants.TimeOff.TIME_OFF,FacilioConstants.TimeSheet.TIME_SHEET,FacilioConstants.Territory.TERRITORY,FacilioConstants.Trip.TRIP,FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,FacilioConstants.ContextNames.EMPLOYEE,FacilioConstants.ContextNames.VENDOR_CONTACT)), "{\"type\": \"module_reports\"}", 4,null,appId),
//                new WebTabContext("Reports", "workorderreport", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER)), "{\"type\": \"module_reports\"}", 1,null,appId),
//                new WebTabContext("Reports", "inventoryreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.STORE_ROOM,FacilioConstants.ContextNames.ITEM,FacilioConstants.ContextNames.TOOL,FacilioConstants.ContextNames.SERVICE,FacilioConstants.ContextNames.ITEM_TYPES,FacilioConstants.ContextNames.TOOL_TYPES,FacilioConstants.ContextNames.INVENTORY_REQUEST)), "{\"type\": \"module_reports\"}", 14,null,appId),
//                new WebTabContext("Reports","inspectionreport",WebTabContext.Type.REPORT,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Inspection.INSPECTION_RESPONSE,FacilioConstants.Inspection.INSPECTION_TEMPLATE)), "{\"type\": \"module_reports\"}", 54,null,appId),
//                new WebTabContext("Reports", "purchasereports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.PURCHASE_REQUEST,FacilioConstants.ContextNames.PURCHASE_ORDER,FacilioConstants.ContextNames.RECEIVABLE,FacilioConstants.ContextNames.TERMS_AND_CONDITIONS,FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION)), "{\"type\": \"module_reports\"}", 56,null,appId),
//                new WebTabContext("Reports", "contractreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.PURCHASE_CONTRACTS,FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,FacilioConstants.ContextNames.WARRANTY_CONTRACTS,FacilioConstants.ContextNames.LABOUR_CONTRACTS,FacilioConstants.ContextNames.TERMS_AND_CONDITIONS)), "{\"type\": \"module_reports\"}", 21,null,appId),
//                new WebTabContext("Reports", "vendorreports", WebTabContext.Type.REPORT, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.VENDORS,FacilioConstants.ContextNames.VENDOR_CONTACT)), "{\"type\": \"module_reports\"}", 57,null,appId),
//                new WebTabContext("Reports","inductionreport",WebTabContext.Type.REPORT,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Induction.INDUCTION_RESPONSE,FacilioConstants.Induction.INDUCTION_TEMPLATE)), "{\"type\": \"module_reports\"}", 55,null,appId),
//                new WebTabContext("Reports","servicereport",WebTabContext.Type.REPORT,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SERVICE_REQUEST,FacilioConstants.ContextNames.CONTACT_DIRECTORY,FacilioConstants.ContextNames.ADMIN_DOCUMENTS)), "{\"type\": \"module_reports\"}", 31,null,appId),
//                new WebTabContext("Reports","budgetreport",WebTabContext.Type.REPORT,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.Budget.BUDGET,FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT,FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE)), "{\"type\": \"module_reports\"}", 49,null,appId),


        );
        for(WebTabContext webTab : tabs){
            tabsMap.put(webTab.getRoute(),webTab);
        }
        return tabsMap;
    }


    public List<WebTabGroupContext> getWebTabGroups(long appId, long layoutId) throws Exception {
        Map<String,WebTabContext> tabsMap = getWebTabs(appId);
        List<WebTabGroupContext> webTabGroups = Arrays.asList(
                new WebTabGroupContext(Arrays.asList(tabsMap.get("dashboard"), tabsMap.get("dispatch"),tabsMap.get("approval")), "Home", "home", 1, 1, null, layoutId, IconType.home),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("portfolio"), tabsMap.get("assets")), "Portfolio", "portfolio", 1, 1, null, layoutId, IconType.portfolio),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("serviceOrder"),tabsMap.get("serviceAppointment")), "Services", "service", 6, 3, null,layoutId,IconType.service_order),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("quote")), "Financials", "financials", 6, 5, null,layoutId,IconType.invoice),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("employees"),tabsMap.get("trip"),tabsMap.get("timeSheet"),tabsMap.get("shift"),tabsMap.get("shiftplanner"),tabsMap.get("attendance"),tabsMap.get("myAttendance"),tabsMap.get("timeOff")), "Workforce", "workforce", 17, 7, null,layoutId,IconType.workforce),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("client"),tabsMap.get("clientcontact")), "Client", "client", 17, 6, null,layoutId,IconType.client),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("vendors"),tabsMap.get("vendorcontact")), "Vendor", "vendor", 17, 6, null,layoutId,IconType.vendor),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("items"),tabsMap.get("tools"),tabsMap.get("service"),tabsMap.get("storeroom"),tabsMap.get("itemtypes"),tabsMap.get("tooltypes"),tabsMap.get("inventoryrequests"),tabsMap.get("transferrequests")), "Inventory", "inventory", 17, 8, null,layoutId,IconType.inventory)
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

    public List<WebTabGroupContext> getWebTabGroupsForMobile(long appId, long layoutId) throws Exception {
        Map<String,WebTabContext> tabsMap = getWebTabs(appId);
        List<WebTabGroupContext> webTabGroups = Arrays.asList(
                new WebTabGroupContext(Arrays.asList(tabsMap.get("homepage")), "Home", "home", 1, 1, null, layoutId, IconType.home),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("serviceAppointment_mobile")), "Service Appointment", "serviceAppointment", 1, 1, null, layoutId, IconType.service_appointment),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("timeOff_mobile")), "Time Off", "timeOff", 6, 3, null,layoutId,IconType.time_off),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("serviceOrder_mobile")), "Service Order", "serviceOrder", 6, 5, null,layoutId,IconType.service_order),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("attendance_mobile")), "Attendance", "attendance", 17, 7, null,layoutId,IconType.attendance),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("timeSheet_mobile")), "Time Sheet", "timeSheet", 17, 6, null,layoutId,IconType.time_sheet),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("trip_mobile")), "Trip", "trip", 17, 6, null,layoutId,IconType.trip)
        );
        return webTabGroups;
    }
    public Map<String, List<WebTabContext>> getGroupNameVsTabsMapForMobile(long appId,long layoutId) throws Exception {
        Map<String, List<WebTabContext>> groupNameVsTabsMap = new HashMap<>();
        for(WebTabGroupContext webTabGroup : getWebTabGroupsForMobile(appId,layoutId)){
            groupNameVsTabsMap.put(webTabGroup.getRoute(),webTabGroup.getWebTabs());
        }
        return groupNameVsTabsMap;
    }
}
