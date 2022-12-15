package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ModuleAppPermission {
    private long id = -1L;

    private Long moduleId;
    private Long tabId;
    private Long appId;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    private long permission1;
    private long permission2;
    private AccountUtil.FeatureLicense featureLicense;
    private List<Permission> permissionList;
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    public AccountUtil.FeatureLicense getFeatureLicense() {
        return featureLicense;
    }

    public void setFeatureLicense(AccountUtil.FeatureLicense featureLicense) {
        this.featureLicense = featureLicense;
    }

    private List<String> applicationLinkNames;
    private List<ModuleAppPermissionChild> moduleAppPermissionChildren;

    public List<ModuleAppPermissionChild> getModuleAppPermissionChildren() {
        return moduleAppPermissionChildren;
    }

    public void setModuleAppPermissionChildren(List<ModuleAppPermissionChild> moduleAppPermissionChildren) {
        this.moduleAppPermissionChildren = moduleAppPermissionChildren;
    }

    public List<String> getApplications() {
        return applicationLinkNames;
    }

    public void setApplication(List<String> applicationLinkNames) {
        this.applicationLinkNames = applicationLinkNames;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getTabId() {
        return tabId;
    }

    public void setTabId(Long tabId) {
        this.tabId = tabId;
    }

    public long getPermission1() {
        return permission1;
    }

    public void setPermission1(long permission1) {
        this.permission1 = permission1;
    }

    public long getPermission2() {
        return permission2;
    }

    public void setPermission2(long permission2) {
        this.permission2 = permission2;
    }

    public ModuleAppPermission() {
    }

    public ModuleAppPermission(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    public ModuleAppPermission(Long tabId, List<Permission> permissionList, List<String> applicationLinkNames) throws Exception {
        new ModuleAppPermission(tabId, permissionList);
        this.applicationLinkNames = applicationLinkNames;
    }

    public ModuleAppPermission(Long tabId, List<Permission> permissionList) throws Exception {

        this.tabId = tabId;
        this.permissionList = permissionList;
    }
}