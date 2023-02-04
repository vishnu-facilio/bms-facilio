package com.facilio.componentpackage.command;

import com.facilio.chain.FacilioChain;

public class PackageChainFactory {

	
	private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getOrgPackageChain() {
        FacilioChain c = getDefaultChain();

        return c;
    }
    

}
