package com.facilio.permission.context.module;

import com.facilio.permission.context.BasePermissionContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Setter
@Getter
public class ModuleTypePermissionSetContext extends BasePermissionContext {
    private Long moduleId;
}