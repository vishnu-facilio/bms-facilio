package com.facilio.bundle.command;

import org.apache.commons.chain.Context;

import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;

public class FetchLatestBundleCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleContext parentBundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		BundleContext latestVersionBundle = BundleUtil.getLatestVersionOfBundle(parentBundle);
		
		context.put(BundleConstants.BUNDLE_CONTEXT, latestVersionBundle);

		return false;
	}

}
