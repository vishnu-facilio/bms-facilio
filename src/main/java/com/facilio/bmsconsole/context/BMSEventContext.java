package com.facilio.bmsconsole.context;

import org.apache.commons.lang3.StringUtils;

public class BMSEventContext extends BaseEventContext {

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

    @Override
    public String constructMessageKey() {
        return "BMSEvent_" + condition + "_" + source + "_" + (controller == null ? StringUtils.EMPTY : controller.getId());
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new BMSAlarmContext();
        }
        super.updateAlarmContext(baseAlarm, add);

        BMSAlarmContext alarm = (BMSAlarmContext) baseAlarm;
        alarm.setCondition(getCondition());
        alarm.setSource(getSource());
        alarm.setController(getController());

        return baseAlarm;
    }

    @Override
    public BaseAlarmContext.Type getEventTypeEnum() {
        return BaseAlarmContext.Type.BMS_ALARM;
    }
}
