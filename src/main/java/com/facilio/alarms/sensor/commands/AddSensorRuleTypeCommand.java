package com.facilio.alarms.sensor.commands;

import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.SensorRulePropContext;
import com.facilio.alarms.sensor.util.NewSensorRuleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddSensorRuleTypeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SensorRuleContext sensorRule= (SensorRuleContext) context.get(FacilioConstants.ContextNames.SENSOR_RULE_MODULE);

        List<SensorRulePropContext> sensorRuleTypes = (List<SensorRulePropContext>) context.get(FacilioConstants.ContextNames.SENSOR_RULE_TYPES);

        if (CollectionUtils.isNotEmpty(sensorRuleTypes)) {
            NewSensorRuleUtil.setParentIdForRuleTypes(sensorRuleTypes, sensorRule);
            NewSensorRuleUtil.addSensorRuleProp(sensorRuleTypes);
        }

        return false;
    }
}
