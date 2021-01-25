package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;
import java.util.Map;

public class JobPlanTaskSectionContext extends V3Context {


    private Long assetCategoryId;
    private Long spaceCategoryId;


    private JobPlanContext jobPlan;

    public JobPlanContext getJobPlan() {
        return jobPlan;
    }

    public void setJobPlan(JobPlanContext jobPlan) {
        this.jobPlan = jobPlan;
    }

    private JobPlanSectionCategory jobPlanSectionCategory;

    public enum JobPlanSectionCategory implements FacilioEnum {
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

        JobPlanSectionCategory(String name) {
            this.name = name;
        }

        public static JobPlanSectionCategory valueOf(int value) {
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

    public void setJobPlanSectionCategory(Integer type) {
        if (type != null) {
            this.jobPlanSectionCategory = JobPlanSectionCategory.valueOf(type);
        }
    }

    public JobPlanSectionCategory getJobPlanSectionCategoryEnum() {
        return jobPlanSectionCategory;
    }
    public Integer getJobPlanSectionCategory() {
        if (jobPlanSectionCategory != null) {
            return jobPlanSectionCategory.getIndex();
        }
        return null;
    }

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

    private Integer sequenceNumber ;
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<Map<String, Object>> tasks;

    public List<Map<String, Object>> getTasks() {
        return tasks;
    }

    public void setTasks(List<Map<String, Object>> tasks) {
        this.tasks = tasks;
    }


}
