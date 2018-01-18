package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.collections.MultiMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardContext.DashboardPublishStatus;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.NumberAggregateOperator;
import com.facilio.bmsconsole.context.ReportContext1;
import com.facilio.bmsconsole.context.ReportCriteriaContext;
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
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
	private List<Map<String, Object>> relatedAlarms;
	
	public List<Map<String, Object>> getRelatedAlarms() {
		return relatedAlarms;
	}
	public void setRelatedAlarms(List<Map<String, Object>> releatedAlarms) {
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
	public ReportContext1 getReportContext() {
		return reportContext;
	}
	public void setReportContext(ReportContext1 reportContext) {
		this.reportContext = reportContext;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	private ReportFolderContext reportFolderContext;
	private ReportContext1 reportContext;
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
		
		List<FacilioField> fields = FieldFactory.getReportFields();
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReport().getTableName())
				.fields(fields);
		
		if (reportContext.getParentFolderId() == null || reportContext.getParentFolderId() < 0) {
			// if report parent folder not exists, mapping to default folder 
			ReportFolderContext defaultFolder = DashboardUtil.getDefaultReportFolder(moduleName);
			reportContext.setParentFolderId(defaultFolder.getId());
		}

		reportContext.setxAxis(DashboardUtil.addOrGetReportfield(reportContext.getxAxisField()).getId());
		if(reportContext.getY1AxisField() != null && reportContext.getY1AxisField().getModuleField() != null) {
			reportContext.setY1Axis(DashboardUtil.addOrGetReportfield(reportContext.getY1AxisField()).getId());
		}
		if(reportContext.getGroupByField() != null && reportContext.getGroupByField().getModuleField() != null) {
			reportContext.setGroupBy(DashboardUtil.addOrGetReportfield(reportContext.getGroupByField()).getId());
		}
		
		Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		reportContext.setId((Long) props.get("id"));
		if(reportContext.getCriteria() != null) {
			
			Long criteriaId = CriteriaAPI.addCriteria(reportContext.getCriteria(), AccountUtil.getCurrentOrg().getId());
			insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReportCriteria().getTableName())
					.fields(FieldFactory.getReportCriteriaFields());
			
			Map<String, Object> prop = new HashMap<String, Object>();
			prop.put("reportId", reportContext.getId());
			prop.put("criteriaId", criteriaId);
			insertBuilder.addRecord(prop).save();
		}
		if(reportContext.getReportUserFilters() != null) {
			for(ReportUserFilterContext userFilter : reportContext.getReportUserFilters()) {
				ReportFieldContext userFilterField = DashboardUtil.addOrGetReportfield(userFilter.getReportFieldContext());
				Map<String, Object> prop = new HashMap<String, Object>();
				prop.put("reportId", reportContext.getId());
				prop.put("reportFieldId", userFilterField.getId());
				prop.put("whereClause", DashboardUtil.getWhereClauseForUserFilter(userFilterField.getField()));
				
				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportUserFilter().getTableName())
						.fields(FieldFactory.getReportUserFilterFields());
				
				insertBuilder.addRecord(prop).save();
			}
		}
		if(reportContext.getReportThresholds() != null) {
			for(ReportThreshold threshhold : reportContext.getReportThresholds()) {
				
				Map<String, Object> prop = FieldUtil.getAsProperties(threshhold);
				prop.put("reportId", reportContext.getId());

				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportThreshold().getTableName())
						.fields(FieldFactory.getReportThresholdFields());
				
				insertBuilder.addRecord(prop).save();
			}
		}
		if(reportContext.getDateFilter() != null) {
			Map<String, Object> prop = FieldUtil.getAsProperties(reportContext.getDateFilter());
			prop.put("reportId", reportContext.getId());

			insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReportDateFilter().getTableName())
					.fields(FieldFactory.getReportDateFilterFields());
			
			insertBuilder.addRecord(prop).save();
		}
		return SUCCESS;
	}
	
	public String addReportFolder() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		reportFolderContext.setModuleId(module.getModuleId());
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReportFolder().getTableName())
				.fields(FieldFactory.getReportFolderFields());
		reportFolderContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		Map<String, Object> props = FieldUtil.getAsProperties(reportFolderContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		reportFolderContext.setId((Long) props.get("id"));
		
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
			props.put("chartType", ReportContext1.ReportChartType.getWidgetChartType(chartType).getValue());
			if (secChartType != null) {
				props.put("secChartType", ReportContext1.ReportChartType.getWidgetChartType(secChartType).getValue());
			}
			
			updateBuilder.update(props);
		}
		
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
		
		ReportFieldContext reportXAxisField = DashboardUtil.getReportField(reportContext.getxAxisField());
		reportContext.setxAxisField(reportXAxisField);
		FacilioField xAxisField = reportXAxisField.getField();
		
		boolean isEnergyDataWithTimeFrame = false;
		if(xAxisField.getDataTypeEnum().equals(FieldType.DATE_TIME) && module.getName().equals("energydata")) {
			isEnergyDataWithTimeFrame = true;
		}
		
		FacilioModule fieldModule = xAxisField.getExtendedModule();
		
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
			xAxisField = xAggregateOpperator.getSelectField(xAxisField);
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
		
//		if(userFilterValues == null) {
//			userFilterValues = new JSONObject();
//			userFilterValues.put("1", "21");
//			JSONArray secVall = new JSONArray();
//			secVall.add("1512086400000");
//			secVall.add("1514764799000");
//			userFilterValues.put("2", secVall);
//		}
		
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
			if(module.getName().equals("energydata") && criteria != null) {
				Map<Integer, Condition> conditions = criteria.getConditions();
				for(Condition condition:conditions.values()) {
					if(condition.getColumnName().equals("Energy_Data.PARENT_METER_ID")) {
						energyMeterValue = energyMeterValue + condition.getValue() +",";
					}
				}
			}
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
		List<Map<String, Object>> rs = builder.get();
		
		if(reportContext.getGroupBy() != null) {
			
			Multimap<Object, JSONObject> res = ArrayListMultimap.create();
			
			HashMap<String, Object> labelMapping = new HashMap<>();
			
			for(int i=0;i<rs.size();i++) {
	 			Map<String, Object> thisMap = rs.get(i);
	 			if(thisMap!=null) {
	 				
	 				JSONObject value = new JSONObject();
	 				value.put("label", thisMap.get("groupBy"));
	 				value.put("value", thisMap.get("value"));
	 				
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
			
			System.out.println("finalres -- "+finalres);
			setReportData(finalres);
		}
		else {
			if(!reportContext.getIsComparisionReport()) {
				JSONArray res = new JSONArray();
				for(int i=0;i<rs.size();i++) {
		 			Map<String, Object> thisMap = rs.get(i);
		 			JSONObject component = new JSONObject();
		 			if(thisMap!=null) {
		 				if(thisMap.get("label") == null) {
		 					continue;
		 				}
		 				if(thisMap.get("dummyField") != null) {
		 					component.put("label", thisMap.get("dummyField"));
		 				}
		 				else {
		 					component.put("label", thisMap.get("label"));
		 				}
		 				component.put("value", thisMap.get("value"));
		 				res.add(component);
		 			}
			 	}
				setReportData(res);
			}
		}
		
		System.out.println("rs after -- "+rs);
		List<List<Map<String, Object>>> comparisionRs = new ArrayList<>(); 
		comparisionRs.add(rs);
		Multimap<String,Object> comparisonresult = ArrayListMultimap.create();
		if(reportContext.getIsComparisionReport()) {
			
			for(int i=0;i<reportContext.getReportCriteriaContexts().size();i++) {
				if(i==0) {
					continue;
				}
				GenericSelectRecordBuilder builder1 = new GenericSelectRecordBuilder()
						.table(module.getTableName())
						.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
						.groupBy(groupByString)
						.select(fields);
				if(module.getExtendModule() != null) {
					builder.innerJoin(module.getExtendModule().getTableName())
						.on(module.getTableName()+".Id="+module.getExtendModule().getTableName()+".Id");
				}
				if(reportContext.getReportCriteriaContexts().get(i).getCriteriaId() != null) {
					criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), reportContext.getReportCriteriaContexts().get(i).getCriteriaId());
					builder1.andCriteria(criteria);
				}
				List<Map<String, Object>> rs1 = builder1.get();
				comparisionRs.add(rs1);
			}
			
			for(List<Map<String, Object>> comp :comparisionRs) {
				for(Map<String, Object> result:comp) {
					String label =(String) result.get("label");
					Object value = result.get("value");
					comparisonresult.put(label, value);
				}
			}
			System.out.println("comparisonresult ---- "+comparisonresult);
			
			JSONArray compResult =  new JSONArray();
			for(String key:comparisonresult.keySet()) {
				JSONObject json = new JSONObject();
				json.put("label", key);
				JSONArray valuesArray = new JSONArray();
				for(Object values :comparisonresult.get(key)) {
					JSONObject json1 = new JSONObject();
					json1.put("label", key);
					json1.put("value", values);
					valuesArray.add(json1);				
				}
				json.put("value", valuesArray);
				compResult.add(json);
			}
			System.out.println("compResult  ----- "+compResult);
			setReportData(compResult);
		}
		
		
		
		if(energyMeterValue != null && isEnergyDataWithTimeFrame) {
			
			List<FacilioField> alarmVsEnergyFields = new ArrayList<>();
			
			FacilioField label = new FacilioField();
			label.setName("label");
			label.setDataType(FieldType.NUMBER);
			label.setColumnName("CREATED_TIME");
			label.setModule(ModuleFactory.getAlarmsModule()); ////alarm vs energy data
			
			alarmVsEnergyFields.add(label);
			
			FacilioField value = new FacilioField();
			value.setName("value");
			value.setDataType(FieldType.NUMBER);
			value.setColumnName("ALARM_ID");
			value.setModule(ModuleFactory.getAlarmVsEnergyData());
			
			alarmVsEnergyFields.add(value);
			
			GenericSelectRecordBuilder builder1 = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getAlarmVsEnergyData().getTableName())
					.innerJoin(ModuleFactory.getAlarmsModule().getTableName())
					.on(ModuleFactory.getAlarmVsEnergyData().getTableName()+".ALARM_ID="+ModuleFactory.getAlarmsModule().getTableName()+".ID")
					.innerJoin(ModuleFactory.getTicketsModule().getTableName())
					.on(ModuleFactory.getTicketsModule().getTableName()+".ID="+ModuleFactory.getAlarmsModule().getTableName()+".ID")
					.andCustomWhere(ModuleFactory.getTicketsModule().getTableName()+".ASSET_ID in ("+energyMeterValue.substring(0, energyMeterValue.length()-1)+")")
					//.andCustomWhere(ModuleFactory.getAlarmsModule().getTableName()+".CREATED_TIME between ? and ?", values)
					.select(alarmVsEnergyFields);
			
			List<Map<String, Object>> alarmVsEnergyProps = builder1.get();
			setRelatedAlarms(alarmVsEnergyProps);
		}
		return SUCCESS;
	}
	
	private List<FacilioField> getDisplayColumns(String module) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> allFields = modBean.getAllFields(module);
		
		String[] displayFieldNames = null;
		
		if ("workorder".equals(module)) {
			displayFieldNames = new String[]{"subject", "category", "space", "assignedTo", "status", "priority", "createdTime", "dueDate"};
		}
		else if ("alarm".equals(module)) {
			displayFieldNames = new String[]{"subject", "severity", "node", "modifiedTime", "acknowledgedBy", "state"};
		}
		else if ("energydata".equals(module)) {
			displayFieldNames = new String[]{"subject", "category", "space", "assignedTo", "status", "priority", "createdTime", "dueDate"};
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
		
		reportContext = DashboardUtil.getReportContext(reportId);
		ReportFolderContext reportFolder = DashboardUtil.getReportFolderContext(reportContext.getParentFolderId());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(reportFolder.getModuleId());
		
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
		
		JSONArray result = new JSONArray();
		for(Map<String, Object> r:rs) {
			result.add(r);
		}
		setReportData(result);
		setDisplayFields(getDisplayColumns(module.getName()));
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
}
