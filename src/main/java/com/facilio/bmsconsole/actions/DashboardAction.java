package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.FacilioReportContext;
import com.facilio.bmsconsole.commands.ReportsChainFactory;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardContext.DashboardPublishStatus;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.util.DashboardUtil;
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
		
//		WidgetReportContext reportContext = DashboardUtil.getReportContext(reportId);
//			
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		FacilioModule module = modBean.getModule(reportContext.getModuleId());
//				
//		FacilioField xAxisField = modBean.getField(reportContext.getXAxis());
//		FacilioModule fieldModule = xAxisField.getExtendedModule();
//		
//		FacilioField yaxisField = new FacilioField();
//		yaxisField.setName("value");
//		yaxisField.setColumnName(reportContext.getY1Axis());
//		yaxisField.setDataType(FieldType.NUMBER);
//		
//		List<FacilioField> fields = new ArrayList<>();
//		fields.add(yaxisField);
//		
//		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
//				.table(module.getTableName())
//				.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId());
//		
//		FacilioField groupByField = new FacilioField();
//		if(getPeriod() != null) {
//			Operator dateOperator = DateOperators.getAllOperators().get(getPeriod());
//			FacilioField timeSeriesfield = modBean.getField(reportContext.getTimeSeriesField());
//			String timePeriodWhereCondition = dateOperator.getWhereClause(timeSeriesfield.getColumnName(), null);
//			builder.andCustomWhere(timePeriodWhereCondition);
//			groupByField.setName("label");
//			
//			if(xAxisField.getModule().getName().equals(timeSeriesfield.getModule().getName()) && xAxisField.getColumnName().equals(timeSeriesfield.getColumnName())) {
//				groupByField.setColumnName(DashboardUtil.getTimeFrameFloorValue(dateOperator, fieldModule.getTableName()+"."+xAxisField.getColumnName()));
//			}
//			else {
//				groupByField.setColumnName(fieldModule.getTableName()+"."+xAxisField.getColumnName());
//			}
//			groupByField.setDataType(xAxisField.getDataType());
//		}
//		else {
//			groupByField.setName("label");
//			groupByField.setColumnName(fieldModule.getTableName()+"."+xAxisField.getColumnName());
//			groupByField.setDataType(xAxisField.getDataType());
//		}
//		builder.groupBy("label");
//		fields.add(groupByField);
//		builder.select(fields);
//		if(!module.getName().equals(fieldModule.getName())) {
//			builder.innerJoin(fieldModule.getTableName())
//				.on(module.getTableName()+".Id="+fieldModule.getTableName()+".Id");
//		}
//		Criteria criteria = null;
//		if(reportContext.getCriteriaId() != null) {
//			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), reportContext.getCriteriaId());
//			builder.andCriteria(criteria);
//		}
//		List<Map<String, Object>> rs = builder.get();
//		
//		if(reportContext.getIsComparisionReport()) {
//			GenericSelectRecordBuilder builder1 = new GenericSelectRecordBuilder()
//					.table(module.getTableName())
//					.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
//					.groupBy("label")
//					.select(fields);
//			if(!module.getName().equals(fieldModule.getName())) {
//				builder1.innerJoin(fieldModule.getTableName())
//					.on(module.getTableName()+".Id="+fieldModule.getTableName()+".Id");
//			}
//			if(reportContext.getCriteriaId() != null) {
//				builder1.andCriteria(criteria);
//			}
//			if(getPeriod() != null) {
//				Operator dateOperator = DateOperators.getAllOperators().get(getPeriod());
//				if (dateOperator.getOperatorId() == DateOperators.CURRENT_WEEK.getOperatorId()) {
//					dateOperator = DateOperators.LAST_WEEK;
//				}
//				FacilioField timeSeriesfield = modBean.getField(reportContext.getTimeSeriesField());
//				String timePeriodWhereCondition1 = dateOperator.getWhereClause(timeSeriesfield.getColumnName(), null);
//				builder1.andCustomWhere(timePeriodWhereCondition1);
//			}
//			System.out.println(builder1.get());
//		}
//		
// 		setReportData(rs);
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
