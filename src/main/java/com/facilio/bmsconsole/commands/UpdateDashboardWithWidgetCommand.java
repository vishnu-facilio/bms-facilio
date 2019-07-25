package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WidgetVsWorkflowContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateDashboardWithWidgetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboard != null) {
			
			DashboardUtil.updateDashboard(dashboard);
			
			List<DashboardWidgetContext> existingWidgets = DashboardUtil.getDashboardWidgetsFormDashboardIdOrTabId(dashboard.getId(),null);
			
			JSONObject widgetMapping = new JSONObject();
			
			List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();
			
			if (widgets != null && widgets.size() > 0)  {
				for (int i = 0; i < widgets.size(); i++) {
					
					DashboardWidgetContext widget = widgets.get(i);
					widgetMapping.put(widget.getId(), true);
					
					if(widget.getId() <= 0) {
						
						
						Chain addWidgetChain = TransactionChainFactory.getAddWidgetChain();

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
				if (!widgetMapping.containsKey(existingWidgets.get(i)
						.getId())) {
					removedWidgets.add(existingWidgets.get(i).getId());
				}
			}
			GenericUpdateRecordBuilder updateBuilder = null;
			GenericUpdateRecordBuilder updateWidgetChart = null;
			List<DashboardWidgetContext> updatedWidgets = dashboard.getDashboardWidgets();
			if (updatedWidgets != null && updatedWidgets.size() > 0)  {
				for (int i = 0; i < updatedWidgets.size(); i++) {
					
					DashboardWidgetContext updatewidget = updatedWidgets.get(i);
					
					updateBuilder = new GenericUpdateRecordBuilder()
							.table(ModuleFactory.getWidgetModule().getTableName())
							.fields(FieldFactory.getWidgetFields())
							.andCustomWhere(ModuleFactory.getWidgetModule().getTableName()+".DASHBOARD_ID = ? AND " + ModuleFactory.getWidgetModule().getTableName()+".ID = ?", dashboard.getId(), updatewidget.getId());

					Long widgetId = updatewidget.getId();
					
					updatewidget.setOrgId(AccountUtil.getCurrentOrg().getId());
					if(updatewidget.getType() == DashboardWidgetContext.WidgetType.CHART.getValue()) {
						updateWidgetChart = new GenericUpdateRecordBuilder()
								.table(ModuleFactory.getWidgetChartModule().getTableName())
								.fields(FieldFactory.getWidgetChartFields())
								.andCustomWhere(ModuleFactory.getWidgetChartModule().getTableName()+".ID= ?", widgetId);
						Map<String,Object> props = FieldUtil.getAsProperties(updatewidget);
						updateWidgetChart.update(props);
					}
					
					
					Map<String, Object> props1 = FieldUtil.getAsProperties(updatewidget);
					
					updateBuilder.update(props1);
					
					if(updatewidget.getType().equals(DashboardWidgetContext.WidgetType.STATIC.getValue())) {
						
						updateBuilder = new GenericUpdateRecordBuilder()
								.table(ModuleFactory.getWidgetStaticModule().getTableName())
								.fields(FieldFactory.getWidgetStaticFields())
								.andCustomWhere(ModuleFactory.getWidgetStaticModule().getTableName()+".ID = ?",updatewidget.getId());

						updateBuilder.update(props1);
						
						if (updatewidget.getWidgetVsWorkflowContexts() != null && !updatewidget.getWidgetVsWorkflowContexts().isEmpty()) {	
							DashboardUtil.deleteWidgetVsWorkflowContext(updatewidget.getId());
							for(WidgetVsWorkflowContext widgetVsWorkflowContext :updatewidget.getWidgetVsWorkflowContexts()) {
								widgetVsWorkflowContext.setWidgetId(updatewidget.getId());
								DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
							}
						}
					}
				}
			}
			if(removedWidgets.size() > 0) {
				GenericDeleteRecordBuilder genericDeleteRecordBuilder = new GenericDeleteRecordBuilder();
				genericDeleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(ModuleFactory.getWidgetModule().getTableName()+".ID", "ID", StringUtils.join(removedWidgets, ","),StringOperators.IS));
				
				genericDeleteRecordBuilder.delete();
			}
		}
		
		return false;
	}

}