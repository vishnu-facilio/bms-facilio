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
}
