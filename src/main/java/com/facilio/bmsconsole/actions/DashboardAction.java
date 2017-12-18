package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardContext.DashboardPublishStatus;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.DashboardUtil;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.opensymphony.xwork2.ActionSupport;

public class DashboardAction extends ActionSupport {

	public DashboardContext getDashboard() {
		return dashboard;
	}
	public void setDashboard(DashboardContext dashboardContext) {
		this.dashboard = dashboardContext;
	}
	public DashboardWidgetContext getDashboardWidget() {
		return dashboardWidget;
	}
	public void setDashboardWidget(DashboardWidgetContext dashboardWidgetContext) {
		this.dashboardWidget = dashboardWidgetContext;
	}
	private DashboardContext dashboard;
	private DashboardWidgetContext dashboardWidget;
	
	private Long dashboardId;
	private int dashboardPublishStatus;
	
	public int getDashboardPublishStatus() {
		return dashboardPublishStatus;
	}
	public void setDashboardPublishStatus(int dashboardPublishStatus) {
		this.dashboardPublishStatus = dashboardPublishStatus;
	}
	public Long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(Long dashboardId) {
		this.dashboardId = dashboardId;
	}
	public String addDashboard() throws Exception {
		FacilioContext context = new FacilioContext();
		dashboard.setPublishStatus(DashboardPublishStatus.NONE.ordinal());
		dashboard.setCreatedByUserId(AccountUtil.getCurrentUser().getId());
		dashboard.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
		Chain addDashboardChain = FacilioChainFactory.getAddDashboardChain();
		addDashboardChain.execute(context);
		return SUCCESS;
	}
	public String addWidget() throws Exception {
		
		FacilioContext context = new FacilioContext();
		dashboardWidget.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		context.put(FacilioConstants.ContextNames.WIDGET, dashboardWidget);
		context.put(FacilioConstants.ContextNames.DASHBOARD_ID, dashboardId);
		Chain addWidgetChain = null;
		if(dashboardWidget.getId() != -1) {
			addWidgetChain = FacilioChainFactory.getAddDashboardVsWidgetChain();
		}
		else {
			addWidgetChain = FacilioChainFactory.getAddWidgetChain();
		}
		addWidgetChain.execute(context);
		
		return SUCCESS;
	}
	public String updateDashboardPublishStatus() throws Exception {
		dashboard = new DashboardContext();
		dashboard.setPublishStatus(dashboardPublishStatus);
		dashboard.setId(dashboardId);
		DashboardUtil.updateDashboardPublishStatus(dashboard);
		return SUCCESS;
	}
	
	public String getWidget() {
		
		return SUCCESS;
	}
	
}
