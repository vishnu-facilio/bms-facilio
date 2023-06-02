package com.facilio.componentpackage.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.command.PackageChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PackageAction extends FacilioAction {
    int packageType;
    String uniqueName;
    String displayName;
    Long sourceOrgId;
    Long targetOrgId;

    public String createPackageFromOrg() throws Exception{
        FacilioChain createPackageChain = PackageChainFactory.getCreatePackageChain();

        AccountUtil.setCurrentAccount(sourceOrgId);
        FacilioContext context = createPackageChain.getContext();
        context.put(PackageConstants.UNIQUE_NAME, uniqueName);
        context.put(PackageConstants.DISPLAY_NAME, displayName);
        context.put(PackageConstants.PACKAGE_TYPE, PackageContext.PackageType.valueOf(packageType));
        createPackageChain.execute();

        AccountUtil.cleanCurrentAccount();
        AccountUtil.setCurrentAccount(targetOrgId);
        FacilioChain deployPackageChain = PackageChainFactory.getDeployPackageChain();

        FacilioContext deployContext = deployPackageChain.getContext();
        deployContext.put(PackageConstants.UNIQUE_NAME, uniqueName);
        deployContext.put(PackageConstants.FILE_ID, (Long)context.get(PackageConstants.FILE_ID));
        deployContext.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        deployPackageChain.execute();

        setResult(BundleConstants.DOWNLOAD_URL, context.get(BundleConstants.DOWNLOAD_URL));
        AccountUtil.cleanCurrentAccount();
        return SUCCESS;
    }
}
