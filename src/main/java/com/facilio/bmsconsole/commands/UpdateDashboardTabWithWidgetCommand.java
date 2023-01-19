package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsoleV3.actions.dashboard.V3DashboardAPIHandler;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateDashboardTabWithWidgetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		// TODO Auto-generated method stub
		DashboardTabContext dashboardTabContext = (DashboardTabContext) context.get(FacilioConstants.ContextNames.DASHBOARD_TAB);
		DashboardContext dashboardContext = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboardTabContext != null) {
			if (dashboardContext != null && dashboardContext.getId() > 0) {
				DashboardUtil.updateDashboard(dashboardContext);
			}
			
			DashboardUtil.updateDashboardTab(dashboardTabContext);
			List<DashboardWidgetContext> existingWidgets = DashboardUtil.getDashboardWidgetsFormDashboardIdOrTabId(null,dashboardTabContext.getId());
			
			JSONObject widgetMapping = new JSONObject();
			
			List<DashboardWidgetContext> widgets = dashboardTabContext.getDashboardWidgets();
			V3DashboardAPIHandler.checkAndGenerateWidgetLinkName(widgets,  null, dashboardTabContext.getId());

			if (widgets != null && widgets.size() > 0)  {
				for (int i = 0; i < widgets.size(); i++) {
					
					DashboardWidgetContext widget = widgets.get(i);
					widgetMapping.put(widget.getId(), true);
					
					if(widget.getId() <= 0) {
						
						
						FacilioChain addWidgetChain = TransactionChainFactory.getAddWidgetChain();

						widget.setDashboardTabId(dashboardTabContext.getId());
						context.put(FacilioConstants.ContextNames.WIDGET, widget);
						context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
						addWidgetChain.execute(context);
						
						widget = (DashboardWidgetContext) context.get(FacilioConstants.ContextNames.WIDGET);
					}
				}
			}
			
			
			List<DashboardWidgetContext> updatedWidgets = dashboardTabContext.getDashboardWidgets();
			
			FacilioChain updateWidgetChain = TransactionChainFactory.getUpdateWidgetsChain();
			
			context.put(FacilioConstants.ContextNames.WIDGET_UPDATE_LIST,updatedWidgets);

			updateWidgetChain.execute(context);
			

			List<Long> removedWidgets = new ArrayList<Long>();
			for (int i = 0; i < existingWidgets.size(); i++) {
				if (!widgetMapping.containsKey(existingWidgets.get(i).getId())) {
					removedWidgets.add(existingWidgets.get(i).getId());
				}
			}
			Boolean isFromReport = (Boolean) context.get(FacilioConstants.ContextNames.IS_FROM_REPORT);
			if(!(isFromReport != null && isFromReport) && removedWidgets.size() > 0) {
				GenericDeleteRecordBuilder genericDeleteRecordBuilder = new GenericDeleteRecordBuilder();
				genericDeleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(ModuleFactory.getWidgetModule().getTableName()+".ID", "ID", StringUtils.join(removedWidgets, ","),StringOperators.IS));
				
				genericDeleteRecordBuilder.delete();
			}
		}
		
		return false;
	}

}
