package com.facilio.bundle.command;

import java.io.File;

import org.apache.commons.chain.Context;

import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.oldsandbox.context.SandboxContext;

public class InstallSandboxChangeSetCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		File versionFile = (File) context.get(BundleConstants.BUNDLE_FOLDER);
		
		SandboxContext sandbox = (SandboxContext)context.get(BundleConstants.Sandbox.SANDBOX);
		
		FacilioChain installBundle = BundleTransactionChainFactory.getInstallBundleChain();
		
		FacilioContext newContext = installBundle.getContext();
		
		newContext.put(BundleConstants.BUNDLE_ZIP_FILE, versionFile);
		newContext.put(BundleConstants.BUNDLE_ZIP_FILE_NAME, sandbox.getName());
		
		installBundle.execute();
		
		return false;
	}

}
