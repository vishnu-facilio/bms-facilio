package com.facilio.componentpackage.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.command.PackageChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.ServletActionContext;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Log4j
public class PackageAction extends FacilioAction {
    private boolean fromAdminTool = false;
    private int packageType = 1;
    private String displayName;
    private Long sourceOrgId;
    private Long targetOrgId;

    public String createPackageFromOrg() throws Exception{
        AccountUtil.setCurrentAccount(sourceOrgId);

        LOGGER.info("####Sandbox - Initiating Package creation");
        FacilioChain createPackageChain = PackageChainFactory.getCreatePackageChain();
        FacilioContext context = createPackageChain.getContext();
        context.put(PackageConstants.DISPLAY_NAME, displayName);
        context.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        context.put(PackageConstants.TARGET_ORG_ID, targetOrgId);
        context.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        context.put(PackageConstants.PACKAGE_TYPE, PackageContext.PackageType.valueOf(packageType));
        createPackageChain.execute();
        LOGGER.info("####Sandbox - Completed Package creation");

        AccountUtil.cleanCurrentAccount();
        AccountUtil.setCurrentAccount(targetOrgId);

        LOGGER.info("####Sandbox - Initiating Package Deployment");
        FacilioChain deployPackageChain = PackageChainFactory.getDeployPackageChain();
        FacilioContext deployContext = deployPackageChain.getContext();
        deployContext.put(PackageConstants.FILE_ID, (Long)context.get(PackageConstants.FILE_ID));
        deployContext.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        deployPackageChain.execute();
        LOGGER.info("####Sandbox - Completed Package Deployment");

        CommonCommandUtil.updateOrgInfo("metaMigrationStatus", String.valueOf(1));
        ServletActionContext.getResponse().setStatus(200);
        setResult("result", "success");
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }
}
