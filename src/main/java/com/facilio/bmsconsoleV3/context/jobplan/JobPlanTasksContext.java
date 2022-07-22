package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FacilioIntEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void setOptions(List<String> options) {
        if(options != null && !options.isEmpty()) {
            addAdditionInfo("options",options);
        }
    }

    // declarations for enableInput
    private Boolean enableInput;
    public Boolean getEnableInput(){
        if(getAdditionInfo().containsKey("enableInput")){
            return (Boolean) getAdditionInfo().get("enableInput");
        }
        return enableInput;
    }
    public void setEnableInput(Boolean enableInput) {
        if((enableInput != null && enableInput) || (getInputType() > 0)) {
            addAdditionInfo("enableInput", enableInput);
        }
        this.enableInput = enableInput;
    }

    // declarations for createWoOnFailure
    private Boolean createWoOnFailure;
    public Boolean getCreateWoOnFailure() {
        if(getAdditionInfo().containsKey("createWoOnFailure")){
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
        if(getAdditionInfo().containsKey("attachmentOption")){
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
        if(getAdditionInfo().containsKey("remarkOption")){
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
        if(getAdditionInfo().containsKey("woCreateFormId")) {
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

}

