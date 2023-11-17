package com.facilio.bmsconsoleV3.signup.fsmApp;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.IconType;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class FSMDefaultTabsAndTabGroups {

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
                new WebTabContext("Portfolio", "portfolio", WebTabContext.Type.PORTFOLIO, null, "{ \"type\": \"portfolio\" }", 1,null,appId),
                new WebTabContext("Territory","territory",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Territory.TERRITORY)), null, null,null,appId),
                new WebTabContext("Assets", "assets", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ASSET)), null, 4,null,appId),
                new WebTabContext("Work Order","serviceOrder",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.SERVICE_ORDER)), null, null,null,appId),
                new WebTabContext("Appointment","serviceAppointment",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)), null, null,null,appId),
                new WebTabContext("Quote", "quote", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.QUOTE)), null, 42,null,appId),
                new WebTabContext("Employees","employees",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.EMPLOYEE)), null, null,null,appId),
                new WebTabContext("Trip","trip",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.Trip.TRIP)), null, null,null,appId),
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
                new WebTabContext("Inventory Requests","inventoryrequest",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.INVENTORY_REQUEST)), null, 14,null,appId),
                new WebTabContext("Transfer Requests","transferrequest",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.TRANSFER_REQUEST)), null, 63,null,appId),
                new WebTabContext("Home","homepage",WebTabContext.Type.HOMEPAGE,null,null,null,null,appId),
                new WebTabContext("Analytics","analytics",WebTabContext.Type.ANALYTICS,null,null,null,null,appId)

        );
        for(WebTabContext webTab : tabs){
            tabsMap.put(webTab.getRoute(),webTab);
        }
        return tabsMap;
    }


    public List<WebTabGroupContext> getWebTabGroups(long appId, long layoutId) throws Exception {
        Map<String,WebTabContext> tabsMap = getWebTabs(appId);
        List<WebTabGroupContext> webTabGroups = Arrays.asList(
                new WebTabGroupContext(Arrays.asList(tabsMap.get("dispatch"),tabsMap.get("dashboard"),tabsMap.get("approval"),tabsMap.get("analytics")), "Home", "home", 1, 1, null, layoutId, IconType.home),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("portfolio"), tabsMap.get("assets")), "Portfolio", "portfolio", 1, 1, null, layoutId, IconType.portfolio),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("serviceOrder"),tabsMap.get("serviceAppointment")), "Services", "service", 6, 3, null,layoutId,IconType.service_order),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("quote")), "Financials", "financials", 6, 5, null,layoutId,IconType.invoice),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("employees"),tabsMap.get("trip"),tabsMap.get("timeSheet"),tabsMap.get("shift"),tabsMap.get("shiftplanner"),tabsMap.get("attendance"),tabsMap.get("myAttendance"),tabsMap.get("timeOff")), "Workforce", "workforce", 17, 7, null,layoutId,IconType.workforce),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("client"),tabsMap.get("clientcontact")), "Client", "client", 17, 6, null,layoutId,IconType.client),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("vendors"),tabsMap.get("vendorcontact")), "Vendor", "vendor", 17, 6, null,layoutId,IconType.vendor),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("items"),tabsMap.get("tools"),tabsMap.get("service"),tabsMap.get("storeroom"),tabsMap.get("itemtypes"),tabsMap.get("tooltypes"),tabsMap.get("inventoryrequest"),tabsMap.get("transferrequest")), "Inventory", "inventory", 17, 8, null,layoutId,IconType.inventory)
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
                //to insert invoice module
                new WebTabGroupContext(Arrays.asList(tabsMap.get("homepage"),getWebTab(appId, "serviceAppointment"),ApplicationApi.getWebTabForApplication(appId, "dashboard"),getWebTab(appId, "serviceOrder")), "Favorite", "favorite", 1, 1, null, layoutId, IconType.home),
                new WebTabGroupContext(Arrays.asList(ApplicationApi.getWebTabForApplication(appId, "myAttendance")), "My Attendance", "myAttendance", 1, 1, null, layoutId, IconType.attendance),
                new WebTabGroupContext(Arrays.asList(getWebTab(appId, "timeOff")), "Time Off", "timeOff", 1, 1, null, layoutId, IconType.time_off),
                new WebTabGroupContext(Arrays.asList(getWebTab(appId, "timeSheet")), "Time Sheet", "timeSheet", 1, 1, null, layoutId, IconType.time_sheet),
                new WebTabGroupContext(Arrays.asList(getWebTab(appId, "trip")), "Trip", "trip", 1, 1, null, layoutId, IconType.trip),
                new WebTabGroupContext(Arrays.asList(getWebTab(appId, "inventoryrequest")), "Inventory Requests", "inventoryRequests", 6, 3, null,layoutId,IconType.inventory_request),
                new WebTabGroupContext(Arrays.asList(getWebTab(appId, "quote")), "Financials", "financials", 6, 3, null,layoutId,IconType.invoice)
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
    public WebTabContext getWebTab(long appId,String route)throws Exception{
        WebTabContext webtab = ApplicationApi.getWebTabForApplication(appId, route);
        webtab.setModuleIds(getModuleIdsListFromModuleNames(Arrays.asList(route)));
        return webtab;
    }
}
