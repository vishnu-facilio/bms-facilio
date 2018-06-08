 package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.constants.FacilioConstants;

public class LoadPortalInfoCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		PortalInfoContext portalinfo = AccountUtil.getPortalInfo();
		context.put(FacilioConstants.ContextNames.PORTALINFO, portalinfo);
		
		return false;
	}

}
