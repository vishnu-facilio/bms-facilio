package com.facilio.bmsconsole.actions;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReportsChainFactory;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardContext.DashboardPublishStatus;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.EnergyPurposeAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.NumberAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.SpaceAggregateOperator;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.ReportColumnContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportContext.ReportChartType;
import com.facilio.bmsconsole.context.ReportDateFilterContext;
import com.facilio.bmsconsole.context.ReportEnergyMeterContext;
import com.facilio.bmsconsole.context.ReportFieldContext;
import com.facilio.bmsconsole.context.ReportFolderContext;
import com.facilio.bmsconsole.context.ReportSpaceFilterContext;
import com.facilio.bmsconsole.context.ReportThreshold;
import com.facilio.bmsconsole.context.ReportUserFilterContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetListViewContext;
import com.facilio.bmsconsole.context.WidgetStaticContext;
import com.facilio.bmsconsole.context.WidgetWebContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.pdf.PdfUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.workflows.util.WorkflowUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.opensymphony.xwork2.ActionSupport;

public class DashboardAction extends ActionSupport {

	Long baseLineComparisionDiff;
	
	Long reportEntityId;
	
	public Long getReportEntityId() {
		return reportEntityId;
	}
	public void setReportEntityId(Long reportEntityId) {
		this.reportEntityId = reportEntityId;
	}
	public Long getBaseLineComparisionDiff() {
		return baseLineComparisionDiff;
	}
	public void setBaseLineComparisionDiff(Long baseLineComparisionDiff) {
		this.baseLineComparisionDiff = baseLineComparisionDiff;
	}
	boolean isHeatMap;
	public boolean getIsHeatMap() {
		return isHeatMap;
	}
	String reportName;
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public void setIsHeatMap(boolean isHeatMap) {
		this.isHeatMap = isHeatMap;
	}
	public JSONObject resultVariance;
	
	public JSONObject getResultVariance() {
		return resultVariance;
	}
	public void setResultVariance(JSONObject resultVariance) {
		this.resultVariance = resultVariance;
	}
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
	private WidgetStaticContext widgetStaticContext;
	private WidgetWebContext widgetWebContext;
	
	public WidgetWebContext getWidgetWebContext() {
		return widgetWebContext;
	}
	public void setWidgetWebContext(WidgetWebContext widgetWebContext) {
		this.widgetWebContext = widgetWebContext;
	}
	
	public WidgetStaticContext getWidgetStaticContext() {
		return widgetStaticContext;
	}
	public void setWidgetStaticContext(WidgetStaticContext widgetStaticContext) {
		this.widgetStaticContext = widgetStaticContext;
	}
	
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
	
	private JSONArray reportData;
	public JSONArray getReportData() {
		return this.reportData;
	}
	
	public void setReportData(JSONArray reportData) {
		this.reportData = reportData;
	}
	private JSONArray relatedAlarms;
	
	public JSONArray getRelatedAlarms() {
		return relatedAlarms;
	}
	public void setRelatedAlarms(JSONArray releatedAlarms) {
		this.relatedAlarms = releatedAlarms;
	}
	Long reportId;
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	long baseLineId = -1;
	public long getBaseLineId() {
		return baseLineId;
	}
	public void setBaseLineId(long baseLineId) {
		this.baseLineId = baseLineId;
	}
	
	long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	private JSONArray dateFilter;
	public JSONArray getDateFilter() {
		return dateFilter;
	}
	public void setDateFilter(JSONArray dateFilter) {
		this.dateFilter = dateFilter;
	}
	ReportSpaceFilterContext reportSpaceFilterContext;
	
	public ReportSpaceFilterContext getReportSpaceFilterContext() {
		return reportSpaceFilterContext;
	}
	public void setReportSpaceFilterContext(ReportSpaceFilterContext reportSpaceFilterContext) {
		this.reportSpaceFilterContext = reportSpaceFilterContext;
	}
	ReportEnergyMeterContext energyMeterFilter;
	public ReportEnergyMeterContext getEnergyMeterFilter() {
		return energyMeterFilter;
	}
	
	public void setEnergyMeterFilter(ReportEnergyMeterContext energyMeterFilter) {
		this.energyMeterFilter = energyMeterFilter;
	}
	
	String period;
	public String getPeriod() {
		return period;
	}
	public ReportFolderContext getReportFolderContext() {
		return reportFolderContext;
	}
	public void setReportFolderContext(ReportFolderContext reportFolderContext) {
		this.reportFolderContext = reportFolderContext;
	}
	public ReportContext getReportContext() {
		return reportContext;
	}
	public void setReportContext(ReportContext reportContext) {
		this.reportContext = reportContext;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	private ReportFolderContext reportFolderContext;
	private ReportContext reportContext;
	public ReportThreshold getReportThreshold() {
		return reportThreshold;
	}
	public void setReportThreshold(ReportThreshold reportThreshold) {
		this.reportThreshold = reportThreshold;
	}
	private JSONObject userFilterValues;
	
	public JSONObject getUserFilterValues() {
		return userFilterValues;
	}
	public void setUserFilterValues(JSONObject userFilterValues) {
		this.userFilterValues = userFilterValues;
	}
	private ReportThreshold reportThreshold;
	
	public String addThreshold() throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReportThreshold().getTableName())
				.fields(FieldFactory.getReportThresholdFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(reportThreshold);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		return SUCCESS;
	}

	public String addReport() throws Exception {
		
		if ((reportContext.getParentFolderId() == null || reportContext.getParentFolderId() < 0) && reportContext.getReportEntityId() == null) {
			// if report parent folder not exists, mapping to default folder 
			ReportFolderContext defaultFolder = DashboardUtil.getDefaultReportFolder(moduleName);
			reportContext.setParentFolderId(defaultFolder.getId());
		}
		if(reportContext.getModuleId() == -1) {
			reportContext.setModuleName(moduleName);
		}
		DashboardUtil.addReport(reportContext);
		
		reportContext = DashboardUtil.getReportContext(reportContext.getId());
		
		return SUCCESS;
	}
	
	private List<ReportContext> reports;
	public List<ReportContext> getReports() {
		return reports;
	}
	public void setReports(List<ReportContext> reports) {
		this.reports = reports;
	}
	
	public String addTabularReport() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.REPORT_LIST, reports);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		Chain addTabularReportChain = FacilioChainFactory.addTabularReportChain();
		addTabularReportChain.execute(context);
		reportContext = DashboardUtil.getReportContext((Long) context.get(FacilioConstants.ContextNames.RECORD_ID));
		
		return SUCCESS;
	}
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}
	
	private String result;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public String updateSequence() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.REPORT_COLUMN_LIST, id);
		
		Chain updateSequence = FacilioChainFactory.updateReportColumnSequence();
		updateSequence.execute(context);
		
		result = (String) context.get(FacilioConstants.ContextNames.RESULT);
		
		return SUCCESS;
	}
	
	public String addReportFolder() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		reportFolderContext.setModuleId(module.getModuleId());
		
		DashboardUtil.addReportFolder(reportFolderContext);
		
		return SUCCESS;
	}
	
	private String chartType;
	
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	
	public String getChartType() {
		return this.chartType;
	}
	
	private String secChartType;
	
	public void setSecChartType(String secChartType) {
		this.secChartType = secChartType;
	}
	
	public String getSecChartType() {
		return this.secChartType;
	}
	
	public String updateChartType() throws Exception {
		
		if (reportId > 0) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getReport().getTableName())
					.fields(FieldFactory.getReportFields())
					.andCustomWhere("ID = ?", reportId);

			Map<String, Object> props = new HashMap<String, Object>();
			props.put("chartType", ReportContext.ReportChartType.getWidgetChartType(chartType).getValue());
			if (secChartType != null) {
				props.put("secChartType", ReportContext.ReportChartType.getWidgetChartType(secChartType).getValue());
			}
			
			updateBuilder.update(props);
		}
		
		return SUCCESS;
	}
	
	public String getRelatedAlarmsList() throws Exception {
		if (reportContext == null) {
			reportContext = DashboardUtil.getReportContext(reportId);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = reportModule;
		if (module == null) {
			module = modBean.getModule(reportContext.getModuleId());
		}
		
//		FacilioContext context = new FacilioContext();
//		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
//		context.put(FacilioConstants.ContextNames.MODULE, module);
//		context.put(FacilioConstants.ContextNames.REPORT_DATE_FILTER, dateFilter);
//		Chain getRelatedAlarms = FacilioChainFactory.getRelatedAlarmForReports();
//		getRelatedAlarms.execute(context);
		return SUCCESS;
	}
	
	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}
	
	private String xLabel;
	public String getxLabel() {
		return xLabel;
	}
	public void setxLabel(String xLabel) {
		this.xLabel = xLabel;
	}
	
	private String yLabel;
	public String getyLabel() {
		return yLabel;
	}
	public void setyLabel(String yLabel) {
		this.yLabel = yLabel;
	}
	
	private String yUnit;
	public String getyUnit() {
		return yUnit;
	}
	public void setyUnit(String yUnit) {
		this.yUnit = yUnit;
	}
	
	private int xAggr = -1;
	public int getxAggr() {
		return xAggr;
	}
	public void setxAggr(int xAggr) {
		this.xAggr = xAggr;
	}
	
	private int yAggr = -1;
	public int getyAggr() {
		return yAggr;
	}
	public void setyAggr(int yAggr) {
		this.yAggr = yAggr;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private FacilioModule reportModule;
	public FacilioModule getReportModule() {
		return reportModule;
	}
	public void setReportModule(FacilioModule reportModule) {
		this.reportModule = reportModule;
	}
	
	public String getReadingReportData() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField readingField = modBean.getFieldFromDB(readingFieldId);
		FacilioModule module = readingField.getModule();
		reportContext = constructReportObjectForReadingReport(module, readingField);
		reportModule = module;
		reportData = getDataForReadings(reportContext, module, dateFilter, null, baseLineId, -1);
		return SUCCESS;
	}
	
	public String addComparisionReport() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField readingField = modBean.getFieldFromDB(readingFieldId);
		FacilioModule module = readingField.getModule();
		ReportContext readingReport = constructReportObjectForReadingReport(module, readingField);
		this.reportContext = readingReport;
		return addReport();
	}
	
	private ReportContext constructReportObjectForReadingReport(FacilioModule module, FacilioField readingField) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		ReportContext readingReport = new ReportContext();
		
		readingReport.setModuleId(module.getModuleId());
		readingReport.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		ReportFieldContext xAxis = new ReportFieldContext();
		xAxis.setModuleField(fieldMap.get("ttime"));
		xAxis.setModuleFieldId(xAxis.getModuleField().getId());
		if(xLabel != null && !xLabel.isEmpty()) {
			xAxis.setFieldLabel(xLabel);
		}
		else {
			xAxis.setFieldLabel("Time");
		}
		xAxis.setIsFormulaField(false);
		readingReport.setxAxisField(xAxis);
		readingReport.setxAxisLabel(xAxis.getFieldLabel());
		if(xAggr != -1) {
			readingReport.setxAxisaggregateFunction(xAggr);
		}
		
		ReportFieldContext yAxis = new ReportFieldContext();
		yAxis.setModuleField(readingField);
		yAxis.setModuleFieldId(yAxis.getModuleField().getId());
		if(yLabel != null && !yLabel.isEmpty()) {
			yAxis.setFieldLabel(yLabel);
		}
		else {
			yAxis.setFieldLabel(readingField.getDisplayName());
		}
		if (yUnit != null && !yUnit.isEmpty()) {
			yAxis.setUnit(yUnit);
		}
		else if (readingField instanceof NumberField) {
			yAxis.setUnit(((NumberField) readingField).getUnit());
		}
		yAxis.setIsFormulaField(false);
		readingReport.setY1AxisField(yAxis);
		readingReport.setY1AxisLabel(yAxis.getFieldLabel());
		readingReport.setY1AxisUnit(yAxis.getUnit());
		if(yAggr != -1) {
			readingReport.setY1AxisaggregateFunction(yAggr);
		}
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), PickListOperators.IS));
		readingReport.setCriteria(criteria);
		
		ReportDateFilterContext dateFilter = new ReportDateFilterContext();
		dateFilter.setField(fieldMap.get("ttime"));
		dateFilter.setFieldId(dateFilter.getField().getId());
		dateFilter.setOperatorId(DateOperators.TODAY_UPTO_NOW.getOperatorId());
		readingReport.setDateFilter(dateFilter);
		
		if(reportEntityId != null) {
			readingReport.setReportEntityId(reportEntityId);
		}
		
		if(reportName != null) {
			readingReport.setName(reportName);
		}
		
		return readingReport;
	}
	
	private List<ReportColumnContext> reportColumns;
	public List<ReportColumnContext> getReportColumns() {
		return reportColumns;
	}
	public void setReportColumns(List<ReportColumnContext> reportColumns) {
		this.reportColumns = reportColumns;
	}
	Long widgetId;
	public Long getWidgetId() {
		return widgetId;
	}
	public void setWidgetId(Long widgetId) {
		this.widgetId = widgetId;
	}
	Object cardResult;
	public Object getCardResult() {
		return cardResult;
	}
	public void setCardResult(Object cardResult) {
		this.cardResult = cardResult;
	}
	public String getCardData() throws Exception {
		if(widgetId != null) {
			WidgetStaticContext widgetStaticContext = (WidgetStaticContext) DashboardUtil.getWidget(widgetId);
			;
			
			Map<String,Object> paramMap = new HashMap<>();
			paramMap.put("parentId", widgetStaticContext.getBaseSpaceId());
			cardResult = WorkflowUtil.getResult(widgetStaticContext.getWorkflowId(), paramMap);
		}
		return SUCCESS;
	}
	public String getTabularData() throws Exception {
		reportContext = DashboardUtil.getReportContext(reportId);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		reportColumns = reportContext.getReportColumns();
		reportContext.setReportColumns(null);
		for (ReportColumnContext column : reportColumns) {
			if (column.getReportId() == reportContext.getId()) {
				column.setReport(reportContext);
			}
			else {
				column.setReport(DashboardUtil.getReportContext(column.getReportId()));
			}
			FacilioModule module = modBean.getModule(column.getReport().getModuleId());
			
			if(module.getName().equals("workorder")) {
				column.setData(getDataForTickets(column.getReport(), module, dateFilter, null, column.getBaseLineId(), -1, null));
			}
			else {
				column.setData(getDataForReadings(column.getReport(), module, dateFilter, null, column.getBaseLineId(), -1));
			}
		}
		Multimap<String, Object> resultMap = ArrayListMultimap.create();
		Map<String,Long> dateMap = new HashMap<>();
		JSONArray collumns = new JSONArray();
		JSONArray dataJsonArray = new JSONArray();
		
		if(reportColumns.get(0).getReport().getxAxisaggregateFunction() != null) {
			
			JSONObject columnJson = new JSONObject();
			columnJson.put("label", "Month");
			columnJson.put("width", null);
			columnJson.put("order", "1");
			
			collumns.add(columnJson);
		}
		
		for (ReportColumnContext column : reportColumns) {
			
			JSONObject columnJson = new JSONObject();
			columnJson.put("label", column.getReport().getName());
			columnJson.put("width", null);
			columnJson.put("order", column.getSequence()+1);
			
			collumns.add(columnJson);
			
			JSONArray datas = column.getData();
			Iterator dataIterator = datas.iterator();
			while(dataIterator.hasNext()) {
				JSONObject data = (JSONObject) dataIterator.next();
				Long time = (Long) data.get("label");
				String value = DashboardUtil.getDataFromValue(time, column.getReport().getXAxisAggregateOpperator());
				dateMap.put(value, time);
				resultMap.put(value, data.get("value"));
			}
		}
		
		for(String key:resultMap.keySet()) {
			Collection<Object> d = resultMap.get(key);
			JSONArray data = new JSONArray();
			data.add(dateMap.get(key));
			for(Object s:d) {
				data.add(s);
			}
			dataJsonArray.add(data);
		}
		
		System.out.println("dateMap -- "+collumns);
		System.out.println("datas --- "+dataJsonArray);
		
		reportData = new JSONArray();
		
		JSONObject result = new JSONObject();
		result.put("columns", collumns);
		result.put("data", dataJsonArray);
		
		reportData.add(result);
		
		return SUCCESS;
	}
	
	public String getData() throws Exception {
		
		if (reportContext == null) {
			reportContext = DashboardUtil.getReportContext(reportId);
			// generate preview report
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(reportContext.getModuleId());
		
		if(reportSpaceFilterContext != null) {
			reportContext.setReportSpaceFilterContext(reportSpaceFilterContext);
		}
		else if(buildingId != null) {
			reportSpaceFilterContext = new ReportSpaceFilterContext();
			reportSpaceFilterContext.setBuildingId(buildingId);
			reportContext.setReportSpaceFilterContext(reportSpaceFilterContext);
		}
		if(module.getName().equals("workorder") || module.getName().equals("alarm")) {
			reportData = getDataForTickets(reportContext, module, dateFilter, userFilterValues, baseLineId, criteriaId, energyMeterFilter);
		}
		else {
			if(energyMeterFilter != null && (energyMeterFilter.getGroupBy() != null || energyMeterFilter.getSubMeterId() != null || energyMeterFilter.getServiceId() != null)) {
				reportContext.setEnergyMeter(energyMeterFilter);
			}
			reportData = getDataForReadings(reportContext, module, dateFilter, userFilterValues, baseLineId, criteriaId);
		}
		return SUCCESS;
	}
	
	private String[] meterIds;
	
	public void setMeterIds(String[] meterIds) {
		this.meterIds = meterIds;
	}
	
	public String[] getMeterIds() {
		return this.meterIds;
	}
	
	private boolean excludeViolatedReadings = false;
	
	public void setExcludeViolatedReadings(boolean excludeViolatedReadings) {
		this.excludeViolatedReadings = excludeViolatedReadings;
	}
	
	public boolean getExcludeViolatedReadings() {
		return this.excludeViolatedReadings;
	}
	
	private JSONArray getDataForTickets(ReportContext report, FacilioModule module, JSONArray dateFilter, JSONObject userFilterValues, long baseLineId, long criteriaId, ReportEnergyMeterContext energyMeterFilter) throws Exception {
		JSONArray ticketData = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				;
		
		if(module.getExtendModule() != null) {
			builder.innerJoin(module.getExtendModule().getTableName())
				.on(module.getTableName()+".Id="+module.getExtendModule().getTableName()+".Id");
		}
		
		ReportFieldContext reportXAxisField = DashboardUtil.getReportField(report.getxAxisField());
		report.setxAxisField(reportXAxisField);
		FacilioField xAxisField = reportXAxisField.getField();
		
		List<FacilioField> fields = new ArrayList<>();
		if(xAxisField.getDataTypeEnum().equals(FieldType.DATE_TIME)) {
			FacilioField dummyField = new FacilioField();
			dummyField.setColumnName(xAxisField.getColumnName());
			if (report.getXAxisAggregateOpperator() == FormulaContext.DateAggregateOperator.ACTUAL) {
				dummyField = DateAggregateOperator.ACTUAL.getSelectField(dummyField);
			}
			else {
				dummyField = NumberAggregateOperator.MAX.getSelectField(dummyField);
			}
			dummyField.setName("dummyField");
			fields.add(dummyField);
		}
		
		FacilioField y1AxisField = null;
		ReportFieldContext reportY1AxisField;
		AggregateOperator xAggregateOpperator = report.getXAxisAggregateOpperator();
		if(!xAggregateOpperator.getValue().equals(NumberAggregateOperator.COUNT.getValue())) {
			if ((dateFilter != null || report.getDateFilter() != null) && xAxisField.getDataTypeEnum().equals(FieldType.DATE_TIME)) {
				
				int oprId =  dateFilter != null ? DashboardUtil.predictDateOpperator(dateFilter) : report.getDateFilter().getOperatorId();
				if(getIsHeatMap() || (reportContext.getChartType() != null && reportContext.getChartType().equals(ReportChartType.HEATMAP.getValue())) ) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.HOURSOFDAYONLY;
					report.setChartType(ReportChartType.HEATMAP.getValue());
				}
				else if (oprId == DateOperators.TODAY.getOperatorId() || oprId == DateOperators.YESTERDAY.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.HOURSOFDAY;
				}
				else if (oprId == DateOperators.CURRENT_WEEK.getOperatorId() || oprId == DateOperators.LAST_WEEK.getOperatorId() || oprId == DateOperators.CURRENT_WEEK_UPTO_NOW.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.FULLDATE;
					if(report.getIsComparisionReport()) {
						xAggregateOpperator = FormulaContext.DateAggregateOperator.WEEKDAY;
					}
				}
				else if (oprId == DateOperators.CURRENT_MONTH.getOperatorId() || oprId == DateOperators.LAST_MONTH.getOperatorId() || oprId == DateOperators.CURRENT_MONTH_UPTO_NOW.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.FULLDATE;
					if(report.getIsComparisionReport()) {
						xAggregateOpperator = FormulaContext.DateAggregateOperator.DAYSOFMONTH;
					}
				}
				else if (oprId == DateOperators.CURRENT_YEAR.getOperatorId() || oprId == DateOperators.LAST_YEAR.getOperatorId() || oprId == DateOperators.CURRENT_YEAR_UPTO_NOW.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.MONTHANDYEAR;
				}
				report.setxAxisaggregateFunction(xAggregateOpperator.getValue());
			}
			if (getIsHeatMap() || (reportContext.getChartType() != null && reportContext.getChartType().equals(ReportChartType.HEATMAP.getValue())) || !report.getIsHighResolutionReport()) {
				
				xAxisField = xAggregateOpperator.getSelectField(xAxisField);
				
				if(xAggregateOpperator instanceof SpaceAggregateOperator) {
					
					FacilioModule baseSpaceModule = modBean.getModule("basespace");
					
					builder.innerJoin(baseSpaceModule.getTableName())
					.on(baseSpaceModule.getTableName()+".ID=Tickets.RESOURCE_ID");
					
					if(xAggregateOpperator.equals(SpaceAggregateOperator.BUILDING)) {
						
						report.getxAxisField().getField().setDisplayName("Building");
						report.getxAxisField().getField().setName("building");
					}
				}
			}
		}
		if(report.getY1Axis() != null || report.getY1AxisField() != null) {
			reportY1AxisField = DashboardUtil.getReportField(report.getY1AxisField());
			AggregateOperator y1AggregateOpperator = report.getY1AxisAggregateOpperator();
			y1AxisField = reportY1AxisField.getField();
			y1AxisField = y1AggregateOpperator.getSelectField(y1AxisField);
		}
		else {
			y1AxisField = NumberAggregateOperator.COUNT.getSelectField(xAxisField);
			reportY1AxisField = new ReportFieldContext();
			reportY1AxisField.setModuleField(y1AxisField);
		}
		xAxisField.setName("label");
		y1AxisField.setName("value");
		report.setY1AxisField(reportY1AxisField);

		if ("WorkOrders".equals(module.getTableName())){
			builder.leftJoin("PM_To_WO").on("WorkOrders.ID=WO_ID");
		}
		
		if(userFilterValues != null && report.getReportUserFilters() != null) {
			for(ReportUserFilterContext userFilter : report.getReportUserFilters()) {
				if(userFilterValues.containsKey(userFilter.getId().toString())) {
					Object userFilterValue = userFilterValues.get(userFilter.getId().toString());
					if(userFilterValue instanceof JSONArray) {
						JSONArray userFilterJsonValue = (JSONArray) userFilterValue;
						builder.andCustomWhere(userFilter.getWhereClause(), userFilterJsonValue.get(0),userFilterJsonValue.get(1));
					}
					else {
						builder.andCustomWhere(userFilter.getWhereClause(), userFilterValue);
					}
				}
			}
		}
		String groupByString = "label";
		
		if(report.getGroupBy() != null) {
			ReportFieldContext reportGroupByField = DashboardUtil.getReportField(report.getGroupByField());
			report.setGroupByField(reportGroupByField);
			FacilioField groupByField = reportGroupByField.getField();
			groupByField.setName("groupBy");
			fields.add(groupByField);
			builder.groupBy("groupBy");
			groupByString = groupByString + ",groupBy";
		}
			
		if (report.getY1AxisAggregateOpperator() != FormulaContext.DateAggregateOperator.ACTUAL) {
			builder.groupBy(groupByString);
		}
		
		if(report.getLimit() != null) {
			builder.limit(report.getLimit());
		}
		
		if(report.getOrderBy() != null) {
			if(report.getOrderByFunc() != null) {
				builder.orderBy(report.getOrderBy() +" "+report.getOrderByFunc().getStringValue());
			}
			else {
				builder.orderBy(report.getOrderBy());
			}
		}
		
		Condition dateCondition = null;
		Criteria criteria = null;
		if(baseLineId != -1) {
			BaseLineContext baseLineContext = BaseLineAPI.getBaseLine(baseLineId);
			DateRange dateRange;
			if(dateFilter != null) {
				System.out.println("dateFilter --- "+dateFilter);
				dateRange = new DateRange((long)dateFilter.get(0), (long)dateFilter.get(1));
			}
			else {
				dateRange = report.getDateFilter().getOperator().getRange(null);
			}
			System.out.println("start -- "+dateRange.getStartTime() +" end -- "+dateRange.getEndTime());
			Condition condition = baseLineContext.getBaseLineCondition(report.getDateFilter().getField(), dateRange);
			String baseLineStartValue = condition.getValue().substring(0,condition.getValue().indexOf(","));
			this.baseLineComparisionDiff = dateRange.getStartTime() - Long.parseLong(baseLineStartValue);
			System.out.println(condition);
			builder.andCondition(condition);
		}
		else if(report.getDateFilter() != null) {
			dateCondition = DashboardUtil.getDateCondition(report, dateFilter, module);
			builder.andCondition(dateCondition);
		}
		
		if(criteriaId != -1) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), criteriaId);
		}
		else if(report.getReportCriteriaContexts() != null) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), report.getReportCriteriaContexts().get(0).getCriteriaId());
		}
		if(criteria != null) {
			builder.andCriteria(criteria);
		}
		if(report.getCriteria() != null) {
			builder.andCriteria(report.getCriteria());
		}
		
		fields.add(y1AxisField);
		fields.add(xAxisField);
		builder.select(fields);
		List<Map<String, Object>> rs = builder.get();
		System.out.println("builder --- "+reportContext.getId() +"   "+baseLineId);
		System.out.println("builder --- "+builder);
		
		System.out.println("res 1-- "+rs);
		
		if(report.getGroupBy() != null) {
			
			Multimap<Object, JSONObject> res = ArrayListMultimap.create();
			HashMap<String, Object> labelMapping = new HashMap<>();
			
			for(int i=0;i<rs.size();i++) {
	 			Map<String, Object> thisMap = rs.get(i);
	 			if(thisMap!=null) {
	 				
	 					String strLabel = (thisMap.get("label") != null) ? thisMap.get("label").toString() : "Unknown";
	 					JSONObject value = new JSONObject();
		 				value.put("label", thisMap.get("groupBy"));
		 				value.put("value", thisMap.get("value"));
		 				
		 				Object xlabel = thisMap.get("label");
		 				if(thisMap.get("dummyField") != null) {
		 					xlabel = thisMap.get("dummyField");
		 				}
		 				if (labelMapping.containsKey(strLabel)) {
		 					xlabel = labelMapping.get(strLabel);
		 				}
		 				else {
		 					labelMapping.put(strLabel, xlabel);
		 				}
		 				res.put(xlabel, value);
	 			}
		 	}
			JSONArray finalres = new JSONArray();
			for(Object key : res.keySet()) {
				JSONObject j1 = new JSONObject();
				j1.put("label", key);
				j1.put("value", res.get(key));
				finalres.add(j1);
			}
			ticketData = finalres;
		}
		else {
			JSONArray res = new JSONArray();
			
			for(int i=0;i<rs.size();i++) {

				Map<String, Object> thisMap = rs.get(i);
	 			JSONObject component = new JSONObject();
	 			if(thisMap!=null) {
	 				if(thisMap.get("dummyField") != null) {
	 					component.put("label", thisMap.get("dummyField"));
	 				}
	 				else {
	 					Object lbl = thisMap.get("label");
	 					component.put("label", lbl);
	 				}
 					component.put("value", thisMap.get("value"));
 					res.add(component);
	 			}
		 	}
			System.out.println("res -- "+res);
			ticketData = res;
		}
		return ticketData;
	}
	
	public String deleteReport() throws Exception {
		DashboardUtil.deleteReport(reportId);
		return SUCCESS;
	}
	
	
	private JSONArray getDataForReadings(ReportContext report, FacilioModule module, JSONArray dateFilter, JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception {
		JSONArray readingData = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				;
		
		builder.orderBy("TTIME");
		
		if(module.getExtendModule() != null) {
			builder.innerJoin(module.getExtendModule().getTableName())
				.on(module.getTableName()+".Id="+module.getExtendModule().getTableName()+".Id");
		}
		
		ReportFieldContext reportXAxisField = DashboardUtil.getReportField(report.getxAxisField());
		report.setxAxisField(reportXAxisField);
		FacilioField xAxisField = reportXAxisField.getField();
		
		GenericSelectRecordBuilder subBuilder = null;
		FacilioModule energyMeterModule = modBean.getModule("energymeter");
		
		if(!module.getName().equals(xAxisField.getModule().getName())) {

			subBuilder = new GenericSelectRecordBuilder();
			subBuilder.table(energyMeterModule.getTableName());
			
			xAxisField.setColumnName("PARENT_METER_ID");
			xAxisField.setName("parentId");
			xAxisField.setModule(module);
		}
		
		boolean isEnergyDataWithTimeFrame = false;
		if(xAxisField.getColumnName().contains("TTIME") && module.getName().equals("energydata")) {
			isEnergyDataWithTimeFrame = true;
		}
		
		List<FacilioField> fields = new ArrayList<>();
		if(xAxisField.getDataTypeEnum().equals(FieldType.DATE_TIME)) {
			FacilioField dummyField = new FacilioField();
			dummyField.setColumnName(xAxisField.getColumnName());
			if (report.getXAxisAggregateOpperator() == FormulaContext.DateAggregateOperator.ACTUAL) {
				dummyField = DateAggregateOperator.ACTUAL.getSelectField(dummyField);
			}
			else {
				dummyField = NumberAggregateOperator.MAX.getSelectField(dummyField);
			}
			dummyField.setName("dummyField");
			fields.add(dummyField);
		}
		
		FacilioField y1AxisField = null;
		ReportFieldContext reportY1AxisField;
		AggregateOperator xAggregateOpperator = report.getXAxisAggregateOpperator();
		boolean isGroupBySpace = false;
		if(!xAggregateOpperator.getValue().equals(NumberAggregateOperator.COUNT.getValue())) {
			if (xAggr != -1) {
				xAggregateOpperator = AggregateOperator.getAggregateOperator(xAggr);
				report.setxAxisaggregateFunction(xAggr);
			}
			else if ((dateFilter != null || report.getDateFilter() != null) && xAxisField.getDataTypeEnum().equals(FieldType.DATE_TIME)) {
				
				int oprId =  dateFilter != null ? DashboardUtil.predictDateOpperator(dateFilter) : report.getDateFilter().getOperatorId();
				if(getIsHeatMap() || (reportContext.getChartType() != null && reportContext.getChartType().equals(ReportChartType.HEATMAP.getValue())) ) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.HOURSOFDAYONLY;
					report.setChartType(ReportChartType.HEATMAP.getValue());
				}
				else if (oprId == DateOperators.TODAY.getOperatorId() || oprId == DateOperators.YESTERDAY.getOperatorId() || oprId == DateOperators.TODAY_UPTO_NOW.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.HOURSOFDAY;
				}
				else if (oprId == DateOperators.CURRENT_WEEK.getOperatorId() || oprId == DateOperators.LAST_WEEK.getOperatorId() || oprId == DateOperators.CURRENT_WEEK_UPTO_NOW.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.FULLDATE;
					if(report.getIsComparisionReport()) {
						xAggregateOpperator = FormulaContext.DateAggregateOperator.WEEKDAY;
					}
				}
				else if (oprId == DateOperators.CURRENT_MONTH.getOperatorId() || oprId == DateOperators.LAST_MONTH.getOperatorId() || oprId == DateOperators.CURRENT_MONTH_UPTO_NOW.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.FULLDATE;
					if(report.getIsComparisionReport()) {
						xAggregateOpperator = FormulaContext.DateAggregateOperator.DAYSOFMONTH;
					}
				}
				else if (oprId == DateOperators.CURRENT_YEAR.getOperatorId() || oprId == DateOperators.LAST_YEAR.getOperatorId() || oprId == DateOperators.CURRENT_YEAR_UPTO_NOW.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.MONTHANDYEAR;
				}
				report.setxAxisaggregateFunction(xAggregateOpperator.getValue());
			}
			
			if (getIsHeatMap() || (reportContext.getChartType() != null && reportContext.getChartType().equals(ReportChartType.HEATMAP.getValue())) || !report.getIsHighResolutionReport()) {
				
				if(xAggregateOpperator instanceof SpaceAggregateOperator) {
					isGroupBySpace = true;
					FacilioModule baseSpaceModule = modBean.getModule("basespace");
					
					subBuilder.innerJoin(baseSpaceModule.getTableName())
					.on(baseSpaceModule.getTableName()+".ID=Energy_Meter.PURPOSE_SPACE_ID")
					.andCustomWhere(baseSpaceModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId());
					
					if(xAggregateOpperator.equals(SpaceAggregateOperator.BUILDING)) {
						
						subBuilder.andCustomWhere(baseSpaceModule.getTableName()+".SPACE_TYPE = " + BaseSpaceContext.SpaceType.BUILDING.getIntVal());
						subBuilder.andCustomWhere("Energy_Meter.PURPOSE_ID = "+"(SELECT ID FROM bms.Energy_Meter_Purpose where ORGID = 1 and Name=\"Main\")");
						subBuilder.andCustomWhere("Energy_Meter.IS_ROOT = 1 ");
						
						report.getxAxisField().getField().setDisplayName("Building");
						report.getxAxisField().getField().setName("building");
					}
				}
				else if(xAggregateOpperator instanceof EnergyPurposeAggregateOperator) {
					
					FacilioModule energyMeterPurposeModule = modBean.getModule("energymeterpurpose");
					
					subBuilder.innerJoin(energyMeterPurposeModule.getTableName())
					.on(energyMeterPurposeModule.getTableName()+".ID=Energy_Meter.PURPOSE_ID")
					.andCustomWhere(energyMeterPurposeModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
					.andCustomWhere("Energy_Meter.IS_ROOT = 1 ")
					.andCustomWhere(energyMeterPurposeModule.getTableName()+".Name!=\"Main\"");
					if(reportContext.getReportSpaceFilterContext() != null && reportContext.getReportSpaceFilterContext().getBuildingId() != null) {
						subBuilder.andCustomWhere(energyMeterModule.getTableName()+".PURPOSE_SPACE_ID="+reportContext.getReportSpaceFilterContext().getBuildingId());
					}
					report.getxAxisField().getField().setDisplayName("Service");
					report.getxAxisField().getField().setName("Service");
				}
				else {
					xAxisField = xAggregateOpperator.getSelectField(xAxisField);
				}
			}
		}
		
		if(report.getY1Axis() != null || report.getY1AxisField() != null) {
			reportY1AxisField = DashboardUtil.getReportField(report.getY1AxisField());
			AggregateOperator y1AggregateOpperator = report.getY1AxisAggregateOpperator();
			y1AxisField = reportY1AxisField.getField();
			if(y1AggregateOpperator != null) {
				y1AxisField = y1AggregateOpperator.getSelectField(y1AxisField);
			}
		}
		else {
			y1AxisField = NumberAggregateOperator.COUNT.getSelectField(xAxisField);
			reportY1AxisField = new ReportFieldContext();
			reportY1AxisField.setModuleField(y1AxisField);
		}
		xAxisField.setName("label");
		y1AxisField.setName("value");
		report.setY1AxisField(reportY1AxisField);

		if(userFilterValues != null && report.getReportUserFilters() != null) {
			for(ReportUserFilterContext userFilter : report.getReportUserFilters()) {
				if(userFilterValues.containsKey(userFilter.getId().toString())) {
					Object userFilterValue = userFilterValues.get(userFilter.getId().toString());
					if(userFilterValue instanceof JSONArray) {
						JSONArray userFilterJsonValue = (JSONArray) userFilterValue;
						builder.andCustomWhere(userFilter.getWhereClause(), userFilterJsonValue.get(0),userFilterJsonValue.get(1));
					}
					else {
						builder.andCustomWhere(userFilter.getWhereClause(), userFilterValue);
					}
				}
			}
		}
		String groupByString = "label";
		if(report.getGroupBy() != null) {
			ReportFieldContext reportGroupByField = DashboardUtil.getReportField(report.getGroupByField());
			report.setGroupByField(reportGroupByField);
			FacilioField groupByField = reportGroupByField.getField();
			groupByField.setName("groupBy");
			
			if(!groupByField.getModule().getName().equals(module.getName())) {
				
				groupByField.setColumnName("PARENT_METER_ID");
				groupByField.setModule(module);
				
				subBuilder = new GenericSelectRecordBuilder();
				subBuilder.table(energyMeterModule.getTableName());
				
				if(report.getGroupByAggregateOpperator() instanceof SpaceAggregateOperator) {
					
					isGroupBySpace = true;
					
					FacilioModule baseSpaceModule = modBean.getModule("basespace");
					
					subBuilder.innerJoin(baseSpaceModule.getTableName())
					.on(baseSpaceModule.getTableName()+".ID=Energy_Meter.PURPOSE_SPACE_ID")
					.andCustomWhere(baseSpaceModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId());
					
					if(report.getGroupByAggregateOpperator().equals(SpaceAggregateOperator.BUILDING)) {
						
						subBuilder.andCustomWhere(baseSpaceModule.getTableName()+".SPACE_TYPE = " + BaseSpaceContext.SpaceType.BUILDING.getIntVal());
						subBuilder.andCustomWhere("Energy_Meter.PURPOSE_ID = "+"(SELECT ID FROM bms.Energy_Meter_Purpose where ORGID = 1 and Name=\"Main\")");
						subBuilder.andCustomWhere("Energy_Meter.IS_ROOT = 1");
						
						report.getGroupByField().setFieldLabel("Building");
					}
				}
			}
			
			fields.add(groupByField);
			groupByString = groupByString + ",groupBy";
		}
			
		if (report.getY1AxisAggregateOpperator() != FormulaContext.DateAggregateOperator.ACTUAL) {
			builder.groupBy(groupByString);
		}
		
		if(report.getLimit() != null) {
			builder.limit(report.getLimit());
		}
		if(report.getOrderBy() != null) {
			if(report.getOrderByFunc() != null) {
				builder.orderBy(report.getOrderBy() +" "+report.getOrderByFunc().getStringValue());
			}
			else {
				builder.orderBy(report.getOrderBy());
			}
		}
		else {
			
		}
		
		Condition dateCondition = null;
		Criteria criteria = null;
		if(baseLineId != -1) {
			BaseLineContext baseLineContext = BaseLineAPI.getBaseLine(baseLineId);
			DateRange dateRange;
			if(dateFilter != null) {
				System.out.println("dateFilter --- "+dateFilter);
				dateRange = new DateRange((long)dateFilter.get(0), (long)dateFilter.get(1));
			}
			else {
				dateRange = report.getDateFilter().getOperator().getRange(null);
			}
			System.out.println("start -- "+dateRange.getStartTime() +" end -- "+dateRange.getEndTime());
			Condition condition = baseLineContext.getBaseLineCondition(report.getDateFilter().getField(), dateRange);
			String baseLineStartValue = condition.getValue().substring(0,condition.getValue().indexOf(","));
			this.baseLineComparisionDiff = dateRange.getStartTime() - Long.parseLong(baseLineStartValue);
			System.out.println(condition);
			builder.andCondition(condition);
		}
		else if(report.getDateFilter() != null) {
			dateCondition = DashboardUtil.getDateCondition(report, dateFilter, module);
			builder.andCondition(dateCondition);
		}
		
		if(criteriaId != -1) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), criteriaId);
		}
		else if(report.getReportCriteriaContexts() != null) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), report.getReportCriteriaContexts().get(0).getCriteriaId());
		}
		if(criteria != null) {
			builder.andCriteria(criteria);
		}
		if(report.getCriteria() != null) {
			builder.andCriteria(report.getCriteria());
		}
		
		String energyMeterValue = "";
		JSONObject buildingVsMeter = new JSONObject();
		Map<Long,Double> buildingVsArea = null;
		HashMap <Long, ArrayList<Long>> purposeVsMeter= new HashMap<Long,ArrayList<Long>>();
		JSONObject purposeVsMeter1 = new JSONObject();
		Condition buildingCondition = null;
		Multimap<Long,EnergyMeterContext> energyMeterMap =  ArrayListMultimap.create();
		
		if(subBuilder != null) {
			List<FacilioField> subFields = new ArrayList<>(); 
			
			FacilioField facilioField = new FacilioField();
			facilioField.setColumnName("ID");
			facilioField.setModule(energyMeterModule);
			facilioField.setName("id");
			subFields.add(facilioField);
			
			FacilioField facilioField1 = new FacilioField();
			facilioField1.setColumnName("PURPOSE_SPACE_ID");
			facilioField1.setModule(energyMeterModule);
			facilioField1.setName("purposeSpace");
			subFields.add(facilioField1);
			
			FacilioField facilioField2 = new FacilioField();
			facilioField2.setColumnName("PURPOSE_ID");
			facilioField2.setModule(energyMeterModule);
			facilioField2.setName("purpose");
			
			subFields.add(facilioField2);
			subBuilder.select(subFields);
			
			List<Map<String, Object>> props = subBuilder.get();
			System.out.println("subBuilder -- "+subBuilder);
			if(props != null && !props.isEmpty()) {
				
				List<Long> meterIds = new ArrayList<Long>();
				for(Map<String, Object> prop:props) {
					
					meterIds.add((long) prop.get("id"));
					
					if(isGroupBySpace) {
						buildingVsMeter.put((long) prop.get("id"), (long) prop.get("purposeSpace"));
					}
					
					ArrayList<Long> meterList = purposeVsMeter.get((long) prop.get("purpose"));
					if (meterList == null) {
						meterList = new ArrayList<Long>();
					}
					meterList.add((long) prop.get("id"));
					purposeVsMeter.put((long) prop.get("purpose"), meterList);
					
					purposeVsMeter1.put((long) prop.get("id"), (long) prop.get("purpose"));
				}
				String meterIdStr = StringUtils.join(meterIds, ",");
				buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","parentId", meterIdStr, NumberOperators.EQUALS);
			}
		}
		
		if (report.getEnergyMeter() != null) {
			if (report.getEnergyMeter().getSubMeterId() != null) {
				energyMeterValue = report.getEnergyMeter().getSubMeterId() + "";
				buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", report.getEnergyMeter().getSubMeterId()+"", NumberOperators.EQUALS);
			}
			else if (report.getReportSpaceFilterContext() != null && report.getReportSpaceFilterContext().getBuildingId() != null && report.getEnergyMeter().getServiceId() != null) {
				List<EnergyMeterContext> meters = DeviceAPI.getAllEnergyMeters(report.getReportSpaceFilterContext().getBuildingId(), report.getEnergyMeter().getServiceId());
				if (meters != null && meters.size() > 0) {
					List<Long> meterIds = new ArrayList<Long>();
					for (EnergyMeterContext meter : meters) {
						
						if(meter.isRoot()) {
							meterIds = new ArrayList<Long>();
							meterIds.add(meter.getId());
							break;
						}
						meterIds.add(meter.getId());
					}
					
					String meterIdStr = StringUtils.join(meterIds, ",");
					energyMeterValue = meterIdStr;
					buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
				}
			}
			else if (report.getReportSpaceFilterContext() != null && report.getReportSpaceFilterContext().getBuildingId() != null) {
				if ("service".equalsIgnoreCase(report.getEnergyMeter().getGroupBy())) {
					
					List<EnergyMeterContext> meters = DeviceAPI.getRootServiceMeters(report.getReportSpaceFilterContext().getBuildingId()+"");
					if (meters != null && meters.size() > 0) {
						List<Long> meterIds = new ArrayList<Long>();
						for (EnergyMeterContext meter : meters) {
							meterIds.add(meter.getId());
							buildingVsMeter.put(meter.getId(), meter.getPurpose().getId());
						}
						
						String meterIdStr = StringUtils.join(meterIds, ",");
						energyMeterValue = meterIdStr;
						buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
					}
					
					FacilioField groupByField = new FacilioField();
					groupByField.setName("groupBy");
					groupByField.setDataType(FieldType.NUMBER);
					groupByField.setColumnName("PARENT_METER_ID");
					groupByField.setDisplayName("Service");
					groupByField.setModule(module);
					fields.add(groupByField);
					builder.groupBy("label, groupBy");
					
					ReportFieldContext groupByReportField = new ReportFieldContext();
					groupByReportField.setModuleField(groupByField);
					
					report.setGroupByField(groupByReportField);
					
					report.setGroupBy(-1L);
				}
				else if ("floor".equalsIgnoreCase(report.getEnergyMeter().getGroupBy())) {
					SelectRecordsBuilder<EnergyMeterContext> builder12 = new SelectRecordsBuilder<EnergyMeterContext>()
							.table(modBean.getModule("energymeter").getTableName())
							.moduleName("energymeter")
							.beanClass(EnergyMeterContext.class)
							.select(modBean.getAllFields("energymeter"))
							.innerJoin(modBean.getModule("basespace").getTableName())
							.on(modBean.getModule("basespace").getTableName()+".ID = "+modBean.getModule("energymeter").getTableName()+".PURPOSE_SPACE_ID")
							.andCustomWhere(modBean.getModule("basespace").getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
							.andCustomWhere(modBean.getModule("basespace").getTableName()+".BUILDING_ID="+ report.getReportSpaceFilterContext().getBuildingId())
							.andCustomWhere(modBean.getModule("basespace").getTableName()+".FLOOR_ID>0");
					
					
					
					List<EnergyMeterContext> props = builder12.get();
					List<Long> meterIds = new ArrayList<Long>();
					if(props != null && !props.isEmpty()) {
						for(EnergyMeterContext energyMeterContext:props) {
							
							if(energyMeterContext.isRoot()) {
								meterIds = new ArrayList<Long>();
								meterIds.add(energyMeterContext.getId());
								if(energyMeterMap.containsKey(energyMeterContext.getSpaceId())) {
									energyMeterMap.removeAll(energyMeterContext.getSpaceId());
								}
								energyMeterMap.put(energyMeterContext.getPurposeSpace().getId(), energyMeterContext);
								break;
							}
							energyMeterMap.put(energyMeterContext.getPurposeSpace().getId(), energyMeterContext);
							meterIds.add(energyMeterContext.getId());
						}
					}
					System.out.println("energyMeterMap -- "+energyMeterMap);
					if(!meterIds.isEmpty()) {
						String meterIdStr = StringUtils.join(meterIds, ",");
						energyMeterValue = meterIdStr;
						buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
					}
					FacilioField groupByField = new FacilioField();
					groupByField.setName("groupBy");
					groupByField.setDataType(FieldType.NUMBER);
					groupByField.setColumnName("PARENT_METER_ID");
					groupByField.setDisplayName("Service");
					groupByField.setModule(module);
					fields.add(groupByField);
					builder.groupBy("label, groupBy");
					
					ReportFieldContext groupByReportField = new ReportFieldContext();
					groupByReportField.setModuleField(groupByField);
					
					report.setGroupByField(groupByReportField);
					
					report.setGroupBy(-1L);
				}
				else {
					List<EnergyMeterContext> meters = DeviceAPI.getMainEnergyMeter(report.getReportSpaceFilterContext().getBuildingId()+"");
					if (meters != null && meters.size() > 0) {
						List<Long> meterIds = new ArrayList<Long>();
						for (EnergyMeterContext meter : meters) {
							meterIds.add(meter.getId());
						}
						
						String meterIdStr = StringUtils.join(meterIds, ",");
						energyMeterValue = meterIdStr;
						buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
					}
				}
			}
			else if (report.getEnergyMeter().getGroupBy() != null && "building".equalsIgnoreCase(report.getEnergyMeter().getGroupBy())) {
				
				List<EnergyMeterContext> meters = DeviceAPI.getAllMainEnergyMeters();
				if (meters != null && meters.size() > 0) {
					List<Long> meterIds = new ArrayList<Long>();
					for (EnergyMeterContext meter : meters) {
						meterIds.add(meter.getId());
						buildingVsMeter.put(meter.getId(), meter.getPurposeSpace().getId());
					}
					
					String meterIdStr = StringUtils.join(meterIds, ",");
					energyMeterValue = meterIdStr;
					buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
				}
				String buildingList = StringUtils.join(buildingVsMeter.values(),",");
				buildingVsArea = ReportsUtil.getMapping(SpaceAPI.getBuildingArea(buildingList), "ID", "AREA");
				
				if (!xAxisField.getColumnName().equalsIgnoreCase("PARENT_METER_ID")) {
					FacilioField groupByField = new FacilioField();
					groupByField.setName("groupBy");
					groupByField.setDataType(FieldType.NUMBER);
					groupByField.setColumnName("PARENT_METER_ID");
					groupByField.setDisplayName("Building");
					groupByField.setModule(module);
					fields.add(groupByField);
					builder.groupBy("label, groupBy");
					
					ReportFieldContext groupByReportField = new ReportFieldContext();
					groupByReportField.setModuleField(groupByField);
					
					report.setGroupByField(groupByReportField);
					
					report.setGroupBy(-1L);
				}
			}
			else if (report.getEnergyMeter().getGroupBy() != null && "service".equalsIgnoreCase(report.getEnergyMeter().getGroupBy())) {
				
				List<EnergyMeterContext> meters = DeviceAPI.getAllServiceMeters();
				
				if (meters != null && meters.size() > 0) {
					List<Long> meterIds = new ArrayList<Long>();
					
					for (EnergyMeterContext meter : meters) {
						if (meter.getPurpose() != null) {
							meterIds.add(meter.getId());
							
							long purposeId = meter.getPurpose().getId();
							ArrayList<Long> meterList = purposeVsMeter.get(purposeId);
							if (meterList == null) {
								meterList = new ArrayList<Long>();
								purposeVsMeter.put(purposeId, meterList);
							}
							purposeVsMeter1.put(meter.getId(), purposeId);
							meterList.add(meter.getId());
						}
					}
					
					String meterIdStr = StringUtils.join(meterIds, ",");
					energyMeterValue = meterIdStr;
					buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
				}
				
				if (!xAxisField.getColumnName().equalsIgnoreCase("PARENT_METER_ID")) {
					FacilioField groupByField = new FacilioField();
					groupByField.setName("groupBy");
					groupByField.setDataType(FieldType.NUMBER);
					groupByField.setColumnName("PARENT_METER_ID");
					groupByField.setDisplayName("Service");
					groupByField.setModule(module);
					fields.add(groupByField);
					builder.groupBy("label, groupBy");
					
					ReportFieldContext groupByReportField = new ReportFieldContext();
					groupByReportField.setModuleField(groupByField);
					
					report.setGroupByField(groupByReportField);
					
					report.setGroupBy(-1L);
				}
				else {
					xAxisField.setDisplayName("Service");
				}
			}
		}
		fields.add(y1AxisField);
		fields.add(xAxisField);
		builder.select(fields);
		if(buildingCondition != null) {
			builder.andCondition(buildingCondition);
		}
		List<Map<String, Object>> rs = builder.get();
		System.out.println("builder --- "+reportContext.getId() +"   "+baseLineId);
		System.out.println("builder --- "+builder);
//		System.out.println("rs1 -- "+rs);
		
		Map<String, Double> violatedReadings = getViolatedReadings(report, dateFilter);
		
		if(report.getGroupBy() != null) {
			
			Multimap<Object, JSONObject> res = ArrayListMultimap.create();
			
			HashMap<String, Object> labelMapping = new HashMap<>();
			
			HashMap<String, Object> purposeMapping = new HashMap<>();
			
			if (report.getEnergyMeter() != null && report.getEnergyMeter().getGroupBy() != null && report.getReportSpaceFilterContext() != null && report.getReportSpaceFilterContext().getBuildingId() != null && "floor".equalsIgnoreCase(report.getEnergyMeter().getGroupBy())) {
				List<String> lables = getDistinctLabel(rs);
				JSONArray totalconsumptionBySpace = new JSONArray();
				System.out.println("lables -- "+lables);
				if(!energyMeterMap.isEmpty()) {
					for(String label:lables) {
						JSONArray temp = new JSONArray();
						Long labelDummyValue = null;
						for(Long spaceId :energyMeterMap.keySet()) {
							Double sumValue = 0.0;  
							List<EnergyMeterContext> energyMeterList = (List<EnergyMeterContext>) energyMeterMap.get(spaceId);
							for(EnergyMeterContext energyMeter:energyMeterList) {
								for(Map<String, Object> prop:rs) {
									if(label.equals(prop.get("label").toString()) && energyMeter.getId() == (long)prop.get("groupBy")) {
										sumValue = sumValue + (Double) prop.get("value");
										labelDummyValue = (Long) prop.get("dummyField");
									}
								}
							}
							JSONObject value = new JSONObject();
							ResourceContext resource = ResourceAPI.getResource(spaceId);
							value.put("label", resource.getName());
							value.put("value", sumValue);
							temp.add(value);
							System.out.println(temp);
						}
						JSONObject temp1 = new JSONObject();
						temp1.put("label", labelDummyValue);
						temp1.put("value", temp);
						totalconsumptionBySpace.add(temp1);
					}
				}
				System.out.println("totalconsumptionBySpace -- "+totalconsumptionBySpace);
				readingData = totalconsumptionBySpace;
			}
			else {
				for(int i=0;i<rs.size();i++) {
		 			Map<String, Object> thisMap = rs.get(i);
		 			if(thisMap!=null) {
		 				
		 				if (report.getEnergyMeter() != null && report.getEnergyMeter().getGroupBy() != null&& report.getReportSpaceFilterContext() != null && (report.getReportSpaceFilterContext().getBuildingId() == null || report.getReportSpaceFilterContext().getBuildingId() <= 0) && "service".equalsIgnoreCase(report.getEnergyMeter().getGroupBy())) {
		 					Object xlabel = thisMap.get("label");
		 					if(thisMap.get("dummyField") != null) {
			 					xlabel = thisMap.get("dummyField");
			 				}
			 				if (labelMapping.containsKey(thisMap.get("label").toString())) {
			 					xlabel = labelMapping.get(thisMap.get("label").toString());
			 				}
			 				else {
			 					labelMapping.put(thisMap.get("label").toString(), xlabel);
			 				}
			 				
		 					Double xvalue = (Double) thisMap.get("value");
		 					String groupBy = purposeVsMeter.containsKey(thisMap.get("groupBy")) ? purposeVsMeter.get(thisMap.get("groupBy")).toString() : thisMap.get("groupBy").toString();
		 					
		 					if (purposeMapping.containsKey(xlabel + "-" + groupBy)) {
		 						JSONObject value = (JSONObject) res.get(xlabel);
		 						Double existingValue = (Double) value.get("value");
		 						value.put("value", existingValue + xvalue);
		 					}
		 					else {
		 						JSONObject value = new JSONObject();
		 						value.put("label", purposeVsMeter.containsKey(thisMap.get("groupBy")) ? purposeVsMeter.get(thisMap.get("groupBy")) : thisMap.get("groupBy"));
		 						value.put("value", xvalue);
		 						res.put(xlabel, value);
		 					}
		 				}
		 				else {
		 					String strLabel = (thisMap.get("label") != null) ? thisMap.get("label").toString() : "Unknown";
		 					JSONObject value = new JSONObject();
			 				value.put("label", buildingVsMeter.containsKey(thisMap.get("groupBy")) ? buildingVsMeter.get(thisMap.get("groupBy")) : thisMap.get("groupBy"));
			 				if ("cost".equalsIgnoreCase(report.getY1AxisUnit())) {
			 					Double d = (Double) thisMap.get("value");
			 					value.put("value", d*ReportsUtil.unitCost);
			 					value.put("orig_value", d);
			 				}
			 				else {
			 					value.put("value", thisMap.get("value"));
			 				}
			 				
			 				Object xlabel = thisMap.get("label");
			 				if(thisMap.get("dummyField") != null) {
			 					xlabel = thisMap.get("dummyField");
			 				}
			 				if (labelMapping.containsKey(strLabel)) {
			 					xlabel = labelMapping.get(strLabel);
			 				}
			 				else {
			 					labelMapping.put(strLabel, xlabel);
			 				}
			 				res.put(xlabel, value);
		 				}
		 			}
			 	}
			JSONArray finalres = new JSONArray();
			for(Object key : res.keySet()) {
				JSONObject j1 = new JSONObject();
				j1.put("label", key);
				j1.put("value", res.get(key));
				finalres.add(j1);
			}
				
//			System.out.println("finalres -- "+finalres);
			readingData = finalres;
			}
		}
		else {
			JSONArray res = new JSONArray();
			
			JSONObject purposeIndexMapping = new JSONObject();
			for(int i=0;i<rs.size();i++) {
				boolean newPurpose = false;
	 			Map<String, Object> thisMap = rs.get(i);
	 			JSONObject component = new JSONObject();
	 			if(thisMap!=null) {
	 				// checking and excluding the violated points
	 				if (violatedReadings != null && violatedReadings.containsKey(thisMap.get("label").toString())) {
	 					Double violatedValue = violatedReadings.get(thisMap.get("label").toString());
	 					Double d = (Double) thisMap.get("value");
	 					if (d != null) {
	 						Double newValue = d - violatedValue;
	 						if (newValue < 0) {
	 							newValue = 0d;
	 						}
	 						thisMap.put("value", newValue);
	 						component.put("violated_value", d);
	 					}
	 				}
	 				
	 				if(thisMap.get("dummyField") != null) {
	 					component.put("label", thisMap.get("dummyField"));
	 				}
	 				else {
	 					Object lbl = thisMap.get("label");
	 					if (buildingVsMeter.containsKey(thisMap.get("label"))) {
	 						lbl = buildingVsMeter.get(thisMap.get("label"));
	 					}
	 					else if (purposeVsMeter1.containsKey(thisMap.get("label"))) {
	 						lbl = purposeVsMeter1.get(thisMap.get("label"));
	 						if (!purposeIndexMapping.containsKey(lbl)) {
	 							purposeIndexMapping.put(lbl, res.size());
	 							newPurpose = true;
	 						}
	 					}
	 					component.put("label", lbl);
	 				}
	 				if (!newPurpose && purposeIndexMapping.containsKey(component.get("label"))) {
	 					JSONObject tmpComp = (JSONObject) res.get((Integer) purposeIndexMapping.get(component.get("label")));
	 					if ("cost".equalsIgnoreCase(report.getY1AxisUnit())) {
	 						Double d = (Double) thisMap.get("value");
	 						Double concatVal = d + (Double) tmpComp.get("orig_value");
	 						tmpComp.put("value", concatVal*ReportsUtil.unitCost);
	 						tmpComp.put("orig_value", concatVal);
	 					}
	 					else {
	 						Double d = (Double) thisMap.get("value");
	 						Double concatVal = d + (Double) tmpComp.get("value");
	 						tmpComp.put("value", thisMap.get("value"));
	 					}
	 				}
	 				else {
	 					if ("cost".equalsIgnoreCase(report.getY1AxisUnit())) {
	 						Double d = (Double) thisMap.get("value");
	 						component.put("value", d*ReportsUtil.unitCost);
	 						component.put("orig_value", d);
	 					}
	 					else if ("eui".equalsIgnoreCase(report.getY1AxisUnit())) {
	 						Double d = (Double) thisMap.get("value");
	 						
	 						Double buildingArea = buildingVsArea.get((Long) component.get("label"));
	 						double eui = ReportsUtil.getEUI(d, buildingArea);
	 						component.put("value", eui);
	 						component.put("orig_value", d);
	 					}
	 					else {
	 						component.put("value", thisMap.get("value"));
	 					}
	 					res.add(component);
	 				}
	 			}
		 	}
//			System.out.println("res -- "+res);
			readingData = res;
		}
		
//		System.out.println("rs after -- "+rs);
		if (report.getReportCriteriaContexts() != null) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), report.getReportCriteriaContexts().get(0).getCriteriaId());
			if(module.getName().equals("energydata") && criteria != null) {
				Map<Integer, Condition> conditions = criteria.getConditions();
				for(Condition condition:conditions.values()) {
					if(condition.getColumnName().equals("Energy_Data.PARENT_METER_ID")) {
						energyMeterValue = energyMeterValue + condition.getValue() +",";
					}
				}
			}
		}
		
		if (energyMeterValue != null && !"".equalsIgnoreCase(energyMeterValue.trim())) {
			this.meterIds = energyMeterValue.split(",");
		}
		
		if(energyMeterValue != null && !"".equalsIgnoreCase(energyMeterValue.trim()) && isEnergyDataWithTimeFrame && !report.getIsComparisionReport()) {
			
			List<FacilioField> alarmVsEnergyFields = new ArrayList<>();
			
			FacilioField subject = new FacilioField();
			subject.setName("subject");
			subject.setDataType(FieldType.STRING);
			subject.setColumnName("SUBJECT");
			subject.setModule(ModuleFactory.getTicketsModule()); ////alarm vs energy data
			alarmVsEnergyFields.add(subject);
			FacilioField modTime = new FacilioField();
			modTime.setName("createdTime");
			modTime.setDataType(FieldType.NUMBER);
			modTime.setColumnName("CREATED_TIME");
			modTime.setModule(ModuleFactory.getAlarmsModule()); ////alarm vs energy data
			alarmVsEnergyFields.add(modTime);
			
			
			
			FacilioField severity = new FacilioField();
			severity.setName("severity");
			severity.setDataType(FieldType.NUMBER);
			severity.setColumnName("SEVERITY");
			severity.setModule(ModuleFactory.getAlarmsModule()); ////alarm vs energy data
			alarmVsEnergyFields.add(severity);
			
			
			
			FacilioField serialNumber = new FacilioField();
			serialNumber.setName("serialNumber");
			serialNumber.setDataType(FieldType.NUMBER);
			serialNumber.setColumnName("SERIAL_NUMBER");
			serialNumber.setModule(ModuleFactory.getTicketsModule());
			alarmVsEnergyFields.add(serialNumber);
			
			FacilioField alarmId = new FacilioField();
			alarmId.setName("alarmid");
			alarmId.setDataType(FieldType.NUMBER);
			alarmId.setColumnName("ALARM_ID");
			alarmId.setModule(ModuleFactory.getAlarmVsEnergyData());
			alarmVsEnergyFields.add(alarmId);
			
			if (energyMeterValue.endsWith(",")) {
				energyMeterValue = energyMeterValue.substring(0, energyMeterValue.length()-1);
			}
			
			GenericSelectRecordBuilder builder1 = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getAlarmVsEnergyData().getTableName())
					.innerJoin(ModuleFactory.getAlarmsModule().getTableName())
					.on(ModuleFactory.getAlarmVsEnergyData().getTableName()+".ALARM_ID="+ModuleFactory.getAlarmsModule().getTableName()+".ID")
					.innerJoin(ModuleFactory.getTicketsModule().getTableName())
					.on(ModuleFactory.getTicketsModule().getTableName()+".ID="+ModuleFactory.getAlarmsModule().getTableName()+".ID")
					.andCustomWhere(ModuleFactory.getTicketsModule().getTableName()+".RESOURCE_ID in ("+energyMeterValue+")")
					.andCustomWhere(ModuleFactory.getAlarmsModule().getTableName()+".ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
					.select(alarmVsEnergyFields);
			
			if (dateCondition != null) {
				dateCondition.setField(modTime);
				dateCondition.setComputedWhereClause(null);
				builder1.andCondition(dateCondition);
			}
			
			List<Map<String, Object>> alarmVsEnergyProps = builder1.get();
			Map<Object ,JSONArray> alarmProps=  getAlarmProps(alarmVsEnergyProps);
			JSONArray relatedAlarms = getAlarmReturnFormat(alarmProps);
			setRelatedAlarms(relatedAlarms);
		}
		return readingData;
	}
	
	private Map<String, Double> getViolatedReadings(ReportContext reportContext, JSONArray dateFilter) throws Exception {
		
		Map<String, Double> violatedReadings = new HashMap<>();
		
		List<Integer> markType = new ArrayList<>();
		if (reportContext.getXAxisAggregateOpperator().getValue() == DateAggregateOperator.DAYSOFMONTH.getValue() || reportContext.getXAxisAggregateOpperator().getValue() == DateAggregateOperator.FULLDATE.getValue()) {
			markType.add(MarkedReadingContext.MarkType.HIGH_VALUE_DAILY_VIOLATION.getValue());
			// markType.add(MarkedReadingContext.MarkType.HIGH_VALUE_HOURLY_VIOLATION.getValue());
		}
		else if (reportContext.getXAxisAggregateOpperator().getValue() == DateAggregateOperator.HOURSOFDAY.getValue() || reportContext.getXAxisAggregateOpperator().getValue() == DateAggregateOperator.HOURSOFDAYONLY.getValue()) {
			markType.add(MarkedReadingContext.MarkType.HIGH_VALUE_HOURLY_VIOLATION.getValue());
		}
		
		if ((dateFilter != null || reportContext.getDateFilter() != null) && !excludeViolatedReadings && !markType.isEmpty()) {
			List<Long> timeRange = new ArrayList<>();
			if (dateFilter != null) {
				timeRange.add((Long) dateFilter.get(0));
				timeRange.add((Long) dateFilter.get(1));
			}
			else if (reportContext.getDateFilter() != null) {
				timeRange.add(Long.parseLong(reportContext.getDateFilter().getOperatorId().toString()));
			}
			
			List<Long> deviceList = new ArrayList<>();
			if (meterIds != null) {
				for (String meterId : meterIds) {
					deviceList.add(Long.parseLong(meterId));
				}
			}
			
			List<Long> moduleList = new ArrayList<>();
			moduleList.add(reportContext.getModuleId());
			
			List<Long> fieldList = new ArrayList<Long>();
			fieldList.add(reportContext.getY1AxisField().getModuleField().getId());
			
			FacilioField label = new FacilioField();
			label.setColumnName("TTIME");
			label = reportContext.getXAxisAggregateOpperator().getSelectField(label);
			label.setName("label");
			
			FacilioField value = new FacilioField();
			value.setColumnName("ACTUAL_VALUE");
			value = NumberAggregateOperator.SUM.getSelectField(value);
			value.setName("value");
			
			List<Map<String, Object>> markedReadings = TimeSeriesAPI.getMarkedReadings(TimeSeriesAPI.getCriteria(timeRange, deviceList, moduleList, fieldList, markType), label, value);
			if (markedReadings != null && !markedReadings.isEmpty()) {
				for (Map<String, Object> reading : markedReadings) {
					violatedReadings.put(reading.get("label").toString(), (Double) reading.get("value"));
				}
			}
		}
		return violatedReadings; 
	}
	
	private List<String> getDistinctLabel(List<Map<String, Object>> rs) {
		List<String> labels = new ArrayList<>();
		for(Map<String, Object> prop:rs) {
			if(!labels.contains((String)prop.get("label"))) {
				labels.add((String)prop.get("label"));
			}
		}
		return labels;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getAlarmReturnFormat(Map<Object, JSONArray> alarmProps) {
		if(alarmProps==null) {
			return null;
		}
		
		JSONArray relatedAlarms= new JSONArray();
		for(Map.Entry<Object, JSONArray> props : alarmProps.entrySet() ) {
			
			JSONArray list= props.getValue();
			JSONObject alarms= (JSONObject)list.get(list.size()-1);
			Long labelTime = (Long)alarms.get("time");
			JSONObject record = new JSONObject();
			record.put("label", labelTime);
			record.put("value", list);
			relatedAlarms.add(record);
		}
		return relatedAlarms;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private Map<Object ,JSONArray> getAlarmProps (List<Map<String, Object>> alarmVsEnergyProps ){

		if(alarmVsEnergyProps==null || alarmVsEnergyProps.isEmpty()) {
			return null;

		}

		Integer aggregator=reportContext.getxAxisaggregateFunction();
		int formatter=19;
		if (aggregator != null) {
			formatter= AggregateOperator.getAggregateOperator(aggregator).getValue();
		}

		Map<Object ,JSONArray> alarmProps= new HashMap<Object,JSONArray>();
		for(Map<String,Object> mappingProp: alarmVsEnergyProps) {

			String subject = (String) mappingProp.get("subject");
			Long createdTime=(Long)mappingProp.get("createdTime");
			Long id= (Long)mappingProp.get("alarmid");
			Long alarmSerialNumber=(Long)mappingProp.get("serialNumber");
			Long alarmSeverity=(Long)mappingProp.get("severity");

			JSONObject props = new JSONObject();
			props.put("alarmId", id);
			props.put("subject", subject);
			props.put("severity", alarmSeverity);
			props.put("time", createdTime);
			props.put("serialNumber", alarmSerialNumber);

			Object key=getKey(createdTime,formatter);
			JSONArray alarmsList= alarmProps.get(key);

			if(alarmsList==null) {
				alarmsList= new JSONArray();
				alarmProps.put(key, alarmsList);
			}
			alarmsList.add(props);
		}
		return alarmProps;
	}
	
	private Object getKey(Long modifiedTime,int formatter) {
		
		Object key=null;
		ZonedDateTime zdt= DateTimeUtil.getDateTime(modifiedTime);
		if(formatter==19) {
			
			 key= zdt.getHour();
		}
		else if (formatter==12 || formatter==13){
			
			key=zdt.toLocalDate().toString();
		}
		else if(formatter==15 || formatter==10) {
			key=zdt.getMonth();
		}
		else if (formatter==11) {
			key=zdt.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
		}
		else if(formatter==17) {
			key=zdt.getDayOfWeek().getValue();
			
		}
		return key;
	}
	
	private List<FacilioField> getDisplayColumns(String module) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> allFields = modBean.getAllFields(module);
		
		String[] displayFieldNames = null;
		
		if ("workorder".equals(module)) {
			displayFieldNames = new String[]{"subject", "category", "space", "assignedTo", "status", "priority", "createdTime", "dueDate"};
		}
		else if ("alarm".equals(module)) {
			displayFieldNames = new String[]{"subject", "severity", "node", "createdTime", "acknowledgedBy", "state"};
		}
		else if ("energydata".equals(module)) {
			displayFieldNames = new String[]{"ttime", "totalEnergyConsumptionDelta"};
		}
		
		List<FacilioField> displayColumns = new ArrayList<>();
		
		for (String fieldName : displayFieldNames) {
			for (FacilioField field : allFields) {
				if (field.getName().equalsIgnoreCase(fieldName)) {
					displayColumns.add(field);
				}
			}
		}
		
		return displayColumns;
	}
	
	private List<FacilioField> displayFields;
	
	public void setDisplayFields(List<FacilioField> displayFields) {
		this.displayFields = displayFields;
	}
	
	public List<FacilioField> getDisplayFields() {
		return this.displayFields;
	}
	
	public String getUnderlyingData() throws Exception {
		
		FacilioModule module = getModule();
		
		List<ModuleBaseWithCustomFields> records = getRawData(null, module);
		
		JSONArray result = new JSONArray();
		if(records != null) {
			for(ModuleBaseWithCustomFields r:records) {
				result.add(r);
			}
		}
		
		setReportData(result);
		// setDisplayFields(getDisplayColumns(module.getName()));

		return SUCCESS;
	}
	
	public String exportData() throws Exception{
	
		FacilioModule module = getModule();
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.CV_NAME, reportId.toString());
		context.put(FacilioConstants.ContextNames.PARENT_VIEW, "report");
		context.put(FacilioConstants.ContextNames.LIMIT_VALUE, -1);
		List<ModuleBaseWithCustomFields> records = getRawData(context, module);
		FacilioView view= (FacilioView)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		FileFormat fileFormat = FileFormat.getFileFormat(type);
		if(fileFormat == FileFormat.PDF || fileFormat == FileFormat.IMAGE) {
			String url = ReportsUtil.getReportClientUrl(module.getName(), reportId, fileFormat);
			if(dateFilter != null && dateFilter.size() > 0) {
				url += "?daterange=" + dateFilter.toJSONString();
			}
			fileUrl = PdfUtil.exportUrlAsPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(),url, fileFormat);
		}
		else {
			fileUrl = ExportUtil.exportData(fileFormat, module, view.getFields(), records);
		}
		
		return SUCCESS;
	}
	
	private List<ModuleBaseWithCustomFields> getRawData(FacilioContext context, FacilioModule module) throws Exception {
		
		if(context == null) {
			context = new FacilioContext();
		}
		
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.REPORT_CONTEXT, reportContext);
		context.put(FacilioConstants.ContextNames.DATE_FILTER, dateFilter);
		
		Chain dataChain = ReportsChainFactory.getReportUnderlyingDataChain();
		dataChain.execute(context);
		
		return (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
	}
	
	private FacilioModule getModule() throws Exception {
		reportContext = DashboardUtil.getReportContext(reportId);
		return ReportsUtil.getReportModule(reportContext);
	}
	
	/*private List<Map<String, Object>> getRawData(FacilioModule module) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId());
		
		builder.select(fields);
		if (module.getExtendModule() != null) {
			builder.innerJoin(module.getExtendModule().getTableName())
				.on(module.getTableName()+".Id="+module.getExtendModule().getTableName()+".Id");
		}
		Criteria criteria = null;
		if (reportContext.getReportCriteriaContexts() != null) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), reportContext.getReportCriteriaContexts().get(0).getCriteriaId());
			builder.andCriteria(criteria);
		}
		if (reportContext.getDateFilter() != null) {
			Condition dateCondition = new Condition();
			dateCondition.setField(reportContext.getDateFilter().getField());
			
			if (this.dateFilter != null) {
				if (this.dateFilter.split(",").length > 1) {
					// between
					dateCondition.setOperator(DateOperators.BETWEEN);
					dateCondition.setValue(this.dateFilter);
				}
				else {
					dateCondition.setOperatorId(Integer.parseInt(this.dateFilter));
				}
			}
			else {
				if (reportContext.getDateFilter().getReportId() == 20) {
					// between
					dateCondition.setOperator(DateOperators.BETWEEN);
					dateCondition.setValue(reportContext.getDateFilter().getVal());
				}
				else {
					dateCondition.setOperatorId(reportContext.getDateFilter().getOperatorId());
				}
			}
			builder.andCondition(dateCondition);
		}
		builder.limit(200); // 200 records max
		
		List<Map<String, Object>> rs = builder.get();
		return rs;
	}*/
	
	private Long buildingId;
	
	public void setBuildingId(Long buildingId) {
		this.buildingId = buildingId;
	}
	
	public Long getBuildingId() {
		return this.buildingId;
	}
	
	private Long serviceId;
	
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	
	public Long getServiceId() {
		return this.serviceId;
	}
	
	private Long subMeterId;
	
	public void setSubMeterId(Long subMeterId) {
		this.subMeterId = subMeterId;
	}
	
	public Long getSubMeterId() {
		return this.subMeterId;
	}
	
	List<EnergyMeterContext> energyMeters;
	
	public void setEnergyMeters(List<EnergyMeterContext> energyMeters) {
		this.energyMeters = energyMeters;
	}
	
	public List<EnergyMeterContext> getEnergyMeters() {
		return this.energyMeters;
	}
	
	// get all sub meters of building
	public String getSubMeters() throws Exception {
		
		setEnergyMeters(DeviceAPI.getAllEnergyMeters(buildingId, serviceId));
		
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
	
	JSONObject dashboardMeta;
	
	public void setDashboardMeta(JSONObject dashboardMeta) {
		this.dashboardMeta = dashboardMeta;
	}
	
	public JSONObject getDashboardMeta() {
		return this.dashboardMeta;
	}
	
	public String updateDashboard() throws Exception {
		
		this.dashboard = new DashboardContext();
		this.dashboard.setId((Long) dashboardMeta.get("id"));
		this.dashboard.setDashboardName((String) dashboardMeta.get("dashboardName"));
		
		List dashboardWidgets = (List) dashboardMeta.get("dashboardWidgets");
		if (dashboardWidgets != null) {
			for (int i=0; i < dashboardWidgets.size(); i++) {
				Map widget = (Map) dashboardWidgets.get(i);
				Integer widgetType = DashboardWidgetContext.WidgetType.getWidgetType(widget.get("type").toString()).getValue();
				
				DashboardWidgetContext widgetContext = null;
				if (widgetType == DashboardWidgetContext.WidgetType.CHART.getValue()) {
					widgetContext = new WidgetChartContext();
				}
				else if (widgetType == DashboardWidgetContext.WidgetType.LIST_VIEW.getValue()) {
					widgetContext = new WidgetListViewContext();
				}
				else if (widgetType == DashboardWidgetContext.WidgetType.STATIC.getValue()) {
					widgetContext = new WidgetStaticContext();
				}
				else if (widgetType == DashboardWidgetContext.WidgetType.WEB.getValue()) {
					widgetContext = new WidgetWebContext();
				}
				
				widgetContext.setId((Long) widget.get("id"));
				widgetContext.setLayoutWidth(Integer.parseInt(widget.get("layoutWidth").toString()));
				widgetContext.setLayoutHeight(Integer.parseInt(widget.get("layoutHeight").toString()));
				widgetContext.setLayoutPosition(Integer.parseInt(widget.get("order").toString()));
				widgetContext.setxPosition(Integer.parseInt(widget.get("xPosition").toString()));
				widgetContext.setyPosition(Integer.parseInt(widget.get("yPosition").toString()));
				
				this.dashboard.addDashboardWidget(widgetContext);
			}
		}
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
		
		Chain updateDashboardChain = FacilioChainFactory.getUpdateDashboardChain();
		updateDashboardChain.execute(context);
		
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
		else if (widgetStaticContext != null) {
			context.put(FacilioConstants.ContextNames.WIDGET, widgetStaticContext);
			context.put(FacilioConstants.ContextNames.WIDGET_TYPE, DashboardWidgetContext.WidgetType.STATIC);
			context.put(FacilioConstants.ContextNames.DASHBOARD_ID, widgetStaticContext.getDashboardId());
			widgetStaticContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		}
		else if (widgetWebContext != null) {
			context.put(FacilioConstants.ContextNames.WIDGET, widgetWebContext);
			context.put(FacilioConstants.ContextNames.WIDGET_TYPE, DashboardWidgetContext.WidgetType.WEB);
			context.put(FacilioConstants.ContextNames.DASHBOARD_ID, widgetWebContext.getDashboardId());
			widgetWebContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
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
	
	private List<FacilioModule> modules;
	public List<FacilioModule> getModules() {
		return modules;
	}
	public void setModules(List<FacilioModule> modules) {
		this.modules = modules;
	}
	
	public String getSupportedModules() throws Exception {
		List<FacilioModule> supportedModules = new ArrayList<>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String[] mods = new String[]{"workorder", "alarm", "energydata"};
		for (String mod : mods) {
			supportedModules.add(modBean.getModule(mod));
		}
		setModules(supportedModules);
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
	
	private int type=1;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	private String fileUrl;
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String url) {
		this.fileUrl = url;
	}
	
	public String sendReportMail() throws Exception {
		
 		FacilioModule module = getModule();
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.CV_NAME, reportId.toString());
		context.put(FacilioConstants.ContextNames.PARENT_VIEW, "report");
		context.put(FacilioConstants.ContextNames.LIMIT_VALUE, -1);
		
		context.put(FacilioConstants.ContextNames.REPORT_CONTEXT, reportContext);
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, type);
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		
		Chain mailReportChain = ReportsChainFactory.getSendMailReportChain();
		mailReportChain.execute(context);
 		
 		return SUCCESS;
	}
	
	public String scheduleReport() throws Exception {
		
		emailTemplate.setName("Report");
		emailTemplate.setFrom("report@${org.domain}.facilio.com");
		
		// TODO...pojo
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.REPORT_ID, reportId);
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, type);
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.MAX_COUNT, maxCount);
		context.put(FacilioConstants.ContextNames.SCHEDULE_INFO, scheduleInfo);
 		
		Chain mailReportChain = ReportsChainFactory.getReportScheduleChain();
		mailReportChain.execute(context);
 		
 		return SUCCESS;
	}
	
	private ScheduleInfo scheduleInfo;
	public ScheduleInfo getScheduleInfo() {
		return scheduleInfo;
	}
	public void setScheduleInfo(ScheduleInfo scheduleInfo) {
		this.scheduleInfo = scheduleInfo;
	}
	
	private long startTime;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private int maxCount = -1;
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	private EMailTemplate emailTemplate;
	public EMailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EMailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	
}
