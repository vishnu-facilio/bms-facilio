package com.facilio.bmsconsole.context;

public class AlarmNoteContext extends NoteContext {
    public long getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(long occurrenceId) {
        this.occurrenceId = occurrenceId;
    }

    long occurrenceId;
}
