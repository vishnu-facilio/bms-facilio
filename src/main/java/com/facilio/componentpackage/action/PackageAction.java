package com.facilio.componentpackage.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.command.PackageChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter @Setter @Log4j
public class PackageAction extends FacilioAction {
    private boolean fromAdminTool = false;
    private int packageType = 1;
    private String displayName;
    private Long sourceOrgId;
    private Long targetOrgId;
    private File file;

    public String getDisplayName() {
        if(StringUtils.isEmpty(displayName)) {
            displayName = "package_" + sourceOrgId + "_" + targetOrgId + "_" + System.currentTimeMillis();
        }
        return displayName;
    }

    public String createAndInstallPackage() throws Exception{
        AccountUtil.setCurrentAccount(sourceOrgId);

        LOGGER.info("####Sandbox - Initiating Package creation");
        FacilioChain createPackageChain = PackageChainFactory.getCreatePackageChain();
        FacilioContext context = createPackageChain.getContext();
        context.put(PackageConstants.DISPLAY_NAME, getDisplayName());
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
        deployContext.put(PackageConstants.TARGET_ORG_ID, targetOrgId);
        deployContext.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        deployPackageChain.execute();
        LOGGER.info("####Sandbox - Completed Package Deployment");

        CommonCommandUtil.updateOrgInfo("metaMigrationStatus", String.valueOf(1));
        ServletActionContext.getResponse().setStatus(200);
        setResult("result", "success");
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }

    public String createPackage() throws Exception{
        AccountUtil.setCurrentAccount(sourceOrgId);

        LOGGER.info("####Sandbox - Initiating Package creation");
        FacilioChain createPackageChain = PackageChainFactory.getCreatePackageChain();
        FacilioContext context = createPackageChain.getContext();
        context.put(PackageConstants.DISPLAY_NAME, getDisplayName());
        context.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        context.put(PackageConstants.TARGET_ORG_ID, targetOrgId);
        context.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        context.put(PackageConstants.PACKAGE_TYPE, PackageContext.PackageType.valueOf(packageType));
        createPackageChain.execute();
        LOGGER.info("####Sandbox - Completed Package creation");

        setResult(PackageConstants.DOWNLOAD_URL, context.get(PackageConstants.DOWNLOAD_URL));
        setResult(PackageConstants.FILE_ID, context.get(PackageConstants.FILE_ID));
        setResult("result", "success");

        ServletActionContext.getResponse().setStatus(200);
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }

    public String installPackage() throws Exception{
        AccountUtil.setCurrentAccount(targetOrgId);

        LOGGER.info("####Sandbox - Initiating Package Deployment");
        FacilioChain deployPackageChain = PackageChainFactory.getDeployPackageChain();
        FacilioContext deployContext = deployPackageChain.getContext();
        deployContext.put(PackageConstants.FILE, file);
        deployContext.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        deployContext.put(PackageConstants.TARGET_ORG_ID, targetOrgId);
        deployContext.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        deployPackageChain.execute();
        LOGGER.info("####Sandbox - Completed Package Deployment");

        CommonCommandUtil.updateOrgInfo("metaMigrationStatus", String.valueOf(1));
        ServletActionContext.getResponse().setStatus(200);
        setResult("result", "success");
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }
}
