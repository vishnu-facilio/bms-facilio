package com.facilio.bmsconsoleV3.context.workpermit;

import com.facilio.v3.context.V3Context;

public class WorkPermitTypeChecklistContext extends V3Context {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String item;
    private String description;
    private WorkPermitTypeContext workPermitType;
    private ValidationType validationType;
    private WorkPermitTypeChecklistCategoryContext category;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public WorkPermitTypeChecklistCategoryContext getCategory() {
        return category;
    }

    public void setCategory(WorkPermitTypeChecklistCategoryContext category) {
        this.category = category;
    }

    public Integer getValidationType() {
        return validationType.getIndex();
    }

    public ValidationType getValidationTypeEnum() {
        return validationType;
    }

    public void setValidationType(Integer validationType) {
        if (validationType != null) {
            this.validationType = ValidationType.valueOf(validationType);
        }
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public WorkPermitTypeContext getWorkPermitType() {
        return workPermitType;
    }

    public void setWorkPermitType(WorkPermitTypeContext workPermitType) {
        this.workPermitType = workPermitType;
    }

}
