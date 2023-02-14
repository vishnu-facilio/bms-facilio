package com.facilio.bmsconsole.context;

public class BMSAlarmContext extends BaseAlarmContext {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

    private String alarmClass;
    public String getAlarmClass() {
        return alarmClass;
    }
    public void setAlarmClass(String alarmClass) {
        this.alarmClass = alarmClass;
    }
}
