package com.facilio.bmsconsoleV3.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsoleV3.ModuleAppPermissionChild;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ModuleAppPermission extends ModulePermission{
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

    public ModuleAppPermission(String moduleName, String specialLinkName, List<String> applicationLinkNames, List<Permission> permissionList) throws Exception {
        super(moduleName, specialLinkName, permissionList);
        this.applicationLinkNames = applicationLinkNames;
    }

    public ModuleAppPermission(){}
}