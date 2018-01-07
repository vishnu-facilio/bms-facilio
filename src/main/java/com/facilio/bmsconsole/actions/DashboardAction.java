package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardContext.DashboardPublishStatus;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetListViewContext;
import com.facilio.bmsconsole.context.WidgetPeriodContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.opensymphony.xwork2.ActionSupport;

public class DashboardAction extends ActionSupport {

	public String xaxisLegent;
	public String getXaxisLegent() {
		return xaxisLegent;
	}
	public void setXaxisLegent(String xaxisLegent) {
		this.xaxisLegent = xaxisLegent;
	}
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
	public WidgetChartContext getWidgetChartContext() {
		return widgetChartContext;
	}
	public void setWidgetChartContext(WidgetChartContext widgetChartContext) {
		this.widgetChartContext = widgetChartContext;
	}
	public WidgetListViewContext getWidgetListViewContext() {
		return widgetListViewContext;
	}
	public void setWidgetListViewContext(WidgetListViewContext widgetListViewContext) {
		this.widgetListViewContext = widgetListViewContext;
	}
	private List<DashboardContext> dashboards;
	private DashboardWidgetContext dashboardWidget;
	private WidgetChartContext widgetChartContext;
	private WidgetListViewContext widgetListViewContext; 
	
	public List<DashboardContext> getDashboards() {
		return dashboards;
	}
	public void setDashboards(List<DashboardContext> dashboards) {
		this.dashboards = dashboards;
	}
	
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
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private List<Map<String, Object>> reportData;
	public List<Map<String, Object>> getReportData() {
		return this.reportData;
	}
	
	public void setReportData(List<Map<String, Object>> reportData) {
		this.reportData = reportData;
	}
	Long reportId;
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	String period;
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getData() throws Exception {
		
		//System.out.println(DashboardUtil.getFormulaValue(1l));
		WidgetChartContext widgetChartContext = DashboardUtil.getWidgetChartContext(reportId);
			
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(widgetChartContext.getModuleId());
				
		FacilioField xAxisField = modBean.getField(widgetChartContext.getxAxis());
		FacilioModule fieldModule = xAxisField.getExtendedModule();
		
		FacilioField yaxisField = new FacilioField();
		yaxisField.setName("value");
		yaxisField.setColumnName(widgetChartContext.getY1Axis());
		yaxisField.setDataType(FieldType.NUMBER);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(yaxisField);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId());
		
		FacilioField groupByField = new FacilioField();
		if(getPeriod() != null) {
			Operator dateOperator = DateOperators.getAllOperators().get(getPeriod());
			WidgetPeriodContext widgetperiodContext = DashboardUtil.getWidgetPeriod(widgetChartContext.getId(), getPeriod());
			FacilioField timeSeriesfield = modBean.getField(widgetperiodContext.getTimeSeriesField());
			String timePeriodWhereCondition = dateOperator.getWhereClause(timeSeriesfield.getColumnName(), null);
			builder.andCustomWhere(timePeriodWhereCondition);
			groupByField.setName("label");
			
			if(xAxisField.getModule().getName().equals(timeSeriesfield.getModule().getName()) && xAxisField.getColumnName().equals(timeSeriesfield.getColumnName())) {
				groupByField.setColumnName(DashboardUtil.getTimeFrameFloorValue(dateOperator, fieldModule.getTableName()+"."+xAxisField.getColumnName()));
			}
			else {
				groupByField.setColumnName(fieldModule.getTableName()+"."+xAxisField.getColumnName());
			}
			groupByField.setDataType(xAxisField.getDataType());
		}
		else {
			groupByField.setName("label");
			groupByField.setColumnName(fieldModule.getTableName()+"."+xAxisField.getColumnName());
			groupByField.setDataType(xAxisField.getDataType());
		}
		builder.groupBy("label");
		fields.add(groupByField);
		builder.select(fields);
		if(!module.getName().equals(fieldModule.getName())) {
			builder.innerJoin(fieldModule.getTableName())
				.on(module.getTableName()+".Id="+fieldModule.getTableName()+".Id");
		}
		Criteria criteria = null;
		if(widgetChartContext.getWidgetConditions() != null) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), widgetChartContext.getWidgetConditions().get(0).getCriteriaId());
			builder.andCriteria(criteria);
		}
		List<Map<String, Object>> rs = builder.get();
		
		for(int i=0;i<rs.size();i++) {
 			Map<String, Object> thisMap = rs.get(i);
 			if(thisMap!=null) {
 				if(thisMap.get("label") == null) {
 					thisMap.put("label", null);
 				}
 			}
	 	}
		System.out.println("rs after -- "+rs);
		
		if(true) {
			setXaxisLegent(widgetChartContext.getxAxisLegend());
		}
//		if(widgetChartContext.getIsComparisionReport()) {
//			GenericSelectRecordBuilder builder1 = new GenericSelectRecordBuilder()
//					.table(module.getTableName())
//					.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
//					.groupBy("label")
//					.select(fields);
//			if(!module.getName().equals(fieldModule.getName())) {
//				builder1.innerJoin(fieldModule.getTableName())
//					.on(module.getTableName()+".Id="+fieldModule.getTableName()+".Id");
//			}
//			if(widgetChartContext.getCriteriaId() != null) {
//				builder1.andCriteria(criteria);
//			}
//			if(getPeriod() != null) {
//				Operator dateOperator = DateOperators.getAllOperators().get(getPeriod());
//				if (dateOperator.getOperatorId() == DateOperators.CURRENT_WEEK.getOperatorId()) {
//					dateOperator = DateOperators.LAST_WEEK;
//				}
//				WidgetPeriodContext widgetperiodContext = DashboardUtil.getWidgetPeriod(widgetChartContext.getId(), dateOperator.getOperator());
//				FacilioField timeSeriesfield = modBean.getField(widgetperiodContext.getTimeSeriesField());
//				String timePeriodWhereCondition1 = dateOperator.getWhereClause(timeSeriesfield.getColumnName(), null);
//				builder1.andCustomWhere(timePeriodWhereCondition1);
//			}
//			System.out.println(builder1.get());
//		}
		
 		setReportData(rs);
		return SUCCESS;
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
		
		if(widgetChartContext != null) {
			context.put(FacilioConstants.ContextNames.WIDGET, widgetChartContext);
			context.put(FacilioConstants.ContextNames.WIDGET_TYPE, DashboardWidgetContext.WidgetType.CHART);
			context.put(FacilioConstants.ContextNames.DASHBOARD_ID, widgetChartContext.getDashboardId());
			widgetChartContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		}
		else if (widgetListViewContext != null) {
			context.put(FacilioConstants.ContextNames.WIDGET, widgetListViewContext);
			context.put(FacilioConstants.ContextNames.WIDGET_TYPE, DashboardWidgetContext.WidgetType.LIST_VIEW);
			context.put(FacilioConstants.ContextNames.DASHBOARD_ID, widgetListViewContext.getDashboardId());
			widgetListViewContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		}
		
		Chain addWidgetChain = null;
		if(dashboardWidget != null && dashboardWidget.getId() != -1) {
			addWidgetChain = FacilioChainFactory.getAddDashboardVsWidgetChain();
		}
		else {
			addWidgetChain = FacilioChainFactory.getAddWidgetChain();
		}
		addWidgetChain.execute(context);
		
		return SUCCESS;
	}
	
	public String getDashboardList() throws Exception {
		if (moduleName != null) {
			dashboards = DashboardUtil.getDashboardList(moduleName);
		}
		return SUCCESS;
	}
	
	public String viewDashboard() throws Exception {
		dashboard = DashboardUtil.getDashboardWithWidgets(linkName);
		setDashboardJson(DashboardUtil.getDashboardResponseJson(dashboard));
		return SUCCESS;
	}
	
	public String updateDashboardPublishStatus() throws Exception {
		dashboard = new DashboardContext();
		dashboard.setPublishStatus(dashboardPublishStatus);
		dashboard.setId(dashboardId);
		DashboardUtil.updateDashboardPublishStatus(dashboard);
		return SUCCESS;
	}
	
	private JSONArray dashboardJson;
	
	public void setDashboardJson(JSONArray dashboardJson) {
		this.dashboardJson = dashboardJson;
	}
	public JSONArray getDashboardJson() {
		return dashboardJson;
	}
	public String getWidget() {
		
		return SUCCESS;
	}
	
	private String linkName;

	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
}
