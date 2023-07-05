package com.facilio.alarms.sensor.context.sensoralarm;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class SensorAlarmOccurrenceContext extends AlarmOccurrenceContext {

    private SensorRuleContext sensorRule;
    private long readingFieldId;
    private SensorRuleType sensorRuleType;
    private Boolean meterRollUp;

    public SensorRuleType getSensorRuleTypeEnum() {
        return sensorRuleType;
    }

    public int getSensorRuleType() {
        return sensorRuleType != null ? sensorRuleType.getIndex() : -1;
    }

    public void setSensorRuleType(SensorRuleType sensorRuleType) {
        this.sensorRuleType = sensorRuleType;
    }

    public void setSensorRuleType(int sensorRuleType) {
        this.sensorRuleType = SensorRuleType.valueOf(sensorRuleType);
    }

    public Type getTypeEnum() {
        return Type.SENSOR;
    }
}
