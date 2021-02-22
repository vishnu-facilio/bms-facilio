package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.constants.FacilioConstants;

public class GetAccessTokenForConnectionCommand extends FacilioCommand  {

	private static final Logger LOGGER = LogManager.getLogger(GetAccessTokenForConnectionCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
			ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
			
			if(connectionContext.getStateEnum() == ConnectionContext.State.AUTHORIZED) {
				connectionContext.getGrantTypeEnum().validateConnection(connectionContext);
			}
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

}
