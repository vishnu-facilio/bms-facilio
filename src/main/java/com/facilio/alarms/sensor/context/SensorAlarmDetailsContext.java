package com.facilio.alarms.sensor.context;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.Serializable;

@Setter
@Getter
@Log4j
public class SensorAlarmDetailsContext implements Serializable {

     Long id;
     Long orgId;
     Long sensorRuleId;
     String message;
     String severity;
     Long severityId;

}
