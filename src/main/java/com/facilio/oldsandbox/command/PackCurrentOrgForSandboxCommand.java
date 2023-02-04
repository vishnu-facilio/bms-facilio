package com.facilio.oldsandbox.command;

import org.apache.commons.chain.Context;

import com.facilio.bundle.command.BundleTransactionChainFactory;
import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.oldsandbox.context.SandboxContext;

public class PackCurrentOrgForSandboxCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		SandboxContext sandbox = (SandboxContext) context.get(BundleConstants.Sandbox.SANDBOX);
		
		BundleContext sandboxBundle = new BundleContext();
		sandboxBundle.setBundleName(sandbox.getName()+"_Sanbox");
		sandboxBundle.setBundleGlobalName(sandbox.getDomain()+"_Sanbox");
		sandboxBundle.setTypeEnum(BundleContext.BundleTypeEnum.PRODUCTION_SANDBOX_INTERNAL);
		
		FacilioChain addBundle = BundleTransactionChainFactory.addBundleChain();
		
		FacilioContext newcontext = addBundle.getContext();
		
		newcontext.put(BundleConstants.BUNDLE_CONTEXT, sandboxBundle);
		
		addBundle.execute();
		
		FacilioChain createVersion = BundleTransactionChainFactory.getCreateVersionChain();
		
		newcontext = createVersion.getContext();
		
		newcontext.put(BundleConstants.BUNDLE_CONTEXT, sandboxBundle);
		
		createVersion.execute();
		
		context.put(BundleConstants.BUNDLE_CONTEXT, newcontext.get(BundleConstants.BUNDLE_CONTEXT));
		
		return false;
	}

}
