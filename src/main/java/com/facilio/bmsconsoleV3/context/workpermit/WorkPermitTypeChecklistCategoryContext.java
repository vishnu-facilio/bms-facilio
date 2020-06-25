package com.facilio.bmsconsoleV3.context.workpermit;

import com.facilio.v3.context.V3Context;

public class WorkPermitTypeChecklistCategoryContext extends V3Context {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private ValidationType validationType;
    private WorkPermitTypeContext workPermitType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getValidationType() {
        if (validationType != null) {
            return validationType.getIndex();
        }
        return null;
    }

    public ValidationType getValidationTypeEnum() {
        return validationType;
    }

    public void setValidationType(Integer validationType) {
        if (validationType != null) {
            this.validationType = ValidationType.valueOf(validationType);
        }
    }

    public WorkPermitTypeContext getWorkPermitType() {
        return workPermitType;
    }

    public void setWorkPermitType(WorkPermitTypeContext workPermitType) {
        this.workPermitType = workPermitType;
    }
}
