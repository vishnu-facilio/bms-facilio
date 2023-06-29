package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3TaskSectionContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private Boolean preRequest;

    public Boolean getPreRequest() {
        if(preRequest != null){
            return preRequest.booleanValue();
        }
        return false;
    }

    public Boolean isPreRequest() {
        return preRequest;
    }

    public void setPreRequest(Boolean preRequest) {
        this.preRequest = preRequest;
    }

    private Boolean isEditable;
    public Boolean getIsEditable() {
        return isEditable;
    }
    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }
    public boolean isEditable() {
        if (isEditable != null) {
            return isEditable.booleanValue();
        }
        return false;
    }

    private Long sequenceNumber;
    public Long getSequenceNumber() {
        return sequenceNumber;
    }
    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    private List<Long> taskIds;
    public List<Long> getTaskIds() {
        return taskIds;
    }
    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }
}
