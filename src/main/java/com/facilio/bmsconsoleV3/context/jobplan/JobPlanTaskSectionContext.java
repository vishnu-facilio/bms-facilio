package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
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

    public enum JobPlanSectionCategory implements FacilioIntEnum {
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
        public Integer getIndex() {
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

    // declarations for InputType
    private JobPlanTaskSectionContext.InputType inputType;
    public int getInputType() {
        if(inputType != null) {
            return inputType.getVal();
        }
        return -1;
    }
    public void setInputType(int inputType) {
        this.inputType = JobPlanTaskSectionContext.InputType.valueOf(inputType);
    }
    public void setInputType(JobPlanTaskSectionContext.InputType inputType) {
        this.inputType = inputType;
    }
    public JobPlanTaskSectionContext.InputType getInputTypeEnum() {
        return inputType;
    }

    public enum InputType {
        NONE,
        READING,
        TEXT,
        NUMBER,
        RADIO,
        //CHECKBOX,
        BOOLEAN
        ;

        public int getVal() {
            return ordinal()+1;
        }

        public static JobPlanTaskSectionContext.InputType valueOf(int val) {
            if(val > 0 && val <= values().length) {
                return values()[val - 1];
            }
            return null;
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

    /**
     * Declaring the variables for handling the properties in additionInfo.
     */

    // declarations for options
    private List<String> options;
    public List<String> getOptions() {
        if(options == null && getAdditionInfo().containsKey("options")){
            return (List<String>) getAdditionInfo().get("options");
        }
        return options;
    }
    public void setOptions(List<String> options) {
        addStringListToAdditionInfo("options",options);
        this.options = options;
    }

    // declarations for attachmentRequired
    private Boolean attachmentRequired;
    public Boolean getAttachmentRequired() {
        if(attachmentRequired == null && getAdditionInfo().containsKey("attachmentRequired")){
            return (Boolean) getAdditionInfo().get("attachmentRequired");
        }
        return attachmentRequired;
    }
    public void setAttachmentRequired(Boolean attachmentRequired) {
        this.attachmentRequired = attachmentRequired;
        addAdditionInfo("attachmentRequired", attachmentRequired);
    }
    public Boolean isAttachmentRequired() {
        if(attachmentRequired != null) {
            return attachmentRequired;
        }
        return false;
    }

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

    // declarations for defaultValue
    private String defaultValue;
    public String getDefaultValue() {
        if(defaultValue == null && getAdditionInfo().containsKey("defaultValue")) {
            return (String) getAdditionInfo().get("defaultValue");
        }
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        if(defaultValue != null) {
            addAdditionInfo("defaultValue", defaultValue);
        }
        this.defaultValue = defaultValue;
    }

    // declarations for failureValue
    private String failureValue;
    public String getFailureValue() {
        if(failureValue == null && getAdditionInfo().containsKey("failureValue")) {
            return (String) getAdditionInfo().get("failureValue");
        }
        return failureValue;
    }
    public void setFailureValue(String failureValue) {
        if(failureValue != null) {
            addAdditionInfo("failureValue", failureValue);
        }
        this.failureValue = failureValue;
    }

    // declarations for deviationOperator
    private Integer deviationOperatorId;
    public Integer getDeviationOperatorId() {
        if(deviationOperatorId == null && getAdditionInfo().containsKey("deviationOperatorId")) {
            return ((Long) getAdditionInfo().get("deviationOperatorId")).intValue();
        }else if(deviationOperator != null) {
            return deviationOperator.getOperatorId();
        }
        return deviationOperatorId;
    }
    public void setDeviationOperatorId(Integer operatorId) {
        if(operatorId != null && operatorId > 0){
            addAdditionInfo("deviationOperatorId", operatorId);
        }
        this.deviationOperatorId = operatorId;
        this.deviationOperator = operatorId != null && operatorId > 0 ? Operator.getOperator(operatorId) : null;
    }

    private Operator deviationOperator;
    @JsonIgnore
    public Operator getDeviationOperator() {
        return deviationOperator;
    }
    public void setDeviationOperator(Operator operator) {
        this.deviationOperatorId = operator.getOperatorId();
        this.deviationOperator = operator;
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

    // declarations for remarksRequired
    private Boolean remarksRequired;
    public Boolean getRemarksRequired() {
        if(remarksRequired == null && getAdditionInfo().containsKey("remarksRequired")){
            return (Boolean) getAdditionInfo().get("remarksRequired");
        }
        return remarksRequired;
    }
    public void setRemarksRequired(Boolean remarksRequired) {
        addAdditionInfo("remarksRequired", remarksRequired);
        this.remarksRequired = remarksRequired;
    }
    public Boolean isRemarksRequired() {
        if(remarksRequired != null) {
            return remarksRequired;
        }
        return false;
    }

    // declarations for remarkOptionValues
    private List<String> remarkOptionValues;
    public List<String> getRemarkOptionValues() throws Exception {
        if(remarkOptionValues ==null && getAdditionInfo().containsKey("remarkOptionValues")){
            return (List<String>) getAdditionInfo().get("remarkOptionValues");
        } else if (remarkOptionValuesString != null && remarkOptionValuesString.length() > 0) {
            List<String> valueList = new ArrayList<String>(Arrays.asList(remarkOptionValuesString.split(",")));
            return valueList;
        }
        return remarkOptionValues;
    }
    public void setRemarkOptionValues(List<String> remarkOptionValues) {
        addStringListToAdditionInfo("remarkOptionValues", remarkOptionValues);
        this.remarkOptionValues = remarkOptionValues;
    }

    // declarations for remarkOptionValuesString
    private String remarkOptionValuesString;
    public String getRemarkOptionValuesString() {
        if(remarkOptionValues ==null && getAdditionInfo().containsKey("remarkOptionValuesString")){
            return (String) getAdditionInfo().get("remarkOptionValuesString");
        } else if (remarkOptionValues != null && remarkOptionValues.size() != 0) {
            return StringUtils.join(remarkOptionValues, ",");
        }
        return remarkOptionValuesString;
    }
    public void setRemarkOptionValuesString(String remarkOptionValuesString) {
        addAdditionInfo("remarkOptionValuesString", remarkOptionValuesString);
        this.remarkOptionValuesString = remarkOptionValuesString;
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

    // declarations for attachmentOptionValues
    private List<String> attachmentOptionValues;
    public List<String> getAttachmentOptionValues() throws Exception {
        if(attachmentOptionValues == null && getAdditionInfo().containsKey("attachmentOptionValues")){
            return (List<String>) getAdditionInfo().get("attachmentOptionValues");
        }
        return attachmentOptionValues;
    }
    public void setAttachmentOptionValues(List<String> attachmentOptionValues) {
        addStringListToAdditionInfo("attachmentOptionValues", attachmentOptionValues);
        this.attachmentOptionValues = attachmentOptionValues;
    }

    // declaration of attachmentOptionValuesString
    private String attachmentOptionValuesString;
    public void setAttachmentOptionValuesString(String attachmentOptionValuesString) {
        if (StringUtils.isNotEmpty(attachmentOptionValuesString)) {
            this.attachmentOptionValues = new ArrayList<String>(Arrays.asList(attachmentOptionValuesString.split(",")));
            addAdditionInfo("attachmentOptionValuesString", this.attachmentOptionValuesString);
        }
    }

    // getter for attachmentOptionValues as string
    public String getAttachmentOptionValuesString() {
        if(attachmentOptionValuesString ==null && getAdditionInfo().containsKey("attachmentOptionValuesString")){
            return (String) getAdditionInfo().get("attachmentOptionValuesString");
        } else if (CollectionUtils.isNotEmpty(attachmentOptionValues)) {
            return StringUtils.join(attachmentOptionValues, ",");
        }
        return null;
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

    // Helper function to add list of string to additionInfo
    private void addStringListToAdditionInfo(String key, List<String> list){
        if(key != null && list != null && list.size() >0){
            addAdditionInfo(key, list);
        }
    }

}
