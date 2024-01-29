package com.facilio.permission.context;

import com.facilio.permission.context.BasePermissionContext;
import com.facilio.permission.context.PermissionSetContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PermissionSetSandboxContext extends PermissionSetContext {
    private List<BasePermissionContext> basePermissionContext;
    private List<Long> peopleIds;
}
