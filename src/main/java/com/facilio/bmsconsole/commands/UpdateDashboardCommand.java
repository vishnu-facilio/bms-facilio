package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateDashboardCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboard != null) {			
			List<FacilioField> fields = FieldFactory.getDashboardFields();
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getDashboardModule().getTableName())
					.fields(fields)
					.andCustomWhere(ModuleFactory.getDashboardModule().getTableName()+".ID = ?", dashboard.getId());

			Map<String, Object> props = FieldUtil.getAsProperties(dashboard);
			updateBuilder.update(props);
			
			List<DashboardWidgetContext> existingWidgets = DashboardUtil.getDashboardWidgetsFormDashboardId(dashboard.getId());
			
			JSONObject widgetMapping = new JSONObject();
			
			List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();
			
			if (widgets != null && widgets.size() > 0)  {
				for (int i = 0; i < widgets.size(); i++) {
					
					DashboardWidgetContext widget = widgets.get(i);
					widgetMapping.put(widget.getId(), true);
					
					if(widget.getId() <= 0) {
						
						
						Chain addWidgetChain = FacilioChainFactory.getAddWidgetChain();

						widget.setDashboardId(dashboard.getId());
						context.put(FacilioConstants.ContextNames.WIDGET, widget);
						context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
						context.put(FacilioConstants.ContextNames.DASHBOARD_ID, dashboard.getId());
						addWidgetChain.execute(context);
						
						widget = (DashboardWidgetContext) context.get(FacilioConstants.ContextNames.WIDGET);
					}
				}
			}

			List<Long> removedWidgets = new ArrayList<Long>();
			for (int i = 0; i < existingWidgets.size(); i++) {
				if (!widgetMapping.containsKey(existingWidgets.get(i).getId())) {
					removedWidgets.add(existingWidgets.get(i).getId());
				}
			}
			
			List<DashboardWidgetContext> updatedWidgets = dashboard.getDashboardWidgets();
			if (updatedWidgets != null && updatedWidgets.size() > 0)  {
				for (int i = 0; i < updatedWidgets.size(); i++) {
					
					updateBuilder = new GenericUpdateRecordBuilder()
							.table(ModuleFactory.getDashboardVsWidgetModule().getTableName())
							.fields(FieldFactory.getDashbaordVsWidgetFields())
							.andCustomWhere(ModuleFactory.getDashboardVsWidgetModule().getTableName()+".DASHBOARD_ID = ? AND " + ModuleFactory.getDashboardVsWidgetModule().getTableName()+".WIDGET_ID = ?", dashboard.getId(), updatedWidgets.get(i).getId());

					Map<String, Object> props1 = FieldUtil.getAsProperties(updatedWidgets.get(i));
					
					updateBuilder.update(props1);
					
					if(updatedWidgets.get(i).getType().equals(DashboardWidgetContext.WidgetType.STATIC.getValue())) {
						
						updateBuilder = new GenericUpdateRecordBuilder()
								.table(ModuleFactory.getWidgetStaticModule().getTableName())
								.fields(FieldFactory.getWidgetStaticFields())
								.andCustomWhere(ModuleFactory.getWidgetStaticModule().getTableName()+".ID = ?",updatedWidgets.get(i).getId());

						
						System.out.println("dashboard update props --- "+props1);
						updateBuilder.update(props1);
					}
					
					updateBuilder = new GenericUpdateRecordBuilder()
							.table(ModuleFactory.getWidgetModule().getTableName())
							.fields(FieldFactory.getWidgetFields())
							.andCustomWhere(ModuleFactory.getWidgetModule().getTableName()+".ID = ?", updatedWidgets.get(i).getId());

					updateBuilder.update(props1);
				}
			}
			if(removedWidgets.size() > 0) {
				GenericDeleteRecordBuilder genericDeleteRecordBuilder = new GenericDeleteRecordBuilder();
				genericDeleteRecordBuilder.table(ModuleFactory.getDashboardVsWidgetModule().getTableName())
				.andCustomWhere(ModuleFactory.getDashboardVsWidgetModule().getTableName()+".DASHBOARD_ID = ?", dashboard.getId())
				.andCondition(CriteriaAPI.getCondition(ModuleFactory.getDashboardVsWidgetModule().getTableName()+".WIDGET_ID", "widgetId", StringUtils.join(removedWidgets, ","),StringOperators.IS));
				
				genericDeleteRecordBuilder.delete();
				
				genericDeleteRecordBuilder = new GenericDeleteRecordBuilder();
				genericDeleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(ModuleFactory.getWidgetModule().getTableName()+".ID", "ID", StringUtils.join(removedWidgets, ","),StringOperators.IS));
				
				genericDeleteRecordBuilder.delete();
			}
		}
		
		return false;
	}

}