package com.facilio.agent;

/**
 * This enum is to map Controller Commands with a int value.
 * ControllerCommandtells the command of messages which are being sent and received.
 * Use this enum to handle commands in database.
 *
 */
public enum ControllerCommand
{
    set("set",1),
    get("get",2),
    subscribe("subscribe",3),
    unsubscribe("unsubscribe",4),
    configure("configure",5),
    refresh("refresh",6),
    reconfigure("reconfigure",7),
    discoverpoints("discoverpoints",8);

    private String command;
    private int value;

    public String getCommand() {
        return command;
    }

    public int getValue() {
        return value;
    }

    ControllerCommand(String command, int value) {
        this.command = command;
        this.value = value;
    }
}
