package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.constants.FacilioConstants;

public class GetAccessTokenForConnectionCommand extends FacilioCommand  {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
		
		
		ConnectionUtil.validateOauth2Connection(connectionContext);
		
		return false;
	}

}
