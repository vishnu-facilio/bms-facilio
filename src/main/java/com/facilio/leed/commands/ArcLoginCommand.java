package com.facilio.leed.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.leed.constants.LeedConstants;
import com.facilio.leed.context.ArcContext;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;

public class ArcLoginCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ArcContext  credentials = (ArcContext)context.get(LeedConstants.ContextNames.ARCCONTEXT);
		LeedIntegrator leedInt = new LeedIntegrator();
		credentials = leedInt.LoginArcServer(credentials);
		LeedAPI.AddArcCredential(credentials);
		return false;
	}
	
}
