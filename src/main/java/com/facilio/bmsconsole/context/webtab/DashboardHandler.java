package com.facilio.bmsconsole.context.webtab;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;

import java.util.Map;

public class DashboardHandler implements WebTabHandler{
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String, String> parameters, String action) {
        try {
            long tabId = webtab.getId();
            if(!(webtab.getTypeEnum() == WebTabContext.Type.DASHBOARD || webtab.getTypeEnum() == WebTabContext.Type.REPORT || webtab.getTypeEnum() == WebTabContext.Type.PIVOT || webtab.getTypeEnum() == WebTabContext.Type.NEW_DASHBOARD))
                return false;
            Role role = AccountUtil.getCurrentUser().getRole();
            if(V3PermissionUtil.isFeatureEnabled()){
                NewPermission permission = ApplicationApi.getRolesPermissionForTab(tabId, role.getRoleId());
                boolean hasPerm =  PermissionUtil.hasPermission(permission, action, tabId);
                return hasPerm;
            } else {
                long rolePermissionVal = ApplicationApi.getRolesPermissionValForTab(tabId, role.getRoleId());
                if(webtab != null) {
                    boolean hasPerm = PermissionUtil.hasPermission(rolePermissionVal, action, tabId);
                    return hasPerm;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean hasPermission(long tabId, Map<String, String> parameters, String action) throws Exception {
        WebTabContext tab = ApplicationApi.getWebTab(tabId);
        return hasPermission(tab,parameters,action);
    }
}
