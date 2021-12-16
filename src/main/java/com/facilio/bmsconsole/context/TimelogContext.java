package com.facilio.bmsconsole.context;

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

    private long doneBy;
    public void setDoneBy(long doneBy) {
        this.doneBy = doneBy;
    }
    public long getDoneBy() {
        return doneBy;
    }

    private FacilioField fromStatusField;
    public void setFromStatusField(FacilioField fromStatusField) {
        this.fromStatusField = fromStatusField;
    }
    public FacilioField getFromStatusField() {
        return fromStatusField;
    }
}
