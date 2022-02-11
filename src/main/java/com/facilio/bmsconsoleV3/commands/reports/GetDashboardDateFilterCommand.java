package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;

public class GetDashboardDateFilterCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context)throws Exception{
        long dashboardId = (long) context.get("dashboardId");
        DateRange range = DashboardUtil.getDateFilterFromDashboard(dashboardId);
        if(range != null){
            context.put("range", range);
        }
        return false;
    }
}
