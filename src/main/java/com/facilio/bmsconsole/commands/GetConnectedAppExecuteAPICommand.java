package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.HttpMethod;
import com.facilio.bmsconsole.context.ConnectedAppConnectorContext;
import com.facilio.bmsconsole.context.ConnectedAppRequestContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.constants.FacilioConstants;

public class GetConnectedAppExecuteAPICommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(GetConnectedAppExecuteAPICommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ConnectedAppRequestContext apiRequest = (ConnectedAppRequestContext) context.get(FacilioConstants.ContextNames.CONNECTED_APP_REQUEST);
		ConnectedAppConnectorContext connectedAppConnector = (ConnectedAppConnectorContext) context.get(FacilioConstants.ContextNames.CONNECTED_APP_CONNECTOR);
		
		if (apiRequest != null) {
			
			HttpMethod httpMethod = apiRequest.getMethod() != null ? HttpMethod.valueOf(apiRequest.getMethod()) : HttpMethod.GET;
			
			String apiResponse = null;
			
			if (connectedAppConnector != null) {
				LOGGER.info("Connected App - Execute Connector API: connectedAppId: "+apiRequest.getConnectedAppId()+" connector: "+apiRequest.getConnector()+" url: "+apiRequest.getUrl()+" method: "+apiRequest.getMethod());
				apiResponse = ConnectionUtil.getUrlResult(connectedAppConnector.getConnection(), apiRequest.getUrl(), apiRequest.getParams(), httpMethod, apiRequest.getData(), apiRequest.getContentType(), apiRequest.getHeaders());
			}
			else {
				LOGGER.info("Connected App - Execute External API: connectedAppId: "+apiRequest.getConnectedAppId()+" url: "+apiRequest.getUrl()+" method: "+apiRequest.getMethod());
				apiResponse = ConnectionUtil.getUrlResult(apiRequest.getUrl(), apiRequest.getParams(), httpMethod, apiRequest.getHeaders(), apiRequest.getData(), apiRequest.getContentType());
			}
			
			context.put(FacilioConstants.ContextNames.RESULT, apiResponse);
		}
		
		return false;
	}
}