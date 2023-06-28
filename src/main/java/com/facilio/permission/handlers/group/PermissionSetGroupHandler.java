package com.facilio.permission.handlers.group;

import com.facilio.permission.context.BasePermissionContext;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public interface PermissionSetGroupHandler <T extends BasePermissionContext>{

    List<T> getPermissions(Long groupId) throws Exception;

    Map<String,Long> paramsResolver(Map<String, String> httpParametersMap) throws Exception;

    boolean getDefaultValue(Map<String,Long> queryProp) throws Exception;

    boolean getIsDisabled(Map<String,Long> queryProp) throws Exception;

    boolean showParent(Long groupId) throws Exception;

}