package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;

public class TimelogContext {
    private long id;
    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    private long moduleId;
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

    private long parentId;
    public long getParentId() {
        return parentId;
    }
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    private long fromStatusId;
    public void setFromStatusId(long fromStatusId) {
        this.fromStatusId = fromStatusId;
    }
    public long getFromStatusId() {
        return fromStatusId;
    }

    private long toStatusId;
    public void setToStatusId(long toStatusId) {
        this.toStatusId = toStatusId;
    }
    public long getToStatusId() {
        return toStatusId;
    }

    private long startTime;
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public long getStartTime() {
        return startTime;
    }

    private long endTime;
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public long getEndTime() {
        return endTime;
    }

    private long duration;
    public void setDuration(long duration) {
        this.duration = duration;
    }
    public long getDuration() {
        return duration;
    }

    private boolean isTimerEnabled;
    public void setTimerEnabled(boolean timerEnabled) {
        isTimerEnabled = timerEnabled;
    }
    public boolean isTimerEnabled() {
        return isTimerEnabled;
    }

    private long doneById;
    public void setDoneById(long doneById) {
        this.doneById = doneById;
    }
    public long getDoneById() {
        return doneById;
    }

    private FacilioField fromStatus;
    public void setFromStatus(FacilioField fromStatus) {
        this.fromStatus = fromStatus;
    }
    public FacilioField getFromStatus() {
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
