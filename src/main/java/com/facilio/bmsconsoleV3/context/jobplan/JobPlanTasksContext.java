package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class JobPlanTasksContext extends V3TaskContext {

    private JobPlanTaskSectionContext taskSection;

    public JobPlanTaskSectionContext getTaskSection() {
        return taskSection;
    }

    public void setTaskSection(JobPlanTaskSectionContext taskSection) {
        this.taskSection = taskSection;
    }

    private JobPlanTaskCategory jobPlanTaskCategory;

    public enum JobPlanTaskCategory implements FacilioEnum {
        ALL_SITE("Company"),
        ALL_BUILDING("Asset"),
        ALL_FLOOR("Location"),
        SPACE_CATEGORY("space"),
        ASSET_CATEGORY("asset")
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

//    private Boolean preRequest;
//
//    public Boolean isPreRequest() {
//        if (preRequest != null) {
//            return preRequest.booleanValue();
//        }
//        return false;
//    }
//
//    public void setPreRequest(Boolean preRequest) {
//        this.preRequest = preRequest;
//    }
//    public void getPreRequest(Boolean preRequest) {
//        this.preRequest = preRequest;
//    }
//
//    private String subject;
//    public String getSubject() {
//        return subject;
//    }
//    public void setSubject(String subject) {
//        this.subject = subject;
//    }
//
//    private String description;
//    public String getDescription() {
//        return description;
//    }
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    private Boolean attachmentRequired;
//    public Boolean getAttachmentRequired() {
//        return attachmentRequired;
//    }
//    public void setAttachmentRequired(boolean attachmentRequired) {
//        this.attachmentRequired = attachmentRequired;
//    }
//    public Boolean isAttachmentRequired() {
//        if(attachmentRequired != null) {
//            return attachmentRequired.booleanValue();
//        }
//        return false;
//    }
//
//    private Integer sequence;
//    public Integer getSequence() {
//        return sequence;
//    }
//    public void setSequence(Integer sequence) {
//        this.sequence = sequence;
//    }



}

