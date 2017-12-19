package com.facilio.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.DashboardWidgetPeriodContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.google.common.collect.Multimap;

public class DashboardUtil {
	
	public static List<DashboardWidgetContext> getDashboardWidgetsFormDashboardId(Long dashboardId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWidgetFields();
		fields.addAll(FieldFactory.getDashbaordVsWidgetFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getWidgetModule().getTableName())
				.innerJoin(ModuleFactory.getDashboardVsWidgetModule().getTableName())
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getDashboardVsWidgetModule().getTableName()+".WIDGET_ID")
				.andCustomWhere(ModuleFactory.getDashboardVsWidgetModule().getTableName()+".DASHBOARD_ID = ?", dashboardId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<DashboardWidgetContext> dashboardWidgetContexts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props) {
				DashboardWidgetContext dashboardWidgetContext = FieldUtil.getAsBeanFromMap(prop, DashboardWidgetContext.class);
				addWidgetPeriods(dashboardWidgetContext);
				dashboardWidgetContexts.add(dashboardWidgetContext);
			}
		}
		return dashboardWidgetContexts;
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
				DashboardWidgetPeriodContext dashboardWidgetPeriod = FieldUtil.getAsBeanFromMap(prop, DashboardWidgetPeriodContext.class);
				dashboardWidget.addPeriod(dashboardWidgetPeriod);
			}
		}
		
	}
	
	public static JSONArray getReportJson(Multimap<DashboardContext, DashboardWidgetContext> dashboardWidgets) {
		
		JSONArray result = new JSONArray();
		
		List<DashboardContext> dashboards = new ArrayList(dashboardWidgets.keySet());
		 
		Collections.sort(dashboards);
		 for(DashboardContext dashboard:dashboards) {
			 String dashboardName = dashboard.getDashboardName();
			 System.out.println("after ---"+dashboardName);
				Collection<DashboardWidgetContext> dashboardWidgetContexts = dashboardWidgets.get(dashboard);
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
}
