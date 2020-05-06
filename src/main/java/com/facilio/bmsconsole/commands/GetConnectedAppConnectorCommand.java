package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectedAppConnectorContext;
import com.facilio.bmsconsole.context.ConnectedAppRequestContext;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.constants.FacilioConstants;

public class GetConnectedAppConnectorCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long connectedAppId = (long) context.get(FacilioConstants.ContextNames.ID);
		String connectorName = (String) context.get(FacilioConstants.ContextNames.CONNECTION_NANE);
		ConnectedAppRequestContext apiRequest = (ConnectedAppRequestContext) context.get(FacilioConstants.ContextNames.CONNECTED_APP_REQUEST);
		
		if (apiRequest != null) {
			connectedAppId = apiRequest.getConnectedAppId();
			connectorName = apiRequest.getConnector();
		}
		
		if (connectedAppId > 0 && connectorName != null) {
			ConnectedAppConnectorContext connectedAppConnector = ConnectedAppAPI.getConnector(connectedAppId, connectorName);
		
			context.put(FacilioConstants.ContextNames.CONNECTED_APP_CONNECTOR, connectedAppConnector);
		}
		
		return false;
	}
}