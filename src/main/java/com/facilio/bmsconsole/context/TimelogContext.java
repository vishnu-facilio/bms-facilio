package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;

public class TimelogContext extends V3Context{

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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

    private Boolean isTimerEnabled;

    public Boolean getIsTimerEnabled() {
        return isTimerEnabled;
    }

    public void setIsTimerEnabled(Boolean timerEnabled) {
        isTimerEnabled = timerEnabled;
    }

    private ModuleBaseWithCustomFields parent;

    public void setParent(ModuleBaseWithCustomFields parent) {
        this.parent = parent;
    }

    public ModuleBaseWithCustomFields getParent() {
        return parent;
    }
    private FacilioStatus fromStatus;

    public FacilioStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(FacilioStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    private FacilioStatus toStatus;

    public FacilioStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(FacilioStatus toStatus) {
        this.toStatus = toStatus;
    }

    private User doneBy;
    public void setDoneBy(User doneBy) {
        this.doneBy = doneBy;
    }
    public User getDoneBy() {
        return doneBy;
    }
}
