package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.context.ConnectionParamContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.constants.FacilioConstants;

public class UpdateConnectionCommand extends FacilioCommand  {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
		
		if(connectionContext != null) {
			
			ConnectionUtil.updateConnectionContext(connectionContext);
			
			ConnectionUtil.deleteConnectionParams(connectionContext);
			
			if(connectionContext.getConnectionParams() != null) {
				for(ConnectionParamContext connectionParamContext : connectionContext.getConnectionParams()) {
					connectionParamContext.setConnectionId(connectionContext.getId());
					ConnectionUtil.addConnectionParams(connectionParamContext);
				}
			}
		}
		
		return false;
	}

}
