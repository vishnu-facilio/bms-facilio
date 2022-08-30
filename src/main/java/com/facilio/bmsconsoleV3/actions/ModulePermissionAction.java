package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.context.ModuleAppPermission;
import com.facilio.bmsconsoleV3.context.ModulePermission;
import com.facilio.bmsconsoleV3.util.AppModulePermissionUtil;
import com.facilio.bmsconsoleV3.util.ModulePermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

import java.util.List;

public class ModulePermissionAction extends V3Action {

    private Long moduleId;
    private String specialLinkName;
    private List<Long> ids;

    public Long getModuleId() {
        return moduleId;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
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

    public List<ModulePermission> getModulePermission() {
        return modulePermission;
    }

    public void setModulePermission(List<ModulePermission> modulePermission) {
        this.modulePermission = modulePermission;
    }

    private List<ModulePermission> modulePermission;

    public String addOrUpdate() throws Exception {
        ModulePermissionUtil.addOrUpdateModulePermissions(modulePermission);
        setData("message", "SUCCESS");
        return SUCCESS;
    }

    public String list() throws Exception {

        setData(FacilioConstants.ContextNames.NewTabPermission.MODULE_PERMISSION, ModulePermissionUtil.listModulePermissions(moduleId,specialLinkName));
        return SUCCESS;
    }

    public String delete() throws Exception {
        ModulePermissionUtil.deleteModulePermissions(ids);
        setData("message", "DELETED");
        return SUCCESS;
    }

    public String deleteChildren() throws Exception {
        ModulePermissionUtil.deleteModuleChildPermissions(ids);
        setData("message", "DELETED");
        return SUCCESS;
    }
}
