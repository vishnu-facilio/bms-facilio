package com.facilio.leed.commands;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.leed.constants.LeedConstants;
import com.facilio.leed.context.ArcContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;

public class ArcLoginCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ArcContext  credentials = (ArcContext)context.get(LeedConstants.ContextNames.ARCCONTEXT);
		LeedIntegrator leedInt = new LeedIntegrator();
		credentials = leedInt.LoginArcServer(credentials);
		LeedAPI.AddArcCredential(credentials);
		return false;
	}
	
}
