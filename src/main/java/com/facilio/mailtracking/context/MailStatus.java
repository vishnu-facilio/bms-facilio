package com.facilio.mailtracking.context;

public enum MailStatus {
    IN_PROGRESS,
    SENT,
    DELIVERED,
    BOUNCED;

    public int getValue() {
        return ordinal() + 1;
    }

}
