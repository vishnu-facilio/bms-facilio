package com.facilio.bmsconsole.context;

public class BMSAlarmContext extends BaseAlarmContext {

    private String condition;
    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }

    private String source;
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    private long controller;
    public long getController() {
        return controller;
    }
    public void setController(long controller) {
        this.controller = controller;
    }
}
