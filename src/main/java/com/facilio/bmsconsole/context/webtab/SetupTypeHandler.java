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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SetupTypeHandler implements WebTabHandler{
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String, String> parameters, String action) {
        boolean isSetupTab = parameters.containsKey("setupTab");
        String setupTabType = parameters.get("setupTab");
        String permissionModuleName = parameters.get("permissionModuleName");
        String moduleName = parameters.get(FacilioConstants.ContextNames.WebTab.MODULE_NAME);
        if (permissionModuleName != null) {
            moduleName = permissionModuleName;
        }
        if(isSetupTab) {
            return currentUserHasPermission(webtab,setupTabType,moduleName,action, AccountUtil.getCurrentUser().getRole());
        }
        return true;
    }

    @Override
    public boolean hasPermission(long tabId, Map<String, String> parameters, String action) throws Exception {
        WebTabContext tab = ApplicationApi.getWebTab(tabId);
        return hasPermission(tab,parameters,action);
    }


    public static boolean currentUserHasPermission(WebTabContext tab,String setupTabType, String moduleName, String action, Role role) {
        try {
            long tabId = tab.getId();
            boolean passedTabTypeCheck = false;
            if(StringUtils.isNotEmpty(setupTabType)) {
                List<String> tabTypesSupportedForRequest = Arrays.asList(StringUtils.split(setupTabType,","));
                if(CollectionUtils.isNotEmpty(tabTypesSupportedForRequest)) {
                    if(tabTypesSupportedForRequest.contains(tab.getTypeEnum().name())) {
                        passedTabTypeCheck = true;
                    }
                }
            }
            if(!passedTabTypeCheck)
                return false;
            if(V3PermissionUtil.isFeatureEnabled()){
                NewPermission permission = ApplicationApi.getRolesPermissionForTab(tabId, role.getRoleId());
                boolean hasPerm =  PermissionUtil.hasPermission(permission, action, tabId);
                return hasPerm;
            } else {
                long rolePermissionVal = ApplicationApi.getRolesPermissionValForTab(tabId, role.getRoleId());
                if(tab != null) {
                    boolean hasPerm = PermissionUtil.hasPermission(rolePermissionVal, action, tabId);
                    return hasPerm;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
