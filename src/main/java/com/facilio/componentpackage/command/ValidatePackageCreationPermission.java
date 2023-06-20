package com.facilio.componentpackage.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.componentpackage.constants.PackageConstants;
import org.apache.commons.collections.MapUtils;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import java.util.Map;

public class ValidatePackageCreationPermission extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean fromAdminTool = (boolean) context.getOrDefault(PackageConstants.FROM_ADMIN_TOOL, false);

        if (!fromAdminTool) {
            return false;
        }

        long sourceOrgId = (long) context.get(PackageConstants.SOURCE_ORG_ID);
        long targetOrgId = (long) context.get(PackageConstants.TARGET_ORG_ID);

        AccountUtil.setCurrentAccount(targetOrgId);

        Map<String, Object> metaMigrationPermission = CommonCommandUtil.getOrgInfo(targetOrgId, "metaMigration");
        Map<String, Object> metaMigrationStatus = CommonCommandUtil.getOrgInfo(targetOrgId, "metaMigrationStatus");

        boolean hasMetaMigrationPermission = false;
        if (MapUtils.isNotEmpty(metaMigrationPermission)) {
            hasMetaMigrationPermission = Boolean.parseBoolean(String.valueOf(metaMigrationPermission.get("value")));
        }

        int status = 0;
        if (MapUtils.isNotEmpty(metaMigrationStatus)) {
            status = Integer.parseInt(String.valueOf(metaMigrationStatus.get("value")));
        }

        if (status > 0 || !hasMetaMigrationPermission) {
            return true;
        }

        AccountUtil.cleanCurrentAccount();
        AccountUtil.setCurrentAccount(sourceOrgId);

        String displayName = "package_" + sourceOrgId + "_" + targetOrgId + "_" + System.currentTimeMillis();
        context.put(PackageConstants.DISPLAY_NAME, displayName);

        return false;
    }
}
