package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class DashboardFilterAction extends FacilioAction{

	
	private static final long serialVersionUID = 1L;
	
	private Long dashboardId;
	private Long dashboardTabId;
	private DashboardFilterContext dashboardFilter;
	
	private List<Map<String,Object>> widgets;
	
	public List<Map<String, Object>> getWidgets() {
		return widgets;
	}
	public void setWidgets(List<Map<String, Object>> widgets) {
		this.widgets = widgets;
	}
	public DashboardFilterContext getDashboardFilter() {
		return this.dashboardFilter;
	}
	public void setDashboardFilter(DashboardFilterContext dashboardFilter) {
		this.dashboardFilter = dashboardFilter;
	}
	public String getDashboardModules() throws Exception{
		FacilioChain chain=ReadOnlyChainFactory.getFetchModulesInDashboardChain();
		FacilioContext context=chain.getContext();
		context.put(FacilioConstants.ContextNames.DASHBOARD_ID,getDashboardId());
		context.put(FacilioConstants.ContextNames.DASHBOARD_TAB_ID,getDashboardTabId());
		chain.execute();
		setResult(FacilioConstants.ContextNames.MODULE_LIST, (List<String>)context.get(FacilioConstants.ContextNames.MODULE_LIST));
		return SUCCESS;
	}
	public Long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(Long dashboardId) {
		this.dashboardId = dashboardId;
	}
	public Long getDashboardTabId() {
		return dashboardTabId;
	}
	public void setDashboardTabId(Long dashboardTabId) {
		this.dashboardTabId = dashboardTabId;
	}
	
	public String addOrUpdateDashboardFilters() throws Exception
	{
		FacilioChain dbFilterUpdateChain=TransactionChainFactory.getAddOrUpdateDashboardFilterChain();
		FacilioContext context=dbFilterUpdateChain.getContext();
		DashboardFilterContext dashboardFilter=getDashboardFilter();
		context.put(ContextNames.DASHBOARD_FILTER, dashboardFilter);
		if(dashboardFilter.getDashboardId()!=-1)
		{
			context.put(ContextNames.DASHBOARD, DashboardUtil.getDashboardWithWidgets(dashboardFilter.getDashboardId()));
		}
		else if(dashboardFilter.getDashboardTabId()!=-1)
		{
			context.put(ContextNames.DASHBOARD_TAB, DashboardUtil.getDashboardTabWithWidgets(dashboardFilter.getDashboardTabId()));
		}
		dbFilterUpdateChain.execute();
		return SUCCESS;
	}
	
	public String updateWidgetSettings() throws Exception
	{
		FacilioChain chain=TransactionChainFactory.getUpdateWidgetFilterSettingsChain();
		FacilioContext context=chain.getContext();
		context.put(FacilioConstants.ContextNames.WIDGET_UPDATE_LIST, getWidgets());
		chain.execute();
		setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));

		
		return SUCCESS;
	}
	
}