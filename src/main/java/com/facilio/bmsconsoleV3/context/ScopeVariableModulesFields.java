package com.facilio.bmsconsoleV3.context;

import java.io.Serializable;

public class ScopeVariableModulesFields implements Serializable {
    private long id = -1L;
    private Long moduleId;
    private String fieldName;
    private Long scopeVariableId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Long getScopeVariableId() {
        return scopeVariableId;
    }

    public void setScopeVariableId(Long scopeVariableId) {
        this.scopeVariableId = scopeVariableId;
    }

}
