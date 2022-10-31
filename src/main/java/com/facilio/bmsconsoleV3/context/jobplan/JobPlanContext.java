package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class JobPlanContext extends V3Context {

    private String name;
    private PlannedMaintenance.PMScopeAssigmentType jobPlanCategory;

    public void setJobPlanCategory(Integer type) {
        if (type != null) {
            this.jobPlanCategory = PlannedMaintenance.PMScopeAssigmentType.valueOf(type);
        }
    }

    public PlannedMaintenance.PMScopeAssigmentType getJobPlanCategoryEnum() {
        return jobPlanCategory;
    }
    public Integer getJobPlanCategory() {
        if(jobPlanCategory != null) {
            return jobPlanCategory.getVal();
        }
        return -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<JobPlanTaskSectionContext> jobplansection;

    public List<JobPlanTaskSectionContext> getJobplansection() {
        return jobplansection;
    }

    public void setJobplansection(List<JobPlanTaskSectionContext> jobplansection) {
        this.jobplansection = jobplansection;
    }

    private V3AssetCategoryContext assetCategory;

    public V3SpaceCategoryContext spaceCategory;

    public V3AssetCategoryContext getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(V3AssetCategoryContext assetCategory) {
        this.assetCategory = assetCategory;
    }

    public V3SpaceCategoryContext getSpaceCategory() {
        return spaceCategory;
    }

    public void setSpaceCategory(V3SpaceCategoryContext spaceCategory) {
        this.spaceCategory = spaceCategory;
    }


    /*
        When JobPlan is published isActive is set to true, after that isActive cannot be made false.
     */
    @Getter
    @Setter
    private Boolean isActive;

    /*
        isDisabled is used to track if JobPlan could be used or not.
     */
    @Getter
    @Setter
    private Boolean isDisabled;
}
