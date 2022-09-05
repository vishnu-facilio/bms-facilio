package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ModuleAppPermission{
    private long id = -1L;

    private Long moduleId;
    private String moduleName;
    private String specialLinkName;
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

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    private long applicationId;
    private List<String> applicationLinkNames;
    private List<ModuleAppPermissionChild> moduleAppPermissionChildren;

    public long getModulePermissionParentId() {
        return modulePermissionParentId;
    }

    public List<ModuleAppPermissionChild> getModuleAppPermissionChildren() {
        return moduleAppPermissionChildren;
    }

    public void setModuleAppPermissionChildren(List<ModuleAppPermissionChild> moduleAppPermissionChildren) {
        this.moduleAppPermissionChildren = moduleAppPermissionChildren;
    }

    public void setModulePermissionParentId(long modulePermissionParentId) {
        this.modulePermissionParentId = modulePermissionParentId;
    }

    private long modulePermissionParentId;
    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
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

    public String getSpecialLinkName() {
        return specialLinkName;
    }

    public void setSpecialLinkName(String specialLinkName) {
        this.specialLinkName = specialLinkName;
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

    public ModuleAppPermission(){}

    public ModuleAppPermission(String moduleName, String specialLinkName, List<Permission> permissionList,List<String> applicationLinkNames) throws Exception {
        new ModuleAppPermission(moduleName,specialLinkName,permissionList);
        this.applicationLinkNames = applicationLinkNames;
    }

    public ModuleAppPermission(String moduleName, String specialLinkName, List<Permission> permissionList) throws Exception {
        this.moduleName = moduleName;
        this.specialLinkName = specialLinkName;
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module != null) {
                this.moduleId = modBean.getModule(moduleName).getModuleId();
            }
        }
        this.permissionList = permissionList;
    }
}