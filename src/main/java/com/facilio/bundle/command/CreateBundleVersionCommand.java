package com.facilio.bundle.command;

import org.apache.commons.chain.Context;

import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;

public class CreateBundleVersionCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		String version = (String) context.get(BundleConstants.VERSION);
		
		long fileId = (long) context.get(FacilioConstants.ContextNames.FILE_ID);
		
		
		bundle.setVersion(version);
		bundle.setBundleFileId(fileId);
		bundle.setCreatedTime(System.currentTimeMillis());
		bundle.setModifiedTime(bundle.getCreatedTime());
		
		FacilioChain addBundle = BundleTransactionChainFactory.addBundleChain();
		
		FacilioContext newcontext = addBundle.getContext();
		
		newcontext.put(BundleConstants.BUNDLE_CONTEXT, bundle);
		
		addBundle.execute();
		
		context.put(BundleConstants.BUNDLE_CONTEXT,  newcontext.get(BundleConstants.BUNDLE_CONTEXT));
		
		return false;
	}

}
