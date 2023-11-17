package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.PermissionGroup;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class V3PermissionUtil {

    public static boolean isFeatureEnabled() throws Exception {
        return AccountUtil.isFeatureEnabled(
                AccountUtil.FeatureLicense.NEW_TAB_PERMISSIONS
        );
    }

    public static List < PermissionGroup > getPermissionGroups(WebTabContext webTab) throws Exception {
        List < Permission > permissions = AppModulePermissionUtil.getPermissionValue(webTab);
        List < PermissionGroup > permissionGroups = new ArrayList < > ();
        if (permissions != null) {
            for (Permission p: permissions) {
                if (p instanceof PermissionGroup) {
                    permissionGroups.add((PermissionGroup) p);
                }
            }
            return permissionGroups;
        }
        return null;
    }

    public static long getPermissionValueForActionAndTab(WebTabContext webTab, String action) throws Exception {
        List < Permission > permissions = AppModulePermissionUtil.getPermissionValue(webTab);
        for (Permission permission: permissions) {
            if (permission instanceof PermissionGroup) {
                List < Permission > childPermissions = ((PermissionGroup) permission).getPermissions();
                if (CollectionUtils.isNotEmpty(childPermissions)) {
                    for (Permission childPermission: childPermissions) {
                        if (childPermission.getActionName().equals(action)) {
                            return childPermission.getValue();
                        }
                    }
                }
            } else if (permission.getActionName().equals(action)) {
                return permission.getValue();
            }
        }
        return -1;
    }

    public static List < Permission > getPermissionValue(WebTabContext webtab, Long roleId) throws Exception {
        List < Permission > permissions = AppModulePermissionUtil.getPermissionValue(webtab, roleId);
        if (CollectionUtils.isNotEmpty(permissions)) {
            return permissions;
        }
        return null;
    }

    //Backward compatability for write - read handled by feature license
    public static void addPermissionV3(long roleId, NewPermission newPermissions) throws Exception {
        WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
        WebTabContext tab = tabBean.getWebTab(newPermissions.getTabId());
        FacilioModule module;
        NewPermission perm;
        List < FacilioField > fields;
        if (isFeatureEnabled()) {
            List < String > actionList = AppModulePermissionUtil.getPermissionsForValues(newPermissions.getPermission(), newPermissions.getPermission2());
            long permValue = 0;
            for (String actionName: actionList) {
                long permVal = NewPermissionUtil.getPermissionValue(tab.getType(), actionName);
                if (permVal > 0) {
                    permValue = permValue + permVal;
                }
            }
            NewPermission permissionToInsert = new NewPermission(tab.getId(), permValue, 0);
            permissionToInsert.setRoleId(newPermissions.getRoleId());
            perm = permissionToInsert;
            module = ModuleFactory.getNewPermissionModule(true);
            fields = FieldFactory.getNewPermissionFields(true);
        } else {
            List < String > actionList = NewPermissionUtil.getActionsForPermissionValue(tab.getType(), newPermissions.getPermission());
            long permValue1 = 0;
            long permValue2 = 0;
            Map < String, Permission > permissionsMap = AppModulePermissionUtil.getPermissionsMap();
            for (String actionName: actionList) {
                if (permissionsMap.containsKey(actionName)) {
                    if (permissionsMap.get(actionName).getPermissionMapping().getGroupId() == AppModulePermissionUtil.PermissionMapping.GROUP1PERMISSION.getGroupId()) {
                        permValue1 = permValue1 + permissionsMap.get(actionName).getValue();
                    } else if (permissionsMap.get(actionName).getPermissionMapping().getGroupId() == AppModulePermissionUtil.PermissionMapping.GROUP2PERMISSION.getGroupId()) {
                        permValue2 = permValue2 + permissionsMap.get(actionName).getValue();
                    }
                }
            }

            NewPermission permissionToInsert = new NewPermission(tab.getId(), permValue1, permValue2);
            permissionToInsert.setRoleId(newPermissions.getRoleId());
            perm = permissionToInsert;
            module = ModuleFactory.getNewTabPermissionModule();
            fields = FieldFactory.getNewTabPermissionFields();
        }

        if (perm != null) {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(module.getTableName())
                    .fields(fields);
            builder.insert(FieldUtil.getAsProperties(perm));
        }
    }

    public static NewPermission getPermissionValueForTab(WebTabContext webtab) {
        if (webtab != null) {
            long permission1 = 0, permission2 = 0;
            List < Permission > permissions = webtab.getPermission();
            if (CollectionUtils.isNotEmpty(permissions)) {
                for (Permission permission: permissions) {
                    if (permission.isEnabled()) {
                        long value = getValueForActionName(permission.getActionName());
                        if (permission.getPermissionMapping().getGroupId() == AppModulePermissionUtil.PermissionMapping.GROUP1PERMISSION.getGroupId()) {
                            permission1 = permission1 + value;
                        } else if (permission.getPermissionMapping().getGroupId() == AppModulePermissionUtil.PermissionMapping.GROUP2PERMISSION.getGroupId()) {
                            permission2 = permission2 + value;
                        }
                    }
                }
            }
            NewPermission newPermission = new NewPermission(webtab.getId(), permission1, permission2);
            return newPermission;
        }
        return null;
    }

    private static long getValueForActionName(String actionName) {
        if (actionName != null) {
            Map < String, Permission > permissionMap = AppModulePermissionUtil.getPermissionsMap();
            if (permissionMap.containsKey(actionName)) {
                return permissionMap.get(actionName).getValue();
            }
        }
        return 0;
    }

    private static final List<String> WHITE_LIST_MODULE_NAMES_LIST = Arrays.asList(
            FacilioConstants.ContextNames.USER_NOTIFICATION
    );
    public static boolean isWhitelistedModule(String moduleName) {
        if(StringUtils.isNotEmpty(moduleName) && WHITE_LIST_MODULE_NAMES_LIST.contains(moduleName)) {
            return true;
        }
        return false;
    }

    public static boolean currentUserHasPermission(long tabId, String action) {
        try {
            long rolePermissionVal = ApplicationApi.getRolesPermissionValForTab(tabId, AccountUtil.getCurrentUser().getRole().getRoleId());
            return PermissionUtil.hasPermission(rolePermissionVal, action, tabId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAllowedEnvironment(){
        return (!(FacilioProperties.isProduction() || FacilioProperties.isOnpremise())) && FacilioProperties.isCheckPrivilegeAccess();
    }
}