package com.facilio.bmsconsole.enums;

public enum OfflineUpdateType {
    RECORD(1, "RECORD"),
    ATTACHMENT(2, "ATTACHMENT"),
    TASK(3, "TASK");

    int intVal;
    String name;
    public int getIntVal() {
        return intVal;
    }
    public String getName() {
        return name;
    }
    private OfflineUpdateType(int intVal, String name) {
        this.intVal = intVal;
        this.name = name;
    }
}
