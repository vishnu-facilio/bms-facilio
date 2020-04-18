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
		
		ConnectedAppWidgetContext connectedAppWidget = ConnectedAppAPI.getConnectedAppWidget(connectedAppWidgetId);
		if (connectedAppWidget != null) {
			ConnectedAppContext connectedApp = (ConnectedAppContext) ConnectedAppAPI.getConnectedApp(connectedAppWidget.getConnectedAppId());
		
			context.put(FacilioConstants.ContextNames.CONNECTED_APP, connectedApp);
			context.put(FacilioConstants.ContextNames.CONNECTED_APP_WIDGET, connectedAppWidget);
		}
		
		return false;
	}
}