package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants.ContextNames;

public class FetchDashboardFilterCommand extends FacilioCommand implements Command {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DashboardContext dashboard = null;
		DashboardTabContext dashboardTab = null;
		
		Long dashboardId = null;
		Long dashboardTabId = null;
		
		
		if(context.get(ContextNames.DASHBOARD)!=null)
		{
			dashboard=(DashboardContext)context.get(ContextNames.DASHBOARD);
			dashboardId=dashboard.getId();
		}
		
		
		if(context.get(ContextNames.DASHBOARD_TAB)!=null)
		{
			dashboardTab=(DashboardTabContext)context.get(ContextNames.DASHBOARD_TAB);
			dashboardTabId=dashboardTab.getId();
		}
		
		
		DashboardFilterContext dashboardFilter=DashboardFilterUtil.getDashboardFilter(dashboardId, dashboardTabId);
		if(dashboardFilter!=null)
		{
			dashboardFilter.setDashboardUserFilters(DashboardFilterUtil.getDashboardUserFilters(dashboardFilter.getId()));
		}
			
		
		
		if(dashboard!=null)
		{
			dashboard.setDashboardFilter(dashboardFilter);
		}
		if(dashboardTab!=null)
		{
			dashboardTab.setDashboardFilter(dashboardFilter);
		}
		
		return false;
	}

}
