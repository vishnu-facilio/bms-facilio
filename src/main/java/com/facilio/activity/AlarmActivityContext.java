package com.facilio.activity;

public class AlarmActivityContext extends  ActivityContext {

    public long getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(long occurrenceId) {
        this.occurrenceId = occurrenceId;
    }

    private long occurrenceId = -1;

}

