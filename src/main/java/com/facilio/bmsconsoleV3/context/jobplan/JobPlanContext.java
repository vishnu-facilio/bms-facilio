package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class JobPlanContext extends V3Context {

    private String name;
    private JobPlanCategory jobPlanCategory;

    public enum JobPlanCategory implements FacilioEnum {
        ALL_SITE("Company"),
        ALL_BUILDING("Asset"),
        ALL_FLOOR("Location"),
        SPACE_CATEGORY("space"),
        ASSET_CATEGORY("asset")
        ;
        private String name;

        JobPlanCategory(String name) {
            this.name = name;
        }

        public static JobPlanCategory valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public void setJobPlanCategory(Integer type) {
        if (type != null) {
            this.jobPlanCategory = JobPlanCategory.valueOf(type);
        }
    }

    public JobPlanCategory getJobPlanCategoryEnum() {
        return jobPlanCategory;
    }
    public Integer getJobPlanCategory() {
        if (jobPlanCategory != null) {
            return jobPlanCategory.getIndex();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<JobPlanTaskSectionContext> taskSectionList;

    public List<JobPlanTaskSectionContext> getTaskSectionList() {
        return taskSectionList;
    }

    public void setTaskSectionList(List<JobPlanTaskSectionContext> taskSectionList) {
        this.taskSectionList = taskSectionList;
    }

    private Long assetCategoryId;

    public Long getAssetCategoryId() {
        return assetCategoryId;
    }

    public void setAssetCategoryId(Long assetCategoryId) {
        this.assetCategoryId = assetCategoryId;
    }

    public Long getSpaceCategoryId() {
        return spaceCategoryId;
    }

    public void setSpaceCategoryId(Long spaceCategoryId) {
        this.spaceCategoryId = spaceCategoryId;
    }

    private Long spaceCategoryId;
}
