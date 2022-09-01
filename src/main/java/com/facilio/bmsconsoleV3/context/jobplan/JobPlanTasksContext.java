package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class JobPlanTasksContext extends V3TaskContext {

    private static Logger log = LogManager.getLogger(JobPlanTasksContext.class.getName());

    private JobPlanTaskSectionContext taskSection;

    public JobPlanTaskSectionContext getTaskSection() {
        return taskSection;
    }

    public void setTaskSection(JobPlanTaskSectionContext taskSection) {
        this.taskSection = taskSection;
    }

    private JobPlanTaskCategory jobPlanTaskCategory;

    public enum JobPlanTaskCategory implements FacilioIntEnum {
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
        public Integer getIndex() {
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

    // declarations for additionalInfoJsonStr
    private String additionalInfoJsonStr;
    public String getAdditionalInfoJsonStr() {
        if(additionInfo != null) {
            return additionInfo.toJSONString();
        }
        return null;
    }
    public void setAdditionalInfoJsonStr(String jsonStr) throws ParseException {
        if(jsonStr != null) {
            JSONParser parser = new JSONParser();
            additionInfo = (JSONObject) parser.parse(jsonStr);
        }
    }

    // declarations for additionInfo
    private JSONObject additionInfo;
    public JSONObject getAdditionInfo() {
        if(additionInfo == null){
            return  new JSONObject();
        }
        return additionInfo;
    }
    public void setAdditionInfo(JSONObject additionInfo) {
        this.additionInfo = additionInfo;
    }

    public void addAdditionInfo(String key, Object value) {
        if(this.additionInfo == null) {
            this.additionInfo =  new JSONObject();
        }
        this.additionInfo.put(key,value);
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

    // declarations for createWoOnFailure
    private Boolean createWoOnFailure;
    public Boolean getCreateWoOnFailure() {
        if(createWoOnFailure == null && getAdditionInfo().containsKey("createWoOnFailure")){
            return (Boolean) getAdditionInfo().get("createWoOnFailure");
        }
        return createWoOnFailure;
    }
    public void setCreateWoOnFailure(Boolean createWoOnFailure) {
        addAdditionInfo("createWoOnFailure", createWoOnFailure);
        this.createWoOnFailure = createWoOnFailure;
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
}

