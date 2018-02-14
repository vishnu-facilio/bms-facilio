package com.facilio.bmsconsole.actions;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReportsChainFactory;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardContext.DashboardPublishStatus;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.NumberAggregateOperator;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportEnergyMeterContext;
import com.facilio.bmsconsole.context.ReportFieldContext;
import com.facilio.bmsconsole.context.ReportFolderContext;
import com.facilio.bmsconsole.context.ReportThreshold;
import com.facilio.bmsconsole.context.ReportUserFilterContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetListViewContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.pdf.PdfUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.executor.ScheduleInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
	
	String dateFilter;
	public String getDateFilter() {
		return dateFilter;
	}
	
	public void setDateFilter(String dateFilter) {
		this.dateFilter = dateFilter;
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
		
		if (reportContext.getParentFolderId() == null || reportContext.getParentFolderId() < 0) {
			// if report parent folder not exists, mapping to default folder 
			ReportFolderContext defaultFolder = DashboardUtil.getDefaultReportFolder(moduleName);
			reportContext.setParentFolderId(defaultFolder.getId());
		}

		DashboardUtil.addReport(reportContext);
		
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
		ReportFolderContext reportFolder = DashboardUtil.getReportFolderContext(reportContext.getParentFolderId());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(reportFolder.getModuleId());
		
//		FacilioContext context = new FacilioContext();
//		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
//		context.put(FacilioConstants.ContextNames.MODULE, module);
//		context.put(FacilioConstants.ContextNames.REPORT_DATE_FILTER, dateFilter);
//		Chain getRelatedAlarms = FacilioChainFactory.getRelatedAlarmForReports();
//		getRelatedAlarms.execute(context);
		return SUCCESS;
	}
	
	public String getData() throws Exception {
		
		if (reportContext == null) {
			reportContext = DashboardUtil.getReportContext(reportId);
			// generate preview report
		}
		
		ReportFolderContext reportFolder = DashboardUtil.getReportFolderContext(reportContext.getParentFolderId());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(reportFolder.getModuleId());
		
//		FacilioContext context = new FacilioContext();
//		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
//		context.put(FacilioConstants.ContextNames.MODULE, module);
//		context.put(FacilioConstants.ContextNames.REPORT_DATE_FILTER, dateFilter);
//		context.put(FacilioConstants.ContextNames.REPORT_USER_FILTER_VALUE, userFilterValues);
//		Chain addDashboardChain = FacilioChainFactory.getReportData();
//		addDashboardChain.execute(context);
		
		ReportFieldContext reportXAxisField = DashboardUtil.getReportField(reportContext.getxAxisField());
		reportContext.setxAxisField(reportXAxisField);
		FacilioField xAxisField = reportXAxisField.getField();
		
		boolean isEnergyDataWithTimeFrame = false;
		if(xAxisField.getColumnName().contains("TTIME") && module.getName().equals("energydata")) {
			isEnergyDataWithTimeFrame = true;
		}
		
		List<FacilioField> fields = new ArrayList<>();
		if(xAxisField.getDataTypeEnum().equals(FieldType.DATE_TIME)) {
			FacilioField dummyField = new FacilioField();
			dummyField.setColumnName(xAxisField.getColumnName());
			dummyField = NumberAggregateOperator.MAX.getSelectField(dummyField);
			dummyField.setName("dummyField");
			fields.add(dummyField);
		}
		
		FacilioField y1AxisField = null;
		ReportFieldContext reportY1AxisField;
		AggregateOperator xAggregateOpperator = reportContext.getXAxisAggregateOpperator();
		if(!xAggregateOpperator.getValue().equals(NumberAggregateOperator.COUNT.getValue())) {
			if (this.dateFilter != null || reportContext.getDateFilter() != null) {
				int oprId = (this.dateFilter != null) ? Integer.parseInt(this.dateFilter) : reportContext.getDateFilter().getOperatorId();
				if (oprId == DateOperators.TODAY.getOperatorId() || oprId == DateOperators.YESTERDAY.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.HOURSOFDAY;
				}
				if (oprId == DateOperators.CURRENT_WEEK.getOperatorId() || oprId == DateOperators.LAST_WEEK.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.FULLDATE;
				}
				else if (oprId == DateOperators.CURRENT_MONTH.getOperatorId() || oprId == DateOperators.LAST_MONTH.getOperatorId()) {
					xAggregateOpperator = FormulaContext.DateAggregateOperator.FULLDATE;
				}
				reportContext.setxAxisaggregateFunction(xAggregateOpperator.getValue());
			}
			if (!reportContext.getIsHighResolutionReport()) {
				xAxisField = xAggregateOpperator.getSelectField(xAxisField);
			}
		}
		if(reportContext.getY1Axis() != null) {
			reportY1AxisField = DashboardUtil.getReportField(reportContext.getY1AxisField());
			AggregateOperator y1AggregateOpperator = reportContext.getY1AxisAggregateOpperator();
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
		reportContext.setY1AxisField(reportY1AxisField);
		fields.add(y1AxisField);
		fields.add(xAxisField);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId());
		if ("WorkOrders".equals(module.getTableName())){
			builder.leftJoin("PM_To_WO").on("WorkOrders.ID=WO_ID");
		}
		
		
		if(userFilterValues != null && reportContext.getReportUserFilters() != null) {
			for(ReportUserFilterContext userFilter : reportContext.getReportUserFilters()) {
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
		if(reportContext.getGroupBy() != null) {
			ReportFieldContext reportGroupByField = DashboardUtil.getReportField(reportContext.getGroupByField());
			reportContext.setGroupByField(reportGroupByField);
			FacilioField groupByField = reportGroupByField.getField();
			groupByField.setName("groupBy");
			fields.add(groupByField);
			builder.groupBy("groupBy");
			groupByString = groupByString + ",groupBy";
		}
			
		builder.select(fields);
		builder.groupBy(groupByString);
		
		if(module.getExtendModule() != null) {
			builder.innerJoin(module.getExtendModule().getTableName())
				.on(module.getTableName()+".Id="+module.getExtendModule().getTableName()+".Id");
		}
		if(reportContext.getLimit() != null) {
			builder.limit(reportContext.getLimit());
		}
		if(reportContext.getOrderBy() != null) {
			if(reportContext.getOrderByFunc() != null) {
				builder.orderBy(reportContext.getOrderBy() +" "+reportContext.getOrderByFunc().getStringValue());
			}
			else {
				builder.orderBy(reportContext.getOrderBy());
			}
			
		}
		Criteria criteria = null;
		String energyMeterValue = "";
		if (reportContext.getReportCriteriaContexts() != null) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), reportContext.getReportCriteriaContexts().get(0).getCriteriaId());
			builder.andCriteria(criteria);
		}
		
		Condition dateCondition = null;
		if (reportContext.getDateFilter() != null) {
			dateCondition = new Condition();
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
		JSONObject buildingVsMeter = new JSONObject();
		Map<Long,Double> buildingVsArea = null;
		HashMap <Long, ArrayList<Long>> purposeVsMeter= new HashMap<Long,ArrayList<Long>>();
		JSONObject purposeVsMeter1 = new JSONObject();
		if (getEnergyMeterFilter() != null) {
			reportContext.setEnergyMeter(getEnergyMeterFilter());
		}
		Condition buildingCondition = null;
		if (reportContext.getEnergyMeter() != null) {
			if (reportContext.getEnergyMeter().getSubMeterId() != null) {
				energyMeterValue = reportContext.getEnergyMeter().getSubMeterId() + "";
				buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", reportContext.getEnergyMeter().getSubMeterId()+"", NumberOperators.EQUALS);
			}
			else if (reportContext.getEnergyMeter().getBuildingId() != null) {
				if ("service".equalsIgnoreCase(reportContext.getEnergyMeter().getGroupBy())) {
					
					List<EnergyMeterContext> meters = DeviceAPI.getRootServiceMeters(reportContext.getEnergyMeter().getBuildingId()+"");
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
					
					reportContext.setGroupByField(groupByReportField);
					
					reportContext.setGroupBy(-1L);
				}
				else {
					List<EnergyMeterContext> meters = DeviceAPI.getMainEnergyMeter(reportContext.getEnergyMeter().getBuildingId()+"");
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
			else if (reportContext.getEnergyMeter().getGroupBy() != null && "building".equalsIgnoreCase(reportContext.getEnergyMeter().getGroupBy())) {
				
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
					
					reportContext.setGroupByField(groupByReportField);
					
					reportContext.setGroupBy(-1L);
				}
			}
			else if (reportContext.getEnergyMeter().getGroupBy() != null && "service".equalsIgnoreCase(reportContext.getEnergyMeter().getGroupBy())) {
				
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
					
					reportContext.setGroupByField(groupByReportField);
					
					reportContext.setGroupBy(-1L);
				}
				else {
					xAxisField.setDisplayName("Service");
				}
			}
		}
		if(buildingCondition != null) {
			builder.andCondition(buildingCondition);
		}
		List<Map<String, Object>> rs = builder.get();
		System.out.println("builder --- "+builder);
		System.out.println("rs1 -- "+rs);
		
		if(reportContext.getGroupBy() != null) {
			
			Multimap<Object, JSONObject> res = ArrayListMultimap.create();
			
			HashMap<String, Object> labelMapping = new HashMap<>();
			
			HashMap<String, Object> purposeMapping = new HashMap<>();
			for(int i=0;i<rs.size();i++) {
	 			Map<String, Object> thisMap = rs.get(i);
	 			if(thisMap!=null) {
	 				
	 				if (reportContext.getEnergyMeter() != null && reportContext.getEnergyMeter().getGroupBy() != null && (reportContext.getEnergyMeter().getBuildingId() == null || reportContext.getEnergyMeter().getBuildingId() <= 0) && "service".equalsIgnoreCase(reportContext.getEnergyMeter().getGroupBy())) {
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
		 				if ("cost".equalsIgnoreCase(reportContext.getY1AxisUnit())) {
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
			
			System.out.println("finalres -- "+finalres);
			setReportData(finalres);
		}
		else {
			if(!reportContext.getIsComparisionReport()) {
				JSONArray res = new JSONArray();
				JSONObject purposeIndexMapping = new JSONObject();
				for(int i=0;i<rs.size();i++) {
					boolean newPurpose = false;
		 			Map<String, Object> thisMap = rs.get(i);
		 			JSONObject component = new JSONObject();
		 			if(thisMap!=null) {
//		 				if(thisMap.get("label") == null) {
//		 					continue;
//		 				}
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
		 					if ("cost".equalsIgnoreCase(reportContext.getY1AxisUnit())) {
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
		 					if ("cost".equalsIgnoreCase(reportContext.getY1AxisUnit())) {
		 						Double d = (Double) thisMap.get("value");
		 						component.put("value", d*ReportsUtil.unitCost);
		 						component.put("orig_value", d);
		 					}
		 					else if ("eui".equalsIgnoreCase(reportContext.getY1AxisUnit())) {
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
				setReportData(res);
				System.out.println("res -- "+res);
			}
		}
		
		System.out.println("rs after -- "+rs);
		List<List<Map<String, Object>>> comparisionRs = new ArrayList<>();
//		comparisionRs.add(rs);
		Multimap<String,Map<String, Object>> comparisonresult = ArrayListMultimap.create();
		if(reportContext.getIsComparisionReport()) {
			
			for(int i=0;i<reportContext.getReportCriteriaContexts().size();i++) {
				
				DateOperators dd = null;
				if(reportContext.getReportCriteriaContexts().get(i).getCriteriaId() != null) {
					Criteria criteria1 = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), reportContext.getReportCriteriaContexts().get(i).getCriteriaId());
					Map<Integer, Condition> conditions = criteria1.getConditions();
					for(Integer s:conditions.keySet()) {
						Condition condi = conditions.get(s);
						if(condi.getColumnName().equals("Energy_Data.TTIME") && condi.getOperator() instanceof DateOperators) {
							dd = (DateOperators) condi.getOperator();
						}
					}
				}
//				if(i==0) {
//					comparisionRs.add(rs);
//					continue;
//				}
				List<FacilioField> fields1 = new ArrayList<>(); 
				fields1.addAll(fields);
				if(dd != null) {
					FacilioField ff = new FacilioField();
					if(dd.getOperatorId() == 43) {
						ff.setColumnName("\"Today\"");
					}
					else if(dd.getOperatorId() == 28) {
						ff.setColumnName("\"This Month\"");
					}
					else if(dd.getOperatorId() == 31) {
						ff.setColumnName("\"This Week\"");
					}
					else {
						ff.setColumnName("\""+dd.getOperator()+"\"");
					}
					ff.setName("dateOpperator");
					fields1.add(ff);
				}
				GenericSelectRecordBuilder builder1 = new GenericSelectRecordBuilder()
						.table(module.getTableName())
						.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
						.groupBy(groupByString)
						.select(fields1);
				if(module.getExtendModule() != null) {
					builder.innerJoin(module.getExtendModule().getTableName())
						.on(module.getTableName()+".Id="+module.getExtendModule().getTableName()+".Id");
				}
				if(reportContext.getReportCriteriaContexts().get(i).getCriteriaId() != null) {
					criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), reportContext.getReportCriteriaContexts().get(i).getCriteriaId());
					builder1.andCriteria(criteria);
				}
				if(buildingCondition != null) {
					builder1.andCondition(buildingCondition);
				}
				List<Map<String, Object>> rs1 = builder1.get();
				System.out.println("builder1 --- "+builder1);
				System.out.println("rs comp  "+i+" -- "+rs1);
				comparisionRs.add(rs1);
			}
			
			for(List<Map<String, Object>> comp :comparisionRs) {
				for(Map<String, Object> result:comp) {
					String label =(String) result.get("label");
					comparisonresult.put(label, result);
				}
			}
			System.out.println("comparisonresult ---- "+comparisonresult);
			
			JSONArray compResult =  new JSONArray();
			for(String key:comparisonresult.keySet()) {
				JSONObject json = new JSONObject();
				JSONArray valuesArray = new JSONArray();
				for(Map<String, Object> result :comparisonresult.get(key)) {
					json.put("label", result.get("dummyField"));
					JSONObject json1 = new JSONObject();
					json1.put("label", result.get("dateOpperator"));
					json1.put("value", result.get("value"));
					valuesArray.add(json1);			
				}
				json.put("value", valuesArray);
				compResult.add(json);
			}
			System.out.println("compResult  ----- "+compResult);
			setReportData(compResult);
		}
		
		if (reportContext.getReportCriteriaContexts() != null) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), reportContext.getReportCriteriaContexts().get(0).getCriteriaId());
			if(module.getName().equals("energydata") && criteria != null) {
				Map<Integer, Condition> conditions = criteria.getConditions();
				for(Condition condition:conditions.values()) {
					if(condition.getColumnName().equals("Energy_Data.PARENT_METER_ID")) {
						energyMeterValue = energyMeterValue + condition.getValue() +",";
					}
				}
			}
		}
		
		if(energyMeterValue != null && !"".equalsIgnoreCase(energyMeterValue.trim()) && isEnergyDataWithTimeFrame && !reportContext.getIsComparisionReport()) {
			
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
				builder1.andCondition(dateCondition);
			}
			
			List<Map<String, Object>> alarmVsEnergyProps = builder1.get();
			Map<Object ,JSONArray> alarmProps=  getAlarmProps(alarmVsEnergyProps);
			JSONArray relatedAlarms = getAlarmReturnFormat(alarmProps);
			setRelatedAlarms(relatedAlarms);
		}
		return SUCCESS;
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
		List<ModuleBaseWithCustomFields> records = getRawData(context, module);
		FacilioView view= (FacilioView)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		FileFormat fileFormat = FileFormat.getFileFormat(type);
		if(fileFormat == FileFormat.PDF) {
			fileUrl = PdfUtil.exportUrlAsPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(), AwsUtil.getConfig("clientapp.url")+"/app/wo/reports/view/"+reportId);
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
		
		context.put(FacilioConstants.ContextNames.REPORT_CONTEXT, reportContext);
		context.put(FacilioConstants.ContextNames.DATE_FILTER, dateFilter);
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, type);
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		
		Chain mailReportChain = ReportsChainFactory.getSendMailReportChain();
		mailReportChain.execute(context);
 		
 		return SUCCESS;
	}
	
	public String scheduleReport() throws Exception {
		
		emailTemplate.setFrom("support@${org.orgDomain}.facilio.com");
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.REPORT_ID, reportId);
		context.put(FacilioConstants.ContextNames.DATE_FILTER, dateFilter);
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, type);
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
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
	
	private EMailTemplate emailTemplate;
	public EMailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EMailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	
}
