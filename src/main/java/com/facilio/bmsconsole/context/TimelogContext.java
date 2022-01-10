package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;

public class TimelogContext {
    private long id = -1;
    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    private long moduleId = -1;
    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }
    public long getModuleId() {
        return moduleId;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private long parentId = -1;
    public long getParentId() {
        return parentId;
    }
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    private long fromStatusId = -1;
    public void setFromStatusId(long fromStatusId) {
        this.fromStatusId = fromStatusId;
    }
    public long getFromStatusId() {
        return fromStatusId;
    }

    private long toStatusId = -1;
    public void setToStatusId(long toStatusId) {
        this.toStatusId = toStatusId;
    }
    public long getToStatusId() {
        return toStatusId;
    }

    private long startTime = -1;
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public long getStartTime() {
        return startTime;
    }

    private long endTime = -1;
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public long getEndTime() {
        return endTime;
    }

    private long duration = -1;
    public void setDuration(long duration) {
        this.duration = duration;
    }
    public long getDuration() {
        return duration;
    }

    private Boolean timerEnabled;
    public void setTimerEnabled(Boolean timerEnabled) {
        this.timerEnabled = timerEnabled;
    }
    public Boolean getTimerEnabled() {
        return timerEnabled;
    }

    private long doneById = -1;
    public void setDoneById(long doneById) {
        this.doneById = doneById;
    }
    public long getDoneById() {
        return doneById;
    }

    private FacilioStatus fromStatus;
    public void setFromStatus(FacilioStatus fromStatus) {
        this.fromStatus = fromStatus;
    }
    public FacilioStatus getFromStatus() {
        return fromStatus;
    }

    private User doneBy;
    public void setDoneBy(User doneBy) {
        this.doneBy = doneBy;
    }
    public User getDoneBy() {
        return doneBy;
    }
}
