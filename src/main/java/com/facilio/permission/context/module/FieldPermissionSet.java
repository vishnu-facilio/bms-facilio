package com.facilio.permission.context.module;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class FieldPermissionSet extends ModuleTypePermissionSetContext{
    private Long FieldId;

    public FieldPermissionSet(Long moduleId, Long fieldId,String displayname) {
        this.FieldId = fieldId;
        setModuleId(moduleId);
        setDisplayName(displayname);
    }
}
