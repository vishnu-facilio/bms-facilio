package com.facilio.alarms.sensor.context;

import com.facilio.bmsconsole.context.ResourceContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.Serializable;

@Getter
@Setter
@Log4j
public class SensorRuleAlarmMeta implements Serializable {

    private static final long serialVersionUID = 1L;
    private long id = -1;
    private long orgId = -1;
    private long alarmId = -1;
    private long ruleGroupId = -1;
    private ResourceContext resource;
    private long resourceId = -1;
    private long readingFieldId = -1;
    private int sensorRuleType;
    private Boolean clear;
    private String subject;

    public boolean isClear() {
        return (clear != null) ? clear.booleanValue() : false;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return new StringBuilder("Alarm Meta [id:").append(id)
                .append(", alarmId: ").append(alarmId)
                .append(", ruleId: ").append(ruleGroupId)
                .append(", resourceId: ").append(resourceId)
                .append(", readingFieldId: ").append(readingFieldId)
                .append(", clear: ").append(clear)
                .toString();
    }
}

