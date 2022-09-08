package com.facilio.bmsconsoleV3.context;

import java.io.Serializable;

public class ModuleAppPermissionChild implements Serializable {

    long parentId;
    long childPermission1;
    long childPermission2;
    String displayName;
    private long id = -1L;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public ModuleAppPermissionChild(long parentId, long childPermission1, long childPermission2, String displayName) {
        this.parentId = parentId;
        this.childPermission1 = childPermission1;
        this.childPermission2 = childPermission2;
        this.displayName = displayName;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getChildPermission1() {
        return childPermission1;
    }

    public void setChildPermission1(long childPermission1) {
        this.childPermission1 = childPermission1;
    }

    public long getChildPermission2() {
        return childPermission2;
    }

    public void setChildPermission2(long childPermission2) {
        this.childPermission2 = childPermission2;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private long modulePermissionChildId;

    public ModuleAppPermissionChild(){}

    public long getModulePermissionChildId() {
        return modulePermissionChildId;
    }

    public void setModulePermissionChildId(long modulePermissionChildId) {
        this.modulePermissionChildId = modulePermissionChildId;
    }
}
