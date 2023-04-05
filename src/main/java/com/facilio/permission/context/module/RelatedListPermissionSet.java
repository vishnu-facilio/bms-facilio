package com.facilio.permission.context.module;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class RelatedListPermissionSet extends ModuleTypePermissionSetContext {
    private Long relatedModuleId;
    private Long relatedFieldId;

    public RelatedListPermissionSet(Long moduleId, Long relatedModuleId,Long relatedFieldId,String displayname) {
        this.relatedModuleId = relatedModuleId;
        this.relatedFieldId = relatedFieldId;
        setModuleId(moduleId);
        setDisplayName(displayname);
    }
}