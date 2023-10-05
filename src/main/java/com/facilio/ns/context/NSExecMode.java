package com.facilio.ns.context;

public enum NSExecMode {
    NONE,
    CODE_EXEC,
    WORKFLOW,
    WEAVE;

    public int getValue() {
        return ordinal() + 1;
    }

    public static NSExecMode valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
}
