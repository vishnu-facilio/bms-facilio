package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.bmsconsoleV3.context.dashboard.WidgetDashboardFilterContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;

public class AddOrUpdateNewDashboardUserFilterCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        DashboardFilterContext dashboardFilterContext=context.get(FacilioConstants.ContextNames.DASHBOARD_FILTER)!=null?(DashboardFilterContext)context.get(FacilioConstants.ContextNames.DASHBOARD_FILTER):null;

        if(dashboardFilterContext!=null)
        {
            List<DashboardUserFilterContext> currentDashboardUserFilters=dashboardFilterContext.getDashboardUserFilters();
            if(currentDashboardUserFilters!=null)
            {

                for(DashboardUserFilterContext dashboardUserFilterRel:currentDashboardUserFilters)
                {
                    dashboardUserFilterRel.setDashboardFilterId(dashboardFilterContext.getId());
                    if(dashboardUserFilterRel.getCriteria()!=null)
                    {
                        long criteriaId=CriteriaAPI.addCriteria(dashboardUserFilterRel.getCriteria());
                        dashboardUserFilterRel.setCriteriaId(criteriaId);
                    }

                    if(dashboardUserFilterRel.getId()>0)
                    {
                        DashboardFilterUtil.updateDashboardUserFilerRel(dashboardUserFilterRel);
                    }
                    else {
                        WidgetDashboardFilterContext filter_widget = new WidgetDashboardFilterContext();
                        Long widget_id = (Long) context.get(FacilioConstants.ContextNames.WIDGET_ID);
                        if(widget_id != null && widget_id > 0) {
                            dashboardUserFilterRel.setWidget_id(widget_id);
                            dashboardUserFilterRel.setId(DashboardFilterUtil.insertDashboardUserFilterRel(dashboardUserFilterRel));
                        }else{
                            dashboardUserFilterRel.setId(DashboardFilterUtil.insertDashboardUserFilterRel(dashboardUserFilterRel));
                        }
                    }
                }

            }
        }
        return false;
    }
}
