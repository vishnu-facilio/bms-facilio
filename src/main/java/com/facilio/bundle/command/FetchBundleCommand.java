package com.facilio.bundle.command;

import org.apache.commons.chain.Context;

import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;

public class FetchBundleCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		bundle = BundleUtil.getBundle(bundle.getId());
		
		context.put(BundleConstants.BUNDLE_CONTEXT,bundle);
		
		return false;
	}

}
