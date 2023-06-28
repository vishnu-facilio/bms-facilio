package com.facilio.permission.context;

import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;
import lombok.Setter;

public enum PermissionFieldEnum implements FacilioStringEnum {
    RELATED_LIST_READ_PERMISSION("Read","readPerm"),
    RELATED_LIST_UPDATE_PERMISSION("Edit","writePerm"),
    FIELD_READ_PERMISSION("Read","readPerm");

    @Getter @Setter
    private String displayName;

    @Getter @Setter

    private String propName;
    PermissionFieldEnum(String displayName,String propName) {
        this.displayName = displayName;
        this.propName = propName;
    }
}