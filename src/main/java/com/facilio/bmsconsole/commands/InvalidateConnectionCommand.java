package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.constants.FacilioConstants;

public class InvalidateConnectionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
		
		connectionContext = ConnectionUtil.getConnection(connectionContext.getId());
		
		if(connectionContext != null && (connectionContext.getStateEnum() == ConnectionContext.State.AUTH_TOKEN_GENERATED || connectionContext.getStateEnum() == ConnectionContext.State.AUTHORIZED)) {
			
			connectionContext.setState(ConnectionContext.State.CLIENT_ID_MAPPED.getValue());
			
			connectionContext.setAuthCode("");
			connectionContext.setAccessToken("");
			connectionContext.setRefreshToken("");
			connectionContext.setExpiryTime(-99);
			connectionContext.setRefreshTokenExpiryTime(-99);
			
			ConnectionUtil.updateConnectionContext(connectionContext);
		}
		
		return false;
	}

}
