package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardReadingWidgetFilterContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DashboardFilterAction extends FacilioAction{

	private static final long serialVersionUID = 1L;
	private Long dashboardId;
	private Long dashboardTabId;
	private Long userFilterId;
	private Long targetWidgetId;
	public Long getUserFilterId() {
		return userFilterId;
	}
	public void setUserFilterId(Long userFilterId) {
		this.userFilterId = userFilterId;
	}
	public Long getTargetWidgetId() { return targetWidgetId; }
	public void setTargetWidgetId(Long targetWidgetId) {
		this.targetWidgetId = targetWidgetId;
	}
	private DashboardFilterContext dashboardFilter;
	private Map<String, List<DashboardReadingWidgetFilterContext>> readingWidgetFilterMapping;
	private List<Map<String,Object>> widgets;
	
	public List<Map<String, Object>> getWidgets() {
		return widgets;
	}
	public void setWidgets(List<Map<String, Object>> widgets) {
		this.widgets = widgets;
	}
	public  Map<String, List<DashboardReadingWidgetFilterContext>> getReadingWidgetFilterMapping() {
		return this.readingWidgetFilterMapping;
	}
	public void setReadingWidgetFilterMapping( Map<String, List<DashboardReadingWidgetFilterContext>> readingWidgetFilterMapping) {
		this.readingWidgetFilterMapping = readingWidgetFilterMapping;
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
		FacilioChain dbFilterUpdateChain=TransactionChainFactory.getAddOrUpdateDashboardFilterChain(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2));
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
	public String addOrUpdateReadingFilter() throws Exception
	{
		FacilioChain chain = TransactionChainFactory.getUpdateReadingWidgetFilterChain();
		FacilioContext context = chain.getContext();
		context.put("readingWidgetMappings",readingWidgetFilterMapping);
		context.put(ContextNames.WIDGET_ID,targetWidgetId);
		chain.execute();
		return SUCCESS;
	}
	public String getDashboardUserFilter() throws Exception {
		DashboardContext dashboard = DashboardUtil.getDashboardWithWidgets(dashboardId);
		FacilioChain getDashboardFilterChain = ReadOnlyChainFactory.getFetchDashboardUserFilterFromId();
		FacilioContext getDashboardFilterContext = getDashboardFilterChain.getContext();
		getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
		getDashboardFilterChain.execute();
		List<DashboardUserFilterContext> dashboardUserFilterContexts = dashboard.getDashboardFilter().getDashboardUserFilters().stream().filter(userFilter -> userFilter.getWidget_id() == userFilterId).collect(Collectors.toList());
		setResult("dashboardFilter",dashboardUserFilterContexts.get(0));
		return SUCCESS;
	}
	
}