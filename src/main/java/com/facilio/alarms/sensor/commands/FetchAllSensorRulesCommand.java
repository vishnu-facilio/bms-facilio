package com.facilio.alarms.sensor.commands;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.SensorRulePropContext;
import com.facilio.alarms.sensor.util.NewSensorRuleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class FetchAllSensorRulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<SensorRulePropContext> sensorRuleTypes = new ArrayList<>();
        SensorRuleContext sensorRule = new SensorRuleContext();
        for (SensorRuleType type : SensorRuleType.values()) {
            NewSensorRuleUtil.addSensorRuleType(sensorRuleTypes, type, null, false);
        }
        sensorRule.setSensorRuleTypes(sensorRuleTypes);
        context.put(FacilioConstants.ContextNames.SENSOR_RULE_MODULE, sensorRule);
        return false;
    }

}
