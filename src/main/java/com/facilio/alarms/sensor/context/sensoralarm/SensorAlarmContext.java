package com.facilio.alarms.sensor.context.sensoralarm;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SensorAlarmContext extends BaseAlarmContext {

    private SensorRuleContext sensorRule;
    private long readingFieldId;
    private SensorRuleType sensorRuleType;
    private Boolean meterRollUp;
    private BigDecimal totalDuration;
    private BigDecimal averageFrequencyFailure;

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

}
