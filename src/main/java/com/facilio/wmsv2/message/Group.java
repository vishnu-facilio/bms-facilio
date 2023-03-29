package com.facilio.wmsv2.message;

public enum Group {
    DEFAULT ("default", true),
    DEFAULT_SINGLE_WORKER ("default-single", false),
    SEND_MAIL_WORKER("send-mail", false),
    PRE_SEND_MAIL_WORKER("pre-send-mail", false),
    RECIEVE_MAIL_WORKER("receive-mail", false),
    SCRIPTLOG_WORKER("scriptlog", false),
    PM_PLANNER_WORKER("pm-planner", false),
    ALARM_WORKFLOW_WORKER("alarm-wf",false),
    LONG_RUNNING_WORKER("long-running-task", false);

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
