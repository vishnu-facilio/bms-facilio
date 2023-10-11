package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

@Getter
@Setter
public class JobPlanTasksContext extends V3TaskContext {
	
	private V3AssetCategoryContext assetCategory;

    private V3SpaceCategoryContext spaceCategory;

    private static Logger log = LogManager.getLogger(JobPlanTasksContext.class.getName());

    private JobPlanTaskSectionContext taskSection;

    public JobPlanTaskSectionContext getTaskSection() {
        return taskSection;
    }

    public void setTaskSection(JobPlanTaskSectionContext taskSection) {
        this.taskSection = taskSection;
    }

    private PreventiveMaintenance.PMAssignmentType jobPlanTaskCategory;

    public void setJobPlanTaskCategory(Integer type) {
        if (type != null) {
            this.jobPlanTaskCategory = PreventiveMaintenance.PMAssignmentType.valueOf(type);
        }
    }

    public PreventiveMaintenance.PMAssignmentType getJobPlanTaskCategoryEnum() {
        return jobPlanTaskCategory;
    }
    public Integer getJobPlanTaskCategory() {
        if (jobPlanTaskCategory != null) {
            return jobPlanTaskCategory.getIndex();
        }
        return null;
    }

    private JobPlanContext jobPlan;

    public JobPlanContext getJobPlan() {
        return jobPlan;
    }

    public void setJobPlan(JobPlanContext jobPlan) {
        this.jobPlan = jobPlan;
    }

    /**
     * Re-declaring/Redefining the variables from {@link V3TaskContext }, for handling the properties in additionInfo.
     */
    // definition of getter and setter for options
//    public List<String> getOptions() {
//        if(super.getOptions() == null && getAdditionInfo().containsKey("options")){
//            return  (List<String>) getAdditionInfo().get("options");
//        }
//        return super.getOptions();
//    }
//    public void setOptions(List<String> options) {
//        if(options != null && !options.isEmpty()) {
//            addAdditionInfo("options",options);
//        }
//        super.setOptions(options);
//    }

    // declarations for enableInput
    private Boolean enableInput;
    public Boolean getEnableInput(){
        if(enableInput == null && getAdditionInfo().containsKey("enableInput")){
            return (Boolean) getAdditionInfo().get("enableInput");
        }
        return enableInput;
    }
    public void setEnableInput(Boolean enableInput) {
        if(enableInput != null) {
            addAdditionInfo("enableInput", enableInput);
        }
        this.enableInput = enableInput;
    }

    // declarations for attachmentOption
    private String attachmentOption;
    public String getAttachmentOption() {
        if(attachmentOption == null && getAdditionInfo().containsKey("attachmentOption")){
            return (String) getAdditionInfo().get("attachmentOption");
        }
        return attachmentOption;
    }
    public void setAttachmentOption(String attachmentOption) {
        if(attachmentOption != null){
            addAdditionInfo("attachmentOption", attachmentOption);
        }
        this.attachmentOption = attachmentOption;
    }

    // declarations for remarkOption
    private String remarkOption;
    public String getRemarkOption() {
        if(remarkOption == null && getAdditionInfo().containsKey("remarkOption")){
            return (String) getAdditionInfo().get("remarkOption");
        }
        return remarkOption;
    }
    public void setRemarkOption(String remarkOption) {
        if(remarkOption != null){
            addAdditionInfo("remarkOption", remarkOption);
        }
        this.remarkOption = remarkOption;
    }

    // declarations for woCreateFormId
    private Long woCreateFormId;
    public Long getWoCreateFormId() {
        if(woCreateFormId == null && getAdditionInfo().containsKey("woCreateFormId")) {
            return (Long) getAdditionInfo().get("woCreateFormId");
        }
        return woCreateFormId;
    }
    public void setWoCreateFormId(Long woCreateFormId) {
        if(woCreateFormId != null && woCreateFormId >0){
            addAdditionInfo("woCreateFormId", woCreateFormId);
        }
        this.woCreateFormId = woCreateFormId;
    }

    // declarations for validation
    private String validation;
    public String getValidation() {
        if(validation == null && getAdditionInfo().containsKey("validation")){
            return (String) getAdditionInfo().get("validation");
        }
        return validation;
    }
    public void setValidation(String validation) {
        if(validation != null){
            addAdditionInfo("validation", validation);
        }
        this.validation = validation;
    }

    // declarations for minSafeLimit
    private String minSafeLimit;
    public String getMinSafeLimit() {
        if(minSafeLimit == null && getAdditionInfo().containsKey("minSafeLimit")){
            return (String) getAdditionInfo().get("minSafeLimit");
        }
        return minSafeLimit;
    }
    public void setMinSafeLimit(String minSafeLimit) {
        if(minSafeLimit != null){
            addAdditionInfo("minSafeLimit", minSafeLimit);
        }
        this.minSafeLimit = minSafeLimit;
    }

    // declarations for maxSafeLimit
    private String maxSafeLimit;
    public String getMaxSafeLimit() {
        if(maxSafeLimit == null && getAdditionInfo().containsKey("maxSafeLimit")){
            return (String) getAdditionInfo().get("maxSafeLimit");
        }
        return maxSafeLimit;
    }
    public void setMaxSafeLimit(String maxSafeLimit) {
        if(maxSafeLimit != null){
            addAdditionInfo("maxSafeLimit", maxSafeLimit);
        }
        this.maxSafeLimit = maxSafeLimit;
    }

    @Getter
    @Setter
    private List<Map<String, Object>> inputOptions;


    @Getter
    @Setter
    private Long taskCode;
}

