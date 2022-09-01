package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.PermissionGroup;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class V3PermissionUtil {

    public static boolean isFeatureEnabled() throws Exception {
        return AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_TAB_PERMISSIONS);
    }

    public static List<PermissionGroup> getPermissionGroups(WebTabContext webTab, String moduleName) throws Exception {
        List<Permission> permissions = AppModulePermissionUtil.getPermissionValue(webTab, moduleName);
        List<PermissionGroup> permissionGroups = new ArrayList<>();
        if (permissions != null) {
            for (Permission p : permissions) {
                if (p instanceof PermissionGroup) {
                    permissionGroups.add((PermissionGroup) p);
                }
            }
            return permissionGroups;
        }
        return null;
    }
    public static PermissionGroup getPermissionGroup(WebTabContext webTab, String moduleName, String actionName) throws Exception {
        List<Permission> permissions = AppModulePermissionUtil.getPermissionValue(webTab, moduleName);
        if (permissions != null) {
            for (Permission p : permissions) {
                if (p instanceof PermissionGroup) {
                    if(p != null) {
                        List<Permission> subPermissions = ((PermissionGroup) p).getPermissions();
                        if(CollectionUtils.isNotEmpty(subPermissions)) {
                            for(Permission subPermission : subPermissions) {
                                if (subPermission.getActionName().equalsIgnoreCase(actionName)) {
                                    return (PermissionGroup) p;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static long getPermissionValueForActionAndTab(WebTabContext webTab, String action) throws Exception {
        String moduleName = "*";
        if (webTab.getTypeEnum().getIndex() == WebTabContext.Type.MODULE.getIndex()) {
            List<String> moduleNames = ApplicationApi.getModulesForTab(webTab.getId());
            if (CollectionUtils.isNotEmpty(moduleNames)) {
                moduleName = moduleNames.get(0);
            }
        }
        List<Permission> permissions = AppModulePermissionUtil.getPermissionValue(webTab, moduleName);
        for (Permission permission : permissions) {
            if (permission instanceof PermissionGroup) {
                List<Permission> childPermissions = ((PermissionGroup) permission).getPermissions();
                if (CollectionUtils.isNotEmpty(childPermissions)) {
                    for (Permission childPermission : childPermissions) {
                        if (childPermission.getActionName().equals(action)) {
                            return childPermission.getValue();
                        }
                    }
                }
            }
            else if (permission.getActionName().equals(action)) {
                return permission.getValue();
            }
        }
        return -1;
    }

    public static List<Permission> getPermissionValue(WebTabContext webtab) throws Exception {
        return getPermissionValue(webtab, null);
    }

    public static List<Permission> getPermissionValue(WebTabContext webtab, String moduleName) throws Exception {
        List<Permission> permissions =  AppModulePermissionUtil.getPermissionValue(webtab, moduleName);
        if(CollectionUtils.isNotEmpty(permissions)){
            return permissions;
        }
        return null;
    }
}