package com.facilio.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseSystemLookupRecord extends ModuleBaseWithCustomFields {
    private Long parentId, fieldId;
}
