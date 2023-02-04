package com.facilio.componentpackage.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.command.PackageChainFactory;

public class PackageAction extends FacilioAction {

    public String createPackageFromOrg() throws Exception{
        FacilioChain createPackageChain = PackageChainFactory.getOrgPackageChain();
        FacilioContext context = createPackageChain.getContext();
        createPackageChain.execute();
        setResult(BundleConstants.DOWNLOAD_URL, context.get(BundleConstants.DOWNLOAD_URL));

        return SUCCESS;
    }
}
