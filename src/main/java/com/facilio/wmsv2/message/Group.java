package com.facilio.wmsv2.message;

public enum Group {
    DEFAULT ("default", true),
    DEFAULT_SINGLE_WORKER ("default-single", false),
    SEND_MAIL_WORKER("send-mail", false),
    RECIEVE_MAIL_WORKER("receive-mail", false),
    SCRIPTLOG_WORKER("scriptlog", false),
    PM_PLANNER_WORKER("pm-planner", false);

    String name;
    boolean sendToAllWorker;

    Group(String name, boolean sendToAllWorker) {
        this.name = name;
        this.sendToAllWorker = sendToAllWorker;
    }

    public String getName() {
        return name;
    }

    public boolean isSendToAllWorker() {
        return sendToAllWorker;
    }
}
