package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.constants.FacilioConstants;

public class GetConnectedAppWidgetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long connectedAppWidgetId = (long) context.get(FacilioConstants.ContextNames.ID);
		String widgetLinkName = (String) context.get(FacilioConstants.ContextNames.WIDGET_LINK_NAME);
		
		ConnectedAppWidgetContext connectedAppWidget = null;
		if (connectedAppWidgetId > 0) {
			connectedAppWidget = ConnectedAppAPI.getConnectedAppWidget(connectedAppWidgetId);
		}
		else if (widgetLinkName != null) {
			if (widgetLinkName.split("\\.").length == 2) {
				String connectedAppLink = widgetLinkName.split("\\.")[0];
				String widgetLink = widgetLinkName.split("\\.")[1];
				
				connectedAppWidget = ConnectedAppAPI.getConnectedAppWidget(connectedAppLink, widgetLink);
			}
		}
		if (connectedAppWidget != null) {
			ConnectedAppContext connectedApp = (ConnectedAppContext) ConnectedAppAPI.getConnectedApp(connectedAppWidget.getConnectedAppId());
		
			context.put(FacilioConstants.ContextNames.CONNECTED_APP, connectedApp);
			context.put(FacilioConstants.ContextNames.CONNECTED_APP_WIDGET, connectedAppWidget);
		}
		
		return false;
	}
}