package com.facilio.agent;

/**
 * this enum is to map CommandStatus with a int value.
 * Command-Status tells the status of messages which are being sent and received.
 * Use this enum to handle publish-types in database.
 *
 */
public enum CommandStatus
{
    PROCESSING("Processing",1),
    IGNORED("Ignored",2),
    EXECUTED("Executed",3),
    SENT("Sent",4),
    CONNECTED("Connected",5),
    DISCONNECTED("Disconnected",6);

    private String status;
    private int key;

    public String getStatus() {
        return status;
    }

    public int getKey() {
        return key;
    }

    CommandStatus(String status, int key) {
        this.status = status;
        this.key = key;
    }
}
