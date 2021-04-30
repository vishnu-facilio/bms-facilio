package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class JobPlanContext extends V3Context {

    private String name;
    private JobPlanCategory jobPlanCategory;

    public enum JobPlanCategory implements FacilioIntEnum {
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
        public Integer getIndex() {
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
