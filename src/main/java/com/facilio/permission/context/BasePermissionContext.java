package com.facilio.permission.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Setter
@Getter
public class BasePermissionContext implements Serializable {
    private long id;
    private String displayName;

    private PermissionSetType.Type type;

    private Long permissionSetId;

    private Boolean permission;

    private PermissionFieldEnum permissionType;

    private boolean defaultValue;

    private boolean isDisabled;

}
