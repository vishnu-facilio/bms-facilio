package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class V3TaskSectionTemplate extends V3TaskTemplate {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Boolean isPreRequestSection = false;

    public Boolean isPreRequestSection() {
        return isPreRequestSection;
    }

    public void setPreRequestSection(Boolean isPreRequestSection) {
        this.isPreRequestSection = isPreRequestSection;
    }
    List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts;

    public List<PMIncludeExcludeResourceContext> getPmIncludeExcludeResourceContexts() {
        return pmIncludeExcludeResourceContexts;
    }
    public void setPmIncludeExcludeResourceContexts(List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts) {
        this.pmIncludeExcludeResourceContexts = pmIncludeExcludeResourceContexts;
    }

    private List<PMTaskSectionTemplateTriggers> pmTaskSectionTemplateTriggers;

    public List<PMTaskSectionTemplateTriggers> getPmTaskSectionTemplateTriggers() {
        return pmTaskSectionTemplateTriggers;
    }
    public void setPmTaskSectionTemplateTriggers(List<PMTaskSectionTemplateTriggers> pmTaskSectionTemplateTriggers) {
        this.pmTaskSectionTemplateTriggers = pmTaskSectionTemplateTriggers;
    }

    public void addPmTaskSectionTemplateTriggers(PMTaskSectionTemplateTriggers pmTaskSectionTemplateTrigger) {
        this.pmTaskSectionTemplateTriggers = this.pmTaskSectionTemplateTriggers == null ? new ArrayList<>() :this.pmTaskSectionTemplateTriggers;
        this.pmTaskSectionTemplateTriggers.add(pmTaskSectionTemplateTrigger);
    }

    public List<PMTriggerContext> getPmTriggerContexts() {
        return pmTriggerContexts;
    }
    public void setPmTriggerContexts(List<PMTriggerContext> pmTriggerContexts) {
        this.pmTriggerContexts = pmTriggerContexts;
    }

    public void addPmTriggerContext(PMTriggerContext pmTriggerContext) {
        this.pmTriggerContexts = pmTriggerContexts == null ? new ArrayList<>() : this.pmTriggerContexts;
        this.pmTriggerContexts.add(pmTriggerContext);
    }

    private List<PMTriggerContext> pmTriggerContexts;


    private Boolean isEditable;
    public Boolean getIsEditable() {
        return isEditable;
    }
    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }
    public Boolean isEditable() {
        if (isEditable != null) {
            return isEditable.booleanValue();
        }
        return false;
    }

    private V3TaskContext.InputType inputType;
    public int getInputType() {
        if(inputType != null) {
            return inputType.getVal();
        }
        return -1;
    }
    public void setInputType(int inputType) {
        this.inputType = V3TaskContext.InputType.valueOf(inputType);
    }
    public void setInputType(V3TaskContext.InputType inputType) {
        this.inputType = inputType;
    }
    public V3TaskContext.InputType getInputTypeEnum() {
        return inputType;
    }

    private Boolean attachmentRequired;
    public Boolean getAttachmentRequired() {
        return attachmentRequired;
    }
    public void setAttachmentRequired(boolean attachmentRequired) {
        this.attachmentRequired = attachmentRequired;
    }
    public Boolean isAttachmentRequired() {
        if(attachmentRequired != null) {
            return attachmentRequired.booleanValue();
        }
        return false;
    }

    private Long parentWOTemplateId ;
    public Long getParentWOTemplateId() {
        return parentWOTemplateId;
    }
    public void setParentWOTemplateId(Long parentWOTemplateId) {
        this.parentWOTemplateId = parentWOTemplateId;
    }

    private Long jobPlanId;
    public Long getJobPlanId() {
        return jobPlanId;
    }
    public void setJobPlanId(Long jobPlanId) {
        this.jobPlanId = jobPlanId;
    }

    private Long sequenceNumber ;
    public Long getSequenceNumber() {
        return sequenceNumber;
    }
    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    private List<V3TaskContext> tasks;
    public List<V3TaskContext> getTasks() {
        if(tasks == null && taskTemplates != null && !taskTemplates.isEmpty()) {
            for(V3TaskTemplate taskTemplate :taskTemplates) {
                addTasks(taskTemplate.getTask());
            }
        }
        return tasks;
    }
    public void setTasks(List<V3TaskContext> tasks) {
        this.tasks = tasks;
    }

    public void addTasks(V3TaskContext task) {

        this.tasks = this.tasks == null ? new ArrayList<>() : this.tasks;
        this.tasks.add(task);
    }

    private List<V3TaskTemplate> taskTemplates;

    public List<V3TaskTemplate> getTaskTemplates() {
        return taskTemplates;
    }
    public void setTaskTemplates(List<V3TaskTemplate> taskTemplates) {
        this.taskTemplates = taskTemplates;
    }
    public void addTaskTemplates(V3TaskTemplate taskTemplate) {
        this.taskTemplates = this.taskTemplates == null ? new ArrayList<>() : this.taskTemplates;
        this.taskTemplates.add(taskTemplate);
    }
    @Override
    public JSONObject getOriginalTemplate() {
        // TODO Auto-generated method stub
        return null;
    }
    Long assetCategoryId;
    Long spaceCategoryId;

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

    PreventiveMaintenance.PMAssignmentType assignmentType;

    public int getAssignmentType() {
        if(assignmentType != null) {
            return assignmentType.getVal();
        }
        return -1;
    }
    public void setAssignmentType(int assignmentType) {
        this.assignmentType = PreventiveMaintenance.PMAssignmentType.valueOf(assignmentType);
    }

    Long resourceId;
    public Long getResourceId() {
        return resourceId;
    }
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

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
