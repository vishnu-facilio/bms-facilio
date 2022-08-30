package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.bmsconsoleV3.actions.dashboard.V3DashboardAPIHandler;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class AddDashboardWidgetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		
		if(dashboard != null) {
			List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();
			V3DashboardAPIHandler.checkAndGenerateWidgetLinkName(widgets, dashboard.getId(), null);
			if (widgets != null && widgets.size() > 0)  {
				for (int i = 0; i < widgets.size(); i++) {
					
					DashboardWidgetContext widget = widgets.get(i);
					
					widget.setId(-1l);
					
					FacilioChain addWidgetChain = TransactionChainFactory.getAddWidgetChain();

					widget.setDashboardId(dashboard.getId());
					context.put(FacilioConstants.ContextNames.WIDGET, widget);
					context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
					context.put(FacilioConstants.ContextNames.DASHBOARD_ID, dashboard.getId());
					addWidgetChain.execute(context);
					
					widget = (DashboardWidgetContext) context.get(FacilioConstants.ContextNames.WIDGET);
				}
			}
		}
		return false;
	}

}
