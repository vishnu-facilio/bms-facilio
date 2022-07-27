package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsole.context.PlannedMaintenance.PMScopeAssigmentType;
import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class JobPlanContext extends V3Context {

    private String name;
    private PMScopeAssigmentType jobPlanCategory;

    public void setJobPlanCategory(Integer type) {
        if (type != null) {
            this.jobPlanCategory = PMScopeAssigmentType.valueOf(type);
        }
    }

    public PMScopeAssigmentType getJobPlanCategoryEnum() {
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
}
