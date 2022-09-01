package com.facilio.bmsconsoleV3;

import com.facilio.bmsconsoleV3.context.ModulePermisssionChild;

public class ModuleAppPermissionChild extends ModulePermisssionChild {
    private long modulePermissionChildId;
    public ModuleAppPermissionChild(long parentId, long childPermission1, long childPermission2, String displayName){
        super(parentId, childPermission1, childPermission2, displayName);
    }
    public ModuleAppPermissionChild(){}

    public long getModulePermissionChildId() {
        return modulePermissionChildId;
    }

    public void setModulePermissionChildId(long modulePermissionChildId) {
        this.modulePermissionChildId = modulePermissionChildId;
    }
}
