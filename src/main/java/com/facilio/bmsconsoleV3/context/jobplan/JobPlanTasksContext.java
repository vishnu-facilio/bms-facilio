package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class JobPlanTasksContext extends TaskContext {

    private JobPlanTaskSectionContext taskSection;

    public JobPlanTaskSectionContext getTaskSection() {
        return taskSection;
    }

    public void setTaskSection(JobPlanTaskSectionContext taskSection) {
        this.taskSection = taskSection;
    }

    private JobPlanTaskCategory jobPlanTaskCategory;

    public enum JobPlanTaskCategory implements FacilioEnum {
        ALL_FLOORS("All Floors"),
        ALL_SPACES("All Spaces"),
        SPACE_CATEGORY("Space Category"),
        ASSET_CATEGORY("Asset Category"),
        CURRENT_ASSET("Current Asset"),
        SPECIFIC_ASSET("Specific Asset"),
        ALL_BUILDINGS("All Buildings"),
        ALL_SITES("All Sites")
        ;
        private String name;

        JobPlanTaskCategory(String name) {
            this.name = name;
        }

        public static JobPlanTaskCategory valueOf(int value) {
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

    public void setJobPlanTaskCategory(Integer type) {
        if (type != null) {
            this.jobPlanTaskCategory = JobPlanTaskCategory.valueOf(type);
        }
    }

    public JobPlanTaskCategory getJobPlanTaskCategoryEnum() {
        return jobPlanTaskCategory;
    }
    public Integer getJobPlanTaskCategory() {
        if (jobPlanTaskCategory != null) {
            return jobPlanTaskCategory.getIndex();
        }
        return null;
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

    private JobPlanContext jobPlan;

    public JobPlanContext getJobPlan() {
        return jobPlan;
    }

    public void setJobPlan(JobPlanContext jobPlan) {
        this.jobPlan = jobPlan;
    }


}

