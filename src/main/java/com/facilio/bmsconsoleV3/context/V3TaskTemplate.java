package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.modules.FieldUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V3TaskTemplate extends Template {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String description;

    List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts;

    public List<PMIncludeExcludeResourceContext> getPmIncludeExcludeResourceContexts() {
        return pmIncludeExcludeResourceContexts;
    }
    public void setPmIncludeExcludeResourceContexts(List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts) {
        this.pmIncludeExcludeResourceContexts = pmIncludeExcludeResourceContexts;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private Boolean isPreRequest = false;

    public Boolean isPreRequest() {
        return isPreRequest;
    }

    public void setPreRequest(Boolean isPreRequest) {
        this.isPreRequest = isPreRequest;
    }
    private V3TaskContext.TaskStatus status;
    public V3TaskContext.TaskStatus getStatusEnum() {
        return status;
    }
    public void setStatus(V3TaskContext.TaskStatus status) {
        this.status = status;
    }
    public int getStatus() {
        if (status != null) {
            return status.getValue();
        }
        return -1;
    }
    public void setStatus(int status) {
        this.status = V3TaskContext.TaskStatus.valueOf(status);
    }

    private Long priorityId;
    public Long getPriorityId() {
        return priorityId;
    }
    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    private Long categoryId;
    public Long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    private Long typeId ;
    public Long getTypeId() {
        return typeId;
    }
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    private Long assignmentGroupId;
    public Long getAssignmentGroupId() {
        return assignmentGroupId;
    }
    public void setAssignmentGroupId(long assignmentGroupId) {
        this.assignmentGroupId = assignmentGroupId;
    }

    private Long assignedToId ;
    public Long getAssignedToId() {
        return assignedToId;
    }
    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    private Long resourceId;
    public Long getResourceId() {
        return resourceId;
    }
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    private Long duration ;
    public Long getDuration() {
        return duration;
    }
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    private Long parentTemplateId;
    public Long getParentTemplateId() {
        return parentTemplateId;
    }
    public void setParentTemplateId(Long parentTemplateId) {
        this.parentTemplateId = parentTemplateId;
    }

    private Long jobPlanId ;
    public Long getJobPlanId() {
        return jobPlanId;
    }
    public void setJobPlanId(Long jobPlanId) {
        this.jobPlanId = jobPlanId;
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

    private Long readingFieldId;
    public Long getReadingFieldId() {
        return readingFieldId;
    }
    public void setReadingFieldId(Long readingId) {
        this.readingFieldId = readingId;
    }

    private Long sectionId ;
    public Long getSectionId() {
        return sectionId;
    }
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    private Integer sequence;
    public Integer getSequence() {
        return sequence;
    }
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    private V3TaskTemplate.AttachmentRequiredEnum attachmentRequiredEnum;
    public V3TaskTemplate.AttachmentRequiredEnum getAttachmentRequiredEnum() {
        return attachmentRequiredEnum;
    }

    public void setAttachmentRequiredInt(int attachmentRequired) {
        attachmentRequiredEnum = V3TaskTemplate.AttachmentRequiredEnum.valueOf(attachmentRequired);
    }
    public int getAttachmentRequiredInt() {
        if(attachmentRequiredEnum != null) {
            return attachmentRequiredEnum.getVal();
        }
        return -1;
    }

    public void setAttachmentRequired(boolean attachmentRequired) {
        if(attachmentRequired) {
            attachmentRequiredEnum = V3TaskTemplate.AttachmentRequiredEnum.TRUE;
        }
        else {
            attachmentRequiredEnum = V3TaskTemplate.AttachmentRequiredEnum.FALSE;
        }
    }

    public Boolean getAttachmentRequired() {
        if(attachmentRequiredEnum != null) {
            if(attachmentRequiredEnum.equals(V3TaskTemplate.AttachmentRequiredEnum.TRUE)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    public Boolean isAttachmentRequired() {
        if(attachmentRequiredEnum != null) {
            if(attachmentRequiredEnum.equals(V3TaskTemplate.AttachmentRequiredEnum.TRUE)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private Long siteId;
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

    public V3TaskContext getTask() {
        if (getName() != null && !getName().isEmpty()) {
            Map<String, Object> taskProp = new HashMap<>();

            taskProp.put("subject", getName());
            taskProp.put("description", description);
            taskProp.put("duration", duration);
            taskProp.put("readingFieldId", readingFieldId);
            taskProp.put("sequence", sequence);
            taskProp.put("attachmentRequired", isAttachmentRequired());
            taskProp.put("statusNew", getStatus());

            if (priorityId != null) {
                taskProp.put("priority", FieldUtil.getEmptyLookedUpProp(priorityId));
            }
            if (categoryId != null) {
                taskProp.put("category", FieldUtil.getEmptyLookedUpProp(categoryId));
            }
            if (typeId != null) {
                taskProp.put("type", FieldUtil.getEmptyLookedUpProp(typeId));
            }
            if (assignmentGroupId != null) {
                taskProp.put("assignmentGroup", FieldUtil.getEmptyLookedUpProp(assignmentGroupId));
            }
            if (assignedToId != null) {
                taskProp.put("assignedTo", FieldUtil.getEmptyLookedUpProp(assignedToId));
            }
            if (resourceId != null) {
                taskProp.put("resource", FieldUtil.getEmptyLookedUpProp(resourceId));
            }
            if (inputType != null) {
                taskProp.put("inputType", inputType.getVal());
            }
            if (siteId != null) {
                taskProp.put("siteId", siteId);
            }

            taskProp.put("sectionId", sectionId);
            if (additionInfo != null) {
                taskProp.putAll(additionInfo);
            }
            taskProp.put("readingRules", readingRules);

            return FieldUtil.getAsBeanFromMap(taskProp, V3TaskContext.class);
        }
        return null;
    }
    private List<ReadingRuleContext> readingRules;
    public List<ReadingRuleContext> getReadingRules() {
        return readingRules;
    }
    public void setReadingRules(List<ReadingRuleContext> readingRules) {
        this.readingRules = readingRules;
    }
    public void setTask(V3TaskContext task) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (task != null) {
            setName(task.getSubject());
            description = task.getDescription();
            inputType = task.getInputTypeEnum();
            readingFieldId = task.getReadingFieldId();
            sectionId = task.getSectionId();
            sequence = task.getSequence();
            setAttachmentRequired(task.isAttachmentRequired());
            status = task.getStatusNewEnum();
            if (task.getResource() != null) {
                resourceId = task.getResource().getId();
            }

            if (task.getSiteId() != -1) {
                siteId = task.getSiteId();
            }

            Map<String, Object> prop = FieldUtil.getAsProperties(task);
            prop.remove("id");
            prop.remove("subject");
            prop.remove("description");
            prop.remove("duration");
            prop.remove("status");
            prop.remove("priority");
            prop.remove("category");
            prop.remove("type");
            prop.remove("assignmentGroup");
            prop.remove("assignedTo");
            prop.remove("resource");
            prop.remove("inputType");
            prop.remove("readingFieldId");
            prop.remove("sectionId");
            prop.remove("sequence");
            prop.remove("attachmentRequired");
            prop.remove("siteId");
            additionInfo = new JSONObject();
            additionInfo.putAll(prop);
        }
    }

    @Override
    public JSONObject getOriginalTemplate() {
        // TODO Auto-generated method stub
        return null;
    }
    public long getSiteId() {
        return siteId;
    }
    public void setSiteId(long siteId) {
        this.siteId = siteId;
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

    public enum AttachmentRequiredEnum {
        FALSE,
        TRUE,
        USE_PARENT,
        ;

        public int getVal() {
            return ordinal();
        }

        public static V3TaskTemplate.AttachmentRequiredEnum valueOf(int val) {
            if(val > 0 && val <= values().length) {
                return values()[val];
            }
            return null;
        }
    }
}
