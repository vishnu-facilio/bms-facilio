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
import org.apache.struts2.ServletActionContext;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PackageAction extends FacilioAction {
    private boolean fromAdminTool = false;
    private int packageType = 2;
    private String displayName;
    private Long sourceOrgId;
    private Long targetOrgId;

    public String createPackageFromOrg() throws Exception{
        FacilioChain createPackageChain = PackageChainFactory.getCreatePackageChain();

        AccountUtil.setCurrentAccount(sourceOrgId);
        FacilioContext context = createPackageChain.getContext();
        context.put(PackageConstants.DISPLAY_NAME, displayName);
        context.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        context.put(PackageConstants.TARGET_ORG_ID, targetOrgId);
        context.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        context.put(PackageConstants.PACKAGE_TYPE, PackageContext.PackageType.valueOf(packageType));
        createPackageChain.execute();

        AccountUtil.cleanCurrentAccount();
        AccountUtil.setCurrentAccount(targetOrgId);
        FacilioChain deployPackageChain = PackageChainFactory.getDeployPackageChain();

        FacilioContext deployContext = deployPackageChain.getContext();
        deployContext.put(PackageConstants.FILE_ID, (Long)context.get(PackageConstants.FILE_ID));
        deployContext.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        deployPackageChain.execute();

        CommonCommandUtil.updateOrgInfo("metaMigrationStatus", String.valueOf(1));
        ServletActionContext.getResponse().setStatus(200);
        setResult("result", "success");
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }
}
