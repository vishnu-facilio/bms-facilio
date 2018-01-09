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
import com.facilio.bmsconsole.context.ReportContext1;
import com.facilio.bmsconsole.context.ReportCriteriaContext;
import com.facilio.bmsconsole.context.ReportFieldContext;
import com.facilio.bmsconsole.context.ReportFolderContext;
import com.facilio.bmsconsole.context.ReportFormulaFieldContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetPeriodContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class DashboardUtil {
	
	
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
	
	public static List<DashboardWidgetContext> getDashboardWidgetsFormDashboardId(Long dashboardId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWidgetFields();
		fields.addAll(FieldFactory.getWidgetChartFields());
		fields.addAll(FieldFactory.getWidgetListViewFields());
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
				.andCustomWhere(ModuleFactory.getDashboardVsWidgetModule().getTableName()+".DASHBOARD_ID = ?", dashboardId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<DashboardWidgetContext> dashboardWidgetContexts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props) {
				WidgetType widgetType = WidgetType.getWidgetType((Integer) prop.get("type"));
				DashboardWidgetContext dashboardWidgetContext = (DashboardWidgetContext) FieldUtil.getAsBeanFromMap(prop, widgetType.getWidgetContextClass());
				addWidgetPeriods(dashboardWidgetContext);
				if(widgetType.equals(WidgetType.CHART)) {
					addWidgetConditiontoWidget((WidgetChartContext) dashboardWidgetContext);
				}
				dashboardWidgetContexts.add(dashboardWidgetContext);

			}
		}
		return dashboardWidgetContexts;
	}
	public static List<ReportContext1> getReportsFormReportFolderId(Long folderID) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFields())
				.table(ModuleFactory.getReport().getTableName())
				.andCustomWhere(ModuleFactory.getReport().getTableName()+".REPORT_FOLDER_ID = ?", folderID);
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<ReportContext1> reps;
		if (props != null && !props.isEmpty()) {
			reps = new ArrayList<>();
			for(Map<String, Object> prop:props) {
				ReportContext1 reportFolderContext = FieldUtil.getAsBeanFromMap(prop, ReportContext1.class);
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
			addWidgetConditiontoWidget(widgetReportContext);
			return widgetReportContext;
		}
		return null;
	}
	
	public static ReportContext1 getReportContext(Long reportId) throws Exception {
		
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
			ReportContext1 reportContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportContext1.class);
			
			for(Map<String, Object> prop:props) {
				ReportCriteriaContext reportCriteriaContext = FieldUtil.getAsBeanFromMap(prop, ReportCriteriaContext.class);
				reportContext.addReportCriteriaContext(reportCriteriaContext);
			}
			return reportContext;
		}
		return null;
	}
	public static ReportFieldContext getReportField(Long fieldId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getReportFieldFields();
		fields.addAll(FieldFactory.getReportFormulaFieldFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getReportField().getTableName())
				.leftJoin(ModuleFactory.getReportFormulaField().getTableName())
				.on(ModuleFactory.getReportField().getTableName()+".FORMULA_FIELD_ID="+ModuleFactory.getReportFormulaField().getTableName()+".ID")
				.andCustomWhere(ModuleFactory.getReportField().getTableName()+".ID = ?", fieldId);
		
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
	public static WidgetPeriodContext getWidgetPeriod(Long widgetId,String periodValue) throws Exception {
		
//		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//				.select(FieldFactory.getDashboardWidgetPeriodFields())
//				.table(ModuleFactory.getWidgetPeriodModule().getTableName())
//				.andCustomWhere(ModuleFactory.getWidgetPeriodModule().getTableName()+".WIDGET_ID = ?", widgetId)
//				.andCustomWhere(ModuleFactory.getWidgetPeriodModule().getTableName()+".PERIOD_VALUE = ?", periodValue);
//		
//		List<Map<String, Object>> props = selectBuilder.get();
//		if (props != null && !props.isEmpty()) {
//			WidgetPeriodContext widgetperiodContext = FieldUtil.getAsBeanFromMap(props.get(0), WidgetPeriodContext.class);
//			return widgetperiodContext;
//		}
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
	public static void addWidgetPeriods(DashboardWidgetContext dashboardWidget) throws Exception {
		
//		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//				.select(FieldFactory.getDashboardWidgetPeriodFields())
//				.table(ModuleFactory.getWidgetPeriodModule().getTableName())
//				.andCustomWhere("WIDGET_ID = ?", dashboardWidget.getId());
//		
//		List<Map<String, Object>> props = selectBuilder.get();
//		
//		if (props != null && !props.isEmpty()) {
//			for(Map<String, Object> prop:props) {
//				WidgetPeriodContext dashboardWidgetPeriod = FieldUtil.getAsBeanFromMap(prop, WidgetPeriodContext.class);
//				dashboardWidget.addPeriod(dashboardWidgetPeriod);
//			}
//		}
		
	}
	
	public static void addWidgetConditiontoWidget(WidgetChartContext widgetChartContext) throws Exception {
//		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//				.select(FieldFactory.getWidgetConditionFields())
//				.table(ModuleFactory.getWidgetCondition().getTableName())
//				.andCustomWhere("WIDGET_ID = ?", widgetChartContext.getId());
//		
//		List<Map<String, Object>> props = selectBuilder.get();
//		
//		if (props != null && !props.isEmpty()) {
//			for(Map<String, Object> prop:props) {
//				ReportCriteriaContext widgetConditionContext = FieldUtil.getAsBeanFromMap(prop, ReportCriteriaContext.class);
//				widgetChartContext.addWidgetCondition(widgetConditionContext);
//			}
//		}
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
			Collection<ReportContext1> reportContexts = reportFolder.getReports();
			JSONArray childrenArray = new JSONArray();
			for(ReportContext1 reportContext:reportContexts) {
				childrenArray.add(reportContext.widgetJsonObject());
			}
			JSONObject dashboardJson = new JSONObject();
			dashboardJson.put("label", name);
			dashboardJson.put("children", childrenArray);
			result.add(dashboardJson);
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
}
