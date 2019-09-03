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

    private long controller;
    public long getController() {
        return controller;
    }
    public void setController(long controller) {
        this.controller = controller;
    }


    @Override
    public boolean shouldIgnore() {
        if (StringUtils.isEmpty(condition) || StringUtils.isEmpty(source)) {
            return true;
        }
        return super.shouldIgnore();
    }

    @Override
    public String constructMessageKey() {
        if (StringUtils.isEmpty(condition)) {
            condition = getEventMessage();
        }
        return "BMSEvent_" + condition + "_" + source + "_" + (controller == -1 ? "empty_controller" : controller);
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
