package com.facilio.bmsconsole.context.webtab;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.dispatcher.Parameter;

import java.util.List;
import java.util.Map;

public class ModuleTypeHandler implements WebTabHandler {
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String,String> parameters, String action) {
        String moduleName = parameters.get(FacilioConstants.ContextNames.WebTab.MODULE_NAME);
        String parentModuleName = parameters.get(FacilioConstants.ContextNames.WebTab.PARENT_MODULE_NAME);
        if(parentModuleName != null) {
            moduleName = parentModuleName;
        }
        if(PermissionUtil.permCheckSysModules().contains(moduleName)) {
            return currentUserHasPermission(webtab,moduleName,action, AccountUtil.getCurrentUser().getRole());
        }
        return true;
    }

    @Override
    public boolean hasPermission(long tabId, Map<String,String> parameters, String action) throws Exception {
        WebTabContext tab = ApplicationApi.getWebTab(tabId);
        if(tab != null) {
            return hasPermission(tab,parameters,action);
        }
        return false;
    }

    public static boolean currentUserHasPermission(WebTabContext tab, String moduleName, String action, Role role) {

        try {
            long tabId = tab.getId();
            if (moduleName.equalsIgnoreCase("planned"))
                moduleName = FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE;
            if (V3PermissionUtil.isFeatureEnabled()) {
                NewPermission permission = ApplicationApi.getRolesPermissionForTab(tabId, role.getRoleId());
                List<String> moduleNames = ApplicationApi.getModulesForTab(tabId);
                if (!moduleNames.isEmpty()) {
                    if (moduleNames.contains(moduleName)) {
                        boolean hasPerm = PermissionUtil.hasPermission(permission, action, tabId);
                        return hasPerm;
                    }
                }
            } else {
                long rolePermissionVal = ApplicationApi.getRolesPermissionValForTab(tabId, role.getRoleId());
                List<String> moduleNames = ApplicationApi.getModulesForTab(tabId);
                if (!moduleNames.isEmpty()) {
                    if (moduleNames.contains(moduleName)) {
                        boolean hasPerm = PermissionUtil.hasPermission(rolePermissionVal, action, tabId);
                        return hasPerm;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}