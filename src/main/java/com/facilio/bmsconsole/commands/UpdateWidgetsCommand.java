package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WidgetVsWorkflowContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateWidgetsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		
		List<DashboardWidgetContext> updatedWidgets = (List<DashboardWidgetContext>) context.get(FacilioConstants.ContextNames.WIDGET_UPDATE_LIST);
		
		GenericUpdateRecordBuilder updateBuilder = null;
		GenericUpdateRecordBuilder updateWidgetChart = null;
		if (updatedWidgets != null && updatedWidgets.size() > 0)  {
			for (DashboardWidgetContext updatewidget : updatedWidgets) {
				
				if(updatewidget.getId() > 0) {
					updateBuilder = new GenericUpdateRecordBuilder()
							.table(ModuleFactory.getWidgetModule().getTableName())
							.fields(FieldFactory.getWidgetFields())
							.andCondition(CriteriaAPI.getIdCondition(updatewidget.getId(), ModuleFactory.getWidgetModule()));

					Long widgetId = updatewidget.getId();
					
					updatewidget.setOrgId(AccountUtil.getCurrentOrg().getId());
					if(updatewidget.getType() == DashboardWidgetContext.WidgetType.CHART.getValue()) {
						updateWidgetChart = new GenericUpdateRecordBuilder()
								.table(ModuleFactory.getWidgetChartModule().getTableName())
								.fields(FieldFactory.getWidgetChartFields())
								.andCondition(CriteriaAPI.getIdCondition(updatewidget.getId(), ModuleFactory.getWidgetChartModule()));
						Map<String,Object> props = FieldUtil.getAsProperties(updatewidget);
						updateWidgetChart.update(props);
					}
					
					
					Map<String, Object> props1 = FieldUtil.getAsProperties(updatewidget);
					
					updateBuilder.update(props1);
					
					if(updatewidget.getType().equals(DashboardWidgetContext.WidgetType.STATIC.getValue())) {
						
						updateBuilder = new GenericUpdateRecordBuilder()
								.table(ModuleFactory.getWidgetStaticModule().getTableName())
								.fields(FieldFactory.getWidgetStaticFields())
								.andCondition(CriteriaAPI.getIdCondition(updatewidget.getId(), ModuleFactory.getWidgetStaticModule()));

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
		}
		return false;
	}

}
