package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.context.ModuleAppPermission;
import com.facilio.bmsconsoleV3.util.AppModulePermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

import java.util.List;

public class ApplicationPermissionAction extends V3Action {

    private Long applicationId;
    private Long tabId;

    public Long getTabId() {
        return tabId;
    }

    public void setTabId(Long tabId) {
        this.tabId = tabId;
    }

    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }


    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public List<ModuleAppPermission> getModuleAppPermission() {
        return moduleAppPermission;
    }

    public void setModuleAppPermission(List<ModuleAppPermission> moduleAppPermission) {
        this.moduleAppPermission = moduleAppPermission;
    }

    private List<ModuleAppPermission> moduleAppPermission;

    public String addOrUpdate() throws Exception {
        AppModulePermissionUtil.addOrUpdateModuleAppPermissions(moduleAppPermission);
        setData("message", "SUCCESS");
        return SUCCESS;
    }

    public String list() throws Exception {

        setData(FacilioConstants.ContextNames.NewTabPermission.MODULE_APP_PERMISSION, AppModulePermissionUtil.listAppPermissions(applicationId,tabId));

        return SUCCESS;
    }

    public String delete() throws Exception {
        AppModulePermissionUtil.deleteAppPermissions(ids);
        setData("message", "DELETED");
        return SUCCESS;
    }
    public String deleteChildren() throws Exception {
        AppModulePermissionUtil.deleteAppChildPermissions(ids);
        setData("message", "DELETED");
        return SUCCESS;
    }
}
