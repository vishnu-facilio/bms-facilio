package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.bmsconsole.commands.AddOrUpdateDashboardFilterCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrUpdateDashboardAndTabCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
        if(dashboard!=null){
            List<DashboardTabContext> dashboardTabs = dashboard.getDashboardTabContexts();

            Map<String,Long> filterNameVsIds =  new HashMap<>();
            Map<String,Long> tabNameVsIds = new HashMap<>();
            if(context.get(PackageConstants.DashboardConstants.FILTER_MAP) !=null){
               filterNameVsIds = (Map<String, Long>) context.get(PackageConstants.DashboardConstants.FILTER_MAP);
            }
            if(context.get(PackageConstants.DashboardConstants.TAB_MAP)!=null){
                tabNameVsIds = (Map<String, Long>) context.get(PackageConstants.DashboardConstants.TAB_MAP);
            }
            Long dashboardId = dashboard.getId();
            if(dashboardId!=null && dashboardId>0){
                DashboardUtil.updateDashboard(dashboard);
                DashboardFilterContext filterContext = dashboard.getDashboardFilter();
                if(filterContext!=null){
                    filterContext.setDashboardId(dashboardId);
                    addOrUpdateTimeLineFilter(filterContext,filterNameVsIds);
                }
            }
            else{
                DashboardUtil.addDashboard(dashboard);
                DashboardFilterContext filterContext = dashboard.getDashboardFilter();
                if(filterContext!=null) {
                    filterContext.setDashboardId(dashboard.getId());
                    addOrUpdateTimeLineFilter(filterContext,filterNameVsIds);
                }
            }
            if(dashboardTabs !=null){
                for (DashboardTabContext tab : dashboardTabs){
                    tab.setDashboardId(dashboard.getId());
                    Long tabId = tabNameVsIds.get(tab.getLinkName());
                    if(tabId!=null && tabId>0){
                        DashboardUtil.updateDashboardTab(tab);
                        DashboardFilterContext filterContext = tab.getDashboardFilter();
                        if(filterContext!=null){
                            filterContext.setDashboardTabId(tabId);
                            addOrUpdateTimeLineFilter(filterContext,filterNameVsIds);
                        }
                    }
                    else{
                        FacilioChain addDashboardTabChain = TransactionChainFactory.getAddDashboardTabChain();
                        FacilioContext tabContext = addDashboardTabChain.getContext();
                        tabContext.put(FacilioConstants.ContextNames.DASHBOARD_TAB, tab);
                        addDashboardTabChain.execute();
                        DashboardTabContext dashboardTab = (DashboardTabContext) tabContext.get(FacilioConstants.ContextNames.DASHBOARD_TAB);
                        DashboardFilterContext filterContext = tab.getDashboardFilter();
                        if(filterContext!=null){
                            filterContext.setDashboardTabId(dashboardTab.getId());
                            Long id = AddOrUpdateDashboardFilterCommand.addDashboardFilter(filterContext);
                            filterContext.setId(id);
                        }
                    }
                }
            }
            context.put(FacilioConstants.ContextNames.DASHBOARD,dashboard);
        }
        return false;
    }
    public static void addOrUpdateTimeLineFilter(DashboardFilterContext filterContext,Map<String,Long> filterNameVsIds) throws Exception {
        Long filterId = filterNameVsIds.get(filterContext.getLinkName());
        if (filterId !=null && filterId>0) {
            AddOrUpdateDashboardFilterCommand.updateDashboardFilter(filterContext);
        }
        else{
            Long id = AddOrUpdateDashboardFilterCommand.addDashboardFilter(filterContext);
            filterContext.setId(id);
        }
    }
}
