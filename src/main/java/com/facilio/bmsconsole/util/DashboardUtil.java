package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetConditionContext;
import com.facilio.bmsconsole.context.WidgetPeriodContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
			
			FacilioField selectField = formulaContext.getAggregateOperator().getSelectField(formulaContext.getSelectFieldId());
			
			List<FacilioField> selectFields = new ArrayList<>();
			selectFields.add(selectField);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
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
		fields.addAll(FieldFactory.getDashbaordVsWidgetFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getWidgetModule().getTableName())
				.innerJoin(ModuleFactory.getDashboardVsWidgetModule().getTableName())
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getDashboardVsWidgetModule().getTableName()+".WIDGET_ID")
				.innerJoin(ModuleFactory.getWidgetChartModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetChartModule().getTableName()+".ID")
				.andCustomWhere(ModuleFactory.getDashboardVsWidgetModule().getTableName()+".DASHBOARD_ID = ?", dashboardId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<DashboardWidgetContext> dashboardWidgetContexts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props) {
				if(prop.get("type").equals("chart")) {
					WidgetChartContext dashboardWidgetContext = FieldUtil.getAsBeanFromMap(prop, WidgetChartContext.class);
					addWidgetPeriods(dashboardWidgetContext);
					addWidgetConditiontoWidget(dashboardWidgetContext);
					dashboardWidgetContexts.add(dashboardWidgetContext);
				}
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
			addWidgetConditiontoWidget(widgetReportContext);
			return widgetReportContext;
		}
		return null;
	}
	public static WidgetPeriodContext getWidgetPeriod(Long widgetId,String periodValue) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardWidgetPeriodFields())
				.table(ModuleFactory.getWidgetPeriodModule().getTableName())
				.andCustomWhere(ModuleFactory.getWidgetPeriodModule().getTableName()+".WIDGET_ID = ?", widgetId)
				.andCustomWhere(ModuleFactory.getWidgetPeriodModule().getTableName()+".PERIOD_VALUE = ?", periodValue);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			WidgetPeriodContext widgetperiodContext = FieldUtil.getAsBeanFromMap(props.get(0), WidgetPeriodContext.class);
			return widgetperiodContext;
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
	public static void addWidgetPeriods(DashboardWidgetContext dashboardWidget) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardWidgetPeriodFields())
				.table(ModuleFactory.getWidgetPeriodModule().getTableName())
				.andCustomWhere("WIDGET_ID = ?", dashboardWidget.getId());
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props) {
				WidgetPeriodContext dashboardWidgetPeriod = FieldUtil.getAsBeanFromMap(prop, WidgetPeriodContext.class);
				dashboardWidget.addPeriod(dashboardWidgetPeriod);
			}
		}
		
	}
	
	public static void addWidgetConditiontoWidget(WidgetChartContext widgetChartContext) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWidgetConditionFields())
				.table(ModuleFactory.getWidgetCondition().getTableName())
				.andCustomWhere("WIDGET_ID = ?", widgetChartContext.getId());
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props) {
				WidgetConditionContext widgetConditionContext = FieldUtil.getAsBeanFromMap(prop, WidgetConditionContext.class);
				widgetChartContext.addWidgetCondition(widgetConditionContext);
			}
		}
	}
	public static JSONArray getDashboardResponseJson(DashboardContext dashboard) {
		List dashboards = new ArrayList<>();
		dashboards.add(dashboard);
		return getDashboardResponseJson(dashboards);
	}
	public static JSONArray getDashboardResponseJson(List<DashboardContext> dashboards) {
		
		JSONArray result = new JSONArray();
		
		Collections.sort(dashboards);
		 for(DashboardContext dashboard:dashboards) {
			 String dashboardName = dashboard.getDashboardName();
			Collection<DashboardWidgetContext> dashboardWidgetContexts = dashboard.getDashboardWidgets();
			JSONArray childrenArray = new JSONArray();
			for(DashboardWidgetContext dashboardWidgetContext:dashboardWidgetContexts) {
				childrenArray.add(dashboardWidgetContext.getWidgetJsonObject());
			}
			JSONObject dashboardJson = new JSONObject();
			dashboardJson.put("label", dashboardName);
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
			
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			List<DashboardWidgetContext> dashbaordWidgets = DashboardUtil.getDashboardWidgetsFormDashboardId(dashboard.getId());
			dashboard.setDashboardWidgets(dashbaordWidgets);
			return dashboard;
		}
		return null;
	}
}
