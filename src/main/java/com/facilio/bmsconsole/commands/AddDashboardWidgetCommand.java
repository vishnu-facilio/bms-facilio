package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.constants.FacilioConstants;

public class AddDashboardWidgetCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		
		if(dashboard != null) {
			List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();
			
			if (widgets != null && widgets.size() > 0)  {
				for (int i = 0; i < widgets.size(); i++) {
					
					DashboardWidgetContext widget = widgets.get(i);
					
					widget.setId(-1l);
					
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
		return false;
	}

}
