package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class GetDashboardDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        Long dashboardId = (Long) context.get("id");
        if(dashboardId != null && dashboardId > 0)
        {
            DashboardContext dashoard = DashboardUtil.getDashboardWithWidgets(dashboardId);
            if(dashoard != null) {
                context.put("dashboard", dashoard);
            }
        }
        return false;
    }
}
