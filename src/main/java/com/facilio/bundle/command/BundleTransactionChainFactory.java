package com.facilio.bundle.command;

import com.facilio.chain.FacilioChain;

public class BundleTransactionChainFactory {

	
	private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getCopyCustomizationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PackAllBundleComponentsCommand());
        return c;
    }
    
    public static FacilioChain getInstallBundleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ParseZIPToFolderContentCommand());
        c.addCommand(new InstallBundledFolderContentCommand());
        return c;
    }
    
    public static FacilioChain getPopulateBundleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PopulateBundleToNewOrgCommand());			//as per the old flow, not using now. keeping it just for reference.
        return c;
    }
}
