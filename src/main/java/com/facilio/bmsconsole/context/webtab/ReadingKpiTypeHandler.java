package com.facilio.bmsconsole.context.webtab;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;

import java.util.List;
import java.util.Map;

@Log4j
public class ReadingKpiTypeHandler implements WebTabHandler {
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String, String> parameters, String action) {
        return currentUserHasPermission(webtab, action, AccountUtil.getCurrentUser().getRole());
    }

    @Override
    public boolean hasPermission(long tabId, Map<String, String> parameters, String action) throws Exception {
        WebTabContext tab = ApplicationApi.getWebTab(tabId);
        if (tab != null) {
            return hasPermission(tab, parameters, action);
        }
        return false;
    }

    public static boolean currentUserHasPermission(WebTabContext tab, String action, Role role) {

        try {
            long tabId = tab.getId();
            if (V3PermissionUtil.isFeatureEnabled()) {
                NewPermission permission = ApplicationApi.getRolesPermissionForTab(tabId, role.getRoleId());
                boolean hasPerm = PermissionUtil.hasPermission(permission, action, tabId);
                return hasPerm;
            } else {
                long rolePermissionVal = ApplicationApi.getRolesPermissionValForTab(tabId, role.getRoleId());
                boolean hasPerm = PermissionUtil.hasPermission(rolePermissionVal, action, tabId);
                return hasPerm;
            }
        } catch (Exception e) {
            LOGGER.info("User Permission Exception: " + e);
        }
        return false;
    }
}
