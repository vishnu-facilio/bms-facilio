package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
}
