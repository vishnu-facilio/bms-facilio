package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetDashboardTabListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
        List<DashboardTabContext> DashboardTabs = DashboardUtil.getDashboardTabs(dashboard.getId());
        List<Map<String,Object>> tabsList = new ArrayList<>();
        for(DashboardTabContext dashboardTabContext :DashboardTabs){
            Map<String,Object> tab = new HashMap<>();
            tab.put("name",dashboardTabContext.getName());
            tab.put("tabId",dashboardTabContext.getId());

            if(dashboardTabContext.getChildTabs() != null){
                List<Map<String,Object>> childTab = new ArrayList<>();
                for(DashboardTabContext tabs: dashboardTabContext.getChildTabs()){
                    Map<String,Object> childs = new HashMap<>();
                    childs.put("name",tabs.getName());
                    childs.put("tabId",tabs.getId());
                    childTab.add(childs);
                }
                tab.put("child_tabs",childTab);
            }
            tabsList.add(tab);
        }
        context.put("tabsList",tabsList);
        return false;
    }
    }
