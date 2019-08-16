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

    private ControllerContext controller;
    public ControllerContext getController() {
        return controller;
    }
    public void setController(ControllerContext controller) {
        this.controller = controller;
    }
}
