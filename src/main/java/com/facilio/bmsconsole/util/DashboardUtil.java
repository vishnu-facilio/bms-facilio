package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext.WidgetType;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportCriteriaContext;
import com.facilio.bmsconsole.context.ReportDateFilterContext;
import com.facilio.bmsconsole.context.ReportEnergyMeterContext;
import com.facilio.bmsconsole.context.ReportFieldContext;
import com.facilio.bmsconsole.context.ReportFolderContext;
import com.facilio.bmsconsole.context.ReportFormulaFieldContext;
import com.facilio.bmsconsole.context.ReportThreshold;
import com.facilio.bmsconsole.context.ReportUserFilterContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class DashboardUtil {
	
	public static JSONObject getStandardVariance(List<Map<String, Object>> props) {
		Double min = null ,max = null,avg = null,sum = (double) 0;
		for(Map<String, Object> prop:props) {
			double value = (double) prop.get("value");
			
			sum = sum + value;
			
			if(min == null && max == null) {
				min = value;
				max = value;
			}
			else {
				min = min < value ? min : value;
				max = max > value ? max : value;
			}
		}
		avg = sum / props.size();
		JSONObject variance = new JSONObject();
		variance.put("min", min);
		variance.put("max", max);
		variance.put("avg", avg);
		variance.put("sum", sum);
		return variance;
	}
	
	public static List<DashboardWidgetContext> getDashboardWidgetsFormDashboardId(Long dashboardId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWidgetFields();
		fields.addAll(FieldFactory.getWidgetChartFields());
		fields.addAll(FieldFactory.getWidgetListViewFields());
		fields.addAll(FieldFactory.getWidgetStaticFields());
		fields.addAll(FieldFactory.getWidgetWebFields());
		fields.addAll(FieldFactory.getDashbaordVsWidgetFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getWidgetModule().getTableName())
				.innerJoin(ModuleFactory.getDashboardVsWidgetModule().getTableName())
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getDashboardVsWidgetModule().getTableName()+".WIDGET_ID")
				.leftJoin(ModuleFactory.getWidgetChartModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetChartModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetListViewModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetListViewModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetStaticModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetStaticModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetWebModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetWebModule().getTableName()+".ID")
				.andCustomWhere(ModuleFactory.getDashboardVsWidgetModule().getTableName()+".DASHBOARD_ID = ?", dashboardId);
		
		selectBuilder.orderBy(ModuleFactory.getDashboardVsWidgetModule().getTableName() + ".LAYOUT_POSITION");
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<DashboardWidgetContext> dashboardWidgetContexts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props) {
				WidgetType widgetType = WidgetType.getWidgetType((Integer) prop.get("type"));
				DashboardWidgetContext dashboardWidgetContext = (DashboardWidgetContext) FieldUtil.getAsBeanFromMap(prop, widgetType.getWidgetContextClass());
				dashboardWidgetContexts.add(dashboardWidgetContext);
			}
		}
		return dashboardWidgetContexts;
	}
	public static WidgetChartContext getWidgetChartContext(Long reportId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWidgetChartFields();
		fields.addAll(FieldFactory.getWidgetFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getWidgetChartModule().getTableName())
				.innerJoin(ModuleFactory.getWidgetModule().getTableName())
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetChartModule().getTableName()+".ID")
				.andCustomWhere(ModuleFactory.getWidgetChartModule().getTableName()+".ID = ?", reportId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		System.out.println("111."+props);
		if (props != null && !props.isEmpty()) {
			WidgetChartContext widgetReportContext = FieldUtil.getAsBeanFromMap(props.get(0), WidgetChartContext.class);
			return widgetReportContext;
		}
		return null;
	}
	
	public static boolean updateDashboardPublishStatus(DashboardContext dashboard) throws Exception {
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getDashboardModule().getTableName())
				.fields(FieldFactory.getDashboardFields())
				.andCustomWhere("ID = ?", dashboard.getId());

		Map<String, Object> props = FieldUtil.getAsProperties(dashboard);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	public static boolean updateDashboardLinkName(long dashboardId, String linkName) throws Exception {
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getDashboardModule().getTableName())
				.fields(FieldFactory.getDashboardFields())
				.andCustomWhere("ID = ?", dashboardId);

		Map<String, Object> props = new HashMap<>();
		props.put("linkName", linkName);
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	public static JSONArray getDashboardResponseJson(DashboardContext dashboard) {
		List dashboards = new ArrayList<>();
		dashboards.add(dashboard);
		return getDashboardResponseJson(dashboards);
	}
	
	public static JSONArray getDashboardResponseJson(List<DashboardContext> dashboards) {
		
		JSONArray result = new JSONArray();
		
//		Collections.sort(dashboards);
		 for(DashboardContext dashboard:dashboards) {
			 String dashboardName = dashboard.getDashboardName();
			Collection<DashboardWidgetContext> dashboardWidgetContexts = dashboard.getDashboardWidgets();
			JSONArray childrenArray = new JSONArray();
			for(DashboardWidgetContext dashboardWidgetContext:dashboardWidgetContexts) {
				childrenArray.add(dashboardWidgetContext.widgetJsonObject());
			}
			JSONObject dashboardJson = new JSONObject();
			dashboardJson.put("id", dashboard.getId());
			dashboardJson.put("label", dashboardName);
			dashboardJson.put("linkName", dashboard.getLinkName());
			dashboardJson.put("children", childrenArray);
			result.add(dashboardJson);
		 }
		return result;
	}
	public static JSONArray getReportResponseJson(List<ReportFolderContext> reportFolders) {
		
		JSONArray result = new JSONArray();
		
//		Collections.sort(dashboards);
		 for(ReportFolderContext reportFolder:reportFolders) {
			 String name = reportFolder.getName();
			Collection<ReportContext> reportContexts = reportFolder.getReports();
			JSONArray childrenArray = new JSONArray();
			if(reportContexts != null) {
				for(ReportContext reportContext:reportContexts) {
					childrenArray.add(reportContext.widgetJsonObject());
				}
				JSONObject dashboardJson = new JSONObject();
				dashboardJson.put("id", reportFolder.getId());
				dashboardJson.put("label", name);
				dashboardJson.put("children", childrenArray);
				result.add(dashboardJson);
			}
			else {
				JSONObject dashboardJson = new JSONObject();
				dashboardJson.put("id", reportFolder.getId());
				dashboardJson.put("label", name);
				dashboardJson.put("children", childrenArray);
				result.add(dashboardJson);
			}
		 }
		return result;
	}
	
	public static DashboardContext getDashboardWithWidgets(Long dashboardId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("ID = ?", dashboardId);
		
		Long orgId = AccountUtil.getCurrentOrg().getOrgId();
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			List<DashboardWidgetContext> dashbaordWidgets = DashboardUtil.getDashboardWidgetsFormDashboardId(dashboard.getId());
			dashboard.setDashboardWidgets(dashbaordWidgets);
			return dashboard;
		}
		return null;
	}

	public static DashboardContext getDashboardWithWidgets(String dashboardLinkName) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("LINK_NAME = ?", dashboardLinkName);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			List<DashboardWidgetContext> dashbaordWidgets = DashboardUtil.getDashboardWidgetsFormDashboardId(dashboard.getId());
			dashboard.setDashboardWidgets(dashbaordWidgets);
			return dashboard;
		}
		return null;
	}

	public static List<DashboardContext> getDashboardList(String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("MODULEID = ?", module.getModuleId());
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			List<DashboardContext> dashboardList = new ArrayList<DashboardContext>();
			for (Map<String, Object> prop : props) {
				DashboardContext dashboard = FieldUtil.getAsBeanFromMap(prop, DashboardContext.class);
				dashboardList.add(dashboard);
			}
			return dashboardList;
		}
		return null;
	}
	
	public static String getWhereClauseForUserFilter(FacilioField field) {
		if(field.getDataTypeEnum().equals(FieldType.STRING)) {
			return field.getColumnName() +" = ?";
		}
		else {
			return field.getColumnName() +" between ? and ?";
		}
	}
	
	public static Object getFormulaValue(Long formulaId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFields())
				.table(ModuleFactory.getFormulaModule().getTableName())
				.andCustomWhere(ModuleFactory.getFormulaModule().getTableName()+".ID = ?", formulaId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		FormulaContext formulaContext = null;
		if (props != null && !props.isEmpty()) {
			formulaContext = FieldUtil.getAsBeanFromMap(props.get(0), FormulaContext.class);
		}
		
		if(formulaContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField selectField = formulaContext.getAggregateOperator().getSelectField(modBean.getField(formulaContext.getSelectFieldId()));
			
			List<FacilioField> selectFields = new ArrayList<>();
			selectFields.add(selectField);
			GenericSelectRecordBuilder selectValueBuilder = new GenericSelectRecordBuilder()
					.select(selectFields)
					.table(modBean.getModule(formulaContext.getModuleId()).getTableName());
			
			if(formulaContext.getCriteriaId() != null) {
				Criteria criteria = CriteriaAPI.getCriteria(formulaContext.getOrgId(), formulaContext.getCriteriaId());
				if(criteria != null) {
					selectValueBuilder.andCriteria(criteria);
				}
			}
			List<Map<String, Object>> rs = selectValueBuilder.get();
			if (rs != null && !rs.isEmpty()) {
				Object result = rs.get(0).get("value");
				return result;
			}
		}
		return null;
	}
	
	public static String getTimeFrameFloorValue(Operator dateOperator,String value) {
		
		switch(dateOperator.getOperatorId()) {
		
		case 30: 
		case 31:
		case 32:
			return "FLOOR("+value+"/(1000*60*60*24))";
		default:
			return null;
		}
 	}
	
	public static List<ReportContext> getReportsFormReportFolderId(Long folderID) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFields())
				.table(ModuleFactory.getReport().getTableName());
		
		if (folderID != null && folderID > 0) {
			selectBuilder.andCustomWhere(ModuleFactory.getReport().getTableName()+".REPORT_FOLDER_ID = ?", folderID);
		}
		else {
			selectBuilder.andCustomWhere("(" + ModuleFactory.getReport().getTableName() + ".REPORT_FOLDER_ID IS NULL OR " + ModuleFactory.getReport().getTableName() + ".REPORT_FOLDER_ID = -1)");
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<ReportContext> reps;
		if (props != null && !props.isEmpty()) {
			reps = new ArrayList<>();
			for(Map<String, Object> prop:props) {
				ReportContext reportFolderContext = FieldUtil.getAsBeanFromMap(prop, ReportContext.class);
				reps.add(reportFolderContext);
			}
			return reps;
		}
		return null;
	}
	public static ReportFolderContext getReportFolderContext(Long ReportFolderId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFolderFields())
				.table(ModuleFactory.getReportFolder().getTableName())
				.andCustomWhere(ModuleFactory.getReportFolder().getTableName()+".ID = ?", ReportFolderId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			ReportFolderContext reportFolderContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFolderContext.class);
			return reportFolderContext;
		}
		return null;
	}
	public static ReportFolderContext getDefaultReportFolder(String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFolderFields())
				.table(ModuleFactory.getReportFolder().getTableName())
				.andCustomWhere("MODULEID = ?", module.getModuleId())
				.andCustomWhere(ModuleFactory.getReportFolder().getTableName()+".NAME = ?", "Default");
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			ReportFolderContext reportFolderContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFolderContext.class);
			return reportFolderContext;
		}
		else {
			ReportFolderContext reportFolderContext = new ReportFolderContext();
			reportFolderContext.setOrgId(AccountUtil.getCurrentOrg().getId());
			reportFolderContext.setModuleId(module.getModuleId());
			reportFolderContext.setName("Default");
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReportFolder().getTableName())
					.fields(FieldFactory.getReportFolderFields());
			
			Map<String, Object> props1 = FieldUtil.getAsProperties(reportFolderContext);
			insertBuilder.addRecord(props1);
			insertBuilder.save();
			
			long folderId = (Long) props1.get("id");
			reportFolderContext.setId(folderId);
			return reportFolderContext;
		}
	}
	public static ReportFolderContext getBuildingReportFolder(String moduleName, long buildingId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFolderFields())
				.table(ModuleFactory.getReportFolder().getTableName())
				.andCustomWhere("ORGID = ? AND MODULEID = ? AND BUILDING_ID = ?", AccountUtil.getCurrentOrg().getId(), module.getModuleId(), buildingId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			ReportFolderContext reportFolderContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFolderContext.class);
			return reportFolderContext;
		}
		return null;
	}
	
	public static ReportContext getReportContext(Long reportId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getReportFields();
		fields.addAll(FieldFactory.getReportCriteriaFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getReport().getTableName())
				.leftJoin(ModuleFactory.getReportCriteria().getTableName())
				.on(ModuleFactory.getReport().getTableName()+".ID="+ModuleFactory.getReportCriteria().getTableName()+".REPORT_ID")
				.andCustomWhere(ModuleFactory.getReport().getTableName()+".ID = ?", reportId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ReportContext reportContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportContext.class);
			
			for(Map<String, Object> prop:props) {
				if(prop.get("criteriaId") != null) {
					ReportCriteriaContext reportCriteriaContext = FieldUtil.getAsBeanFromMap(prop, ReportCriteriaContext.class);
					reportContext.addReportCriteriaContext(reportCriteriaContext);
				}
			}
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportThresholdFields())
					.table(ModuleFactory.getReportThreshold().getTableName())
					.andCustomWhere(ModuleFactory.getReportThreshold().getTableName()+".REPORT_ID = ?", reportId);
			
			List<Map<String, Object>> thresholdProps = selectBuilder.get();
			if(thresholdProps != null && !thresholdProps.isEmpty()) {
				for(Map<String, Object> thresholdProp:thresholdProps) {
					ReportThreshold reportThreshold = FieldUtil.getAsBeanFromMap(thresholdProp, ReportThreshold.class);
					reportContext.addReportThreshold(reportThreshold);
				}
			}
			
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportUserFilterFields())
					.table(ModuleFactory.getReportUserFilter().getTableName())
					.andCustomWhere(ModuleFactory.getReportUserFilter().getTableName()+".REPORT_ID = ?", reportId);
			
			List<Map<String, Object>> userFilterProps = selectBuilder.get();
			if(userFilterProps != null && !userFilterProps.isEmpty()) {
				for(Map<String, Object> userFilterProp:userFilterProps) {
					ReportUserFilterContext reportUserFilterContext = FieldUtil.getAsBeanFromMap(userFilterProp, ReportUserFilterContext.class);
					
					ReportFieldContext reportField = new ReportFieldContext();
					reportField.setId(reportUserFilterContext.getReportFieldId());
					
					reportUserFilterContext.setReportFieldContext(getReportField(reportField));
					
					reportContext.addReportUserFilter(reportUserFilterContext);
				}
			}
			
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportDateFilterFields())
					.table(ModuleFactory.getReportDateFilter().getTableName())
					.andCustomWhere(ModuleFactory.getReportDateFilter().getTableName()+".REPORT_ID = ?", reportId);
			
			List<Map<String, Object>> dateFilterProps = selectBuilder.get();
			if (dateFilterProps != null && !dateFilterProps.isEmpty()) {
				ReportDateFilterContext dateFilterContext = FieldUtil.getAsBeanFromMap(dateFilterProps.get(0), ReportDateFilterContext.class);
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField ff = modBean.getField(dateFilterContext.getFieldId());
				dateFilterContext.setField(ff);
				
				reportContext.setDateFilter(dateFilterContext);
			}
			
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportEnergyMeterFields())
					.table(ModuleFactory.getReportEnergyMeter().getTableName())
					.andCustomWhere(ModuleFactory.getReportEnergyMeter().getTableName()+".REPORT_ID = ?", reportId);
			
			List<Map<String, Object>> energyMeterProps = selectBuilder.get();
			if (energyMeterProps != null && !energyMeterProps.isEmpty()) {
				ReportEnergyMeterContext energyMeterContext = FieldUtil.getAsBeanFromMap(energyMeterProps.get(0), ReportEnergyMeterContext.class);
				reportContext.setEnergyMeter(energyMeterContext);
			}
			
			reportContext.setBaseLineContexts(BaseLineAPI.getBaseLinesOfReport(reportId));
			return reportContext;
		}
		return null;
	}
	public static ReportFieldContext getReportField(ReportFieldContext reportField) throws Exception {
		
		if (reportField.getId() == null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField ff = modBean.getField(reportField.getModuleFieldId());
			reportField.setModuleField(ff);
			return reportField;
		}
		else {
			List<FacilioField> fields = FieldFactory.getReportFieldFields();
			fields.addAll(FieldFactory.getReportFormulaFieldFields());
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(ModuleFactory.getReportField().getTableName())
					.leftJoin(ModuleFactory.getReportFormulaField().getTableName())
					.on(ModuleFactory.getReportField().getTableName()+".FORMULA_FIELD_ID="+ModuleFactory.getReportFormulaField().getTableName()+".ID")
					.andCustomWhere(ModuleFactory.getReportField().getTableName()+".ID = ?", reportField.getId());
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				ReportFieldContext reportFieldContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFieldContext.class);
				
				if(reportFieldContext != null && reportFieldContext.getIsFormulaField()) {
					ReportFormulaFieldContext reportFormulaFieldContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFormulaFieldContext.class);
					reportFieldContext.setReportFormulaContext(reportFormulaFieldContext);
				}
				return reportFieldContext;
			}
			return null;
		}
	}
	public static ReportFieldContext addOrGetReportfield(ReportFieldContext reportFieldContext) throws Exception {
		
		String where;
		if(reportFieldContext.getIsFormulaField()) {
			where = ModuleFactory.getReportField().getTableName()+".FORMULA_FIELD_ID = "+reportFieldContext.getFormulaFieldId();
		}
		else {
			where = ModuleFactory.getReportField().getTableName()+".MODULE_FIELD_ID = "+reportFieldContext.getModuleFieldId();
		}
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFieldFields())
				.table(ModuleFactory.getReportField().getTableName())
				.leftJoin(ModuleFactory.getReportFormulaField().getTableName())
				.on(ModuleFactory.getReportField().getTableName()+".FORMULA_FIELD_ID="+ModuleFactory.getReportFormulaField().getTableName()+".ID")
				.andCustomWhere(where);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			reportFieldContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFieldContext.class);
			
			if(reportFieldContext != null && reportFieldContext.getIsFormulaField()) {
				ReportFormulaFieldContext reportFormulaFieldContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFormulaFieldContext.class);
				reportFieldContext.setReportFormulaContext(reportFormulaFieldContext);
			}
			return reportFieldContext;
		}
		else {
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReportField().getTableName())
					.fields(FieldFactory.getReportFieldFields());

			Map<String, Object> prop = FieldUtil.getAsProperties(reportFieldContext);
			insertBuilder.addRecord(prop);
			insertBuilder.save();

			reportFieldContext.setId((Long) prop.get("id"));
			
			return reportFieldContext;
		}
	}
	
	public static boolean addReportFolder(ReportFolderContext reportFolder) throws Exception {
		
		if (reportFolder != null) {
			reportFolder.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReportFolder().getTableName())
					.fields(FieldFactory.getReportFolderFields());
			
			Map<String, Object> props = FieldUtil.getAsProperties(reportFolder);
			insertBuilder.addRecord(props);
			insertBuilder.save();
			
			reportFolder.setId((Long) props.get("id"));
			return true;
		}
		return false;
	}
	
	public static boolean addReport(ReportContext reportContext) throws Exception {
		
		if (reportContext != null) {
			List<FacilioField> fields = FieldFactory.getReportFields();
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReport().getTableName())
					.fields(fields);

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
			if(reportContext.getEnergyMeter() != null) {
				Map<String, Object> prop = FieldUtil.getAsProperties(reportContext.getEnergyMeter());
				prop.put("reportId", reportContext.getId());

				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportEnergyMeter().getTableName())
						.fields(FieldFactory.getReportEnergyMeterFields());
				
				insertBuilder.addRecord(prop).save();
			}
			return true;
		}
		return false;
	}
	
	public static boolean populateBuildingEnergyReports(long buildingId, String buildingName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("energydata");
		
		if (getBuildingReportFolder(module.getName(), buildingId) != null) {
			// already building report folder exists...
			return true;
		}
		
		FacilioField ttimeFld = modBean.getField("ttime", module.getName());
		
		// create building folder
		ReportFolderContext buildingFolder = new ReportFolderContext();
		buildingFolder.setName(buildingName + " Reports");
		buildingFolder.setModuleId(module.getModuleId());
		buildingFolder.setBuildingId(buildingId);
		
		DashboardUtil.addReportFolder(buildingFolder);
		
		// High-res data report
		ReportContext highResReport = new ReportContext();
		highResReport.setName("Electricity Demand Today");
		highResReport.setDescription("Daily Electricity Load in High-Resolution");
		highResReport.setParentFolderId(buildingFolder.getId());
		highResReport.setChartType(ReportContext.ReportChartType.AREA.getValue());
		
		ReportFieldContext xAxisFld = new ReportFieldContext();
		xAxisFld.setModuleFieldId(ttimeFld.getId());
		highResReport.setxAxisField(xAxisFld);
		highResReport.setxAxisaggregateFunction(FormulaContext.DateAggregateOperator.ACTUAL.getValue());
		highResReport.setxAxisLabel("Time");
		
		ReportFieldContext y1AxisFld = new ReportFieldContext();
		y1AxisFld.setModuleFieldId(modBean.getField("totalEnergyConsumptionDelta", module.getName()).getId());
		highResReport.setY1AxisField(y1AxisFld);
		highResReport.setY1AxisaggregateFunction(FormulaContext.NumberAggregateOperator.SUM.getValue());
		highResReport.setY1AxisLabel("Energy Consumption");
		highResReport.setY1AxisUnit("kw");
		
		ReportEnergyMeterContext energyMeterFilter = new ReportEnergyMeterContext();
		energyMeterFilter.setBuildingId(buildingId);
		highResReport.setEnergyMeter(energyMeterFilter);
		
		ReportDateFilterContext dateFilter = new ReportDateFilterContext();
		dateFilter.setFieldId(ttimeFld.getId());
		dateFilter.setOperatorId(DateOperators.TODAY.getOperatorId());
		highResReport.setDateFilter(dateFilter);
		
		DashboardUtil.addReport(highResReport);
		
		// End Use breakdown
		ReportContext endUseBreakdown = new ReportContext();
		endUseBreakdown.setName("End Use Breakdown");
		endUseBreakdown.setDescription("End Use Breakdown");
		endUseBreakdown.setParentFolderId(buildingFolder.getId());
		endUseBreakdown.setChartType(ReportContext.ReportChartType.STACKED_BAR.getValue());
		
		ReportFieldContext endUseXAxisFld = new ReportFieldContext();
		endUseXAxisFld.setModuleFieldId(ttimeFld.getId());
		endUseBreakdown.setxAxisField(endUseXAxisFld);
		endUseBreakdown.setxAxisaggregateFunction(FormulaContext.DateAggregateOperator.FULLDATE.getValue());
		endUseBreakdown.setxAxisLabel("Date");
		
		ReportFieldContext endUseY1AxisFld = new ReportFieldContext();
		endUseY1AxisFld.setModuleFieldId(modBean.getField("totalEnergyConsumptionDelta", module.getName()).getId());
		endUseBreakdown.setY1AxisField(endUseY1AxisFld);
		endUseBreakdown.setY1AxisaggregateFunction(FormulaContext.NumberAggregateOperator.SUM.getValue());
		endUseBreakdown.setY1AxisLabel("Energy Consumption");
		endUseBreakdown.setY1AxisUnit("kwh");
		
		ReportEnergyMeterContext endUseEnergyMeterFilter = new ReportEnergyMeterContext();
		endUseEnergyMeterFilter.setBuildingId(buildingId);
		endUseEnergyMeterFilter.setGroupBy("service");
		endUseBreakdown.setEnergyMeter(endUseEnergyMeterFilter);
		
		ReportDateFilterContext endUseDateFilter = new ReportDateFilterContext();
		endUseDateFilter.setFieldId(ttimeFld.getId());
		endUseDateFilter.setOperatorId(DateOperators.CURRENT_MONTH.getOperatorId());
		endUseBreakdown.setDateFilter(endUseDateFilter);
		
		DashboardUtil.addReport(endUseBreakdown);
		
		// Cost usage by End use
		ReportContext costUseBreakdown = new ReportContext();
		costUseBreakdown.setName("Cost usage by End use");
		costUseBreakdown.setDescription("End Use Breakdown");
		costUseBreakdown.setParentFolderId(buildingFolder.getId());
		costUseBreakdown.setChartType(ReportContext.ReportChartType.STACKED_BAR.getValue());
		
		ReportFieldContext costUseXAxisFld = new ReportFieldContext();
		costUseXAxisFld.setModuleFieldId(ttimeFld.getId());
		costUseBreakdown.setxAxisField(costUseXAxisFld);
		costUseBreakdown.setxAxisaggregateFunction(FormulaContext.DateAggregateOperator.FULLDATE.getValue());
		costUseBreakdown.setxAxisLabel("Date");
		
		ReportFieldContext costUseY1AxisFld = new ReportFieldContext();
		costUseY1AxisFld.setModuleFieldId(modBean.getField("totalEnergyConsumptionDelta", module.getName()).getId());
		costUseBreakdown.setY1AxisField(costUseY1AxisFld);
		costUseBreakdown.setY1AxisaggregateFunction(FormulaContext.NumberAggregateOperator.SUM.getValue());
		costUseBreakdown.setY1AxisLabel("Cost Usage");
		costUseBreakdown.setY1AxisUnit("cost");
		
		ReportEnergyMeterContext costUseEnergyMeterFilter = new ReportEnergyMeterContext();
		costUseEnergyMeterFilter.setBuildingId(buildingId);
		costUseEnergyMeterFilter.setGroupBy("service");
		costUseBreakdown.setEnergyMeter(costUseEnergyMeterFilter);
		
		ReportDateFilterContext costUseDateFilter = new ReportDateFilterContext();
		costUseDateFilter.setFieldId(ttimeFld.getId());
		costUseDateFilter.setOperatorId(DateOperators.CURRENT_MONTH.getOperatorId());
		costUseBreakdown.setDateFilter(costUseDateFilter);
		
		DashboardUtil.addReport(costUseBreakdown);
		
		// Daily Energy breakdown
		ReportContext dailyBreakdown = new ReportContext();
		dailyBreakdown.setName("Daily energy breakdown");
		dailyBreakdown.setDescription("Daily energy breakdown");
		dailyBreakdown.setParentFolderId(buildingFolder.getId());
		dailyBreakdown.setChartType(ReportContext.ReportChartType.BAR.getValue());
		
		ReportFieldContext dailyXAxisFld = new ReportFieldContext();
		dailyXAxisFld.setModuleFieldId(ttimeFld.getId());
		dailyBreakdown.setxAxisField(dailyXAxisFld);
		dailyBreakdown.setxAxisaggregateFunction(FormulaContext.DateAggregateOperator.FULLDATE.getValue());
		dailyBreakdown.setxAxisLabel("Date");
		
		ReportFieldContext dailyY1AxisFld = new ReportFieldContext();
		dailyY1AxisFld.setModuleFieldId(modBean.getField("totalEnergyConsumptionDelta", module.getName()).getId());
		dailyBreakdown.setY1AxisField(dailyY1AxisFld);
		dailyBreakdown.setY1AxisaggregateFunction(FormulaContext.NumberAggregateOperator.SUM.getValue());
		dailyBreakdown.setY1AxisLabel("Energy Consumption");
		dailyBreakdown.setY1AxisUnit("kwh");
		
		ReportEnergyMeterContext dailyEnergyMeterFilter = new ReportEnergyMeterContext();
		dailyEnergyMeterFilter.setBuildingId(buildingId);
		dailyBreakdown.setEnergyMeter(dailyEnergyMeterFilter);
		
		ReportDateFilterContext dailyDateFilter = new ReportDateFilterContext();
		dailyDateFilter.setFieldId(ttimeFld.getId());
		dailyDateFilter.setOperatorId(DateOperators.CURRENT_MONTH.getOperatorId());
		dailyBreakdown.setDateFilter(dailyDateFilter);
		
		DashboardUtil.addReport(dailyBreakdown);
		
		return true;
	}
public static List<Long> getDataSendingMeters(Long orgid) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("energydata");
		
		FacilioField field = new FacilioField();
		field.setColumnName("DISTINCT PARENT_METER_ID");
		field.setName("parentId");
		
		ArrayList<FacilioField> selectFields = new ArrayList<FacilioField>();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table(module.getTableName())
				.andCustomWhere("ORGID = ?",orgid);
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<Long> meterIds = null;
		if (props != null && !props.isEmpty()) {
			meterIds = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				meterIds.add((Long) prop.get("parentId"));
			}
		}
		return meterIds;
	}
	public static final long ONE_MIN_MILLIS = 60000l;
	public static int predictDateOpperator(JSONArray dateFilter) {
	
	long diff = (Long) dateFilter.get(1) - (Long) dateFilter.get(0);
	
	if(diff <= ONE_MIN_MILLIS * 60 * 24) {
		return DateOperators.TODAY.getOperatorId();
	}
	else if (diff <= ONE_MIN_MILLIS * 60 * 24 * 7) {
		return DateOperators.CURRENT_WEEK.getOperatorId();
	}
	else if (diff <= ONE_MIN_MILLIS * 60 * 24 * 31) {
		return DateOperators.CURRENT_MONTH.getOperatorId();
	}
	else {
		return DateOperators.CURRENT_YEAR.getOperatorId();
	}
	
}
	
	public static Condition getDateCondition(ReportContext reportContext, JSONArray dateFilter, FacilioModule module) {
		Condition dateCondition = new Condition();
		dateCondition.setField(reportContext.getDateFilter().getField());
		
		if (dateFilter != null) {
			if (dateFilter.size() > 1) {

				dateCondition.setOperator(DateOperators.BETWEEN);
				long fromValue = (long)dateFilter.get(0);
				long toValue = (long)dateFilter.get(1);
				if(module.getName().equals("energydata") && toValue > DateTimeUtil.getCurrenTime()) {
					toValue = DateTimeUtil.getCurrenTime();
				}
				dateCondition.setValue(fromValue+","+toValue);
			}
		}
		else {
			dateCondition.setOperatorId(reportContext.getDateFilter().getOperatorId());
		}
		return dateCondition;
	}
}
