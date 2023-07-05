package com.facilio.alarms.sensor.commands;

import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.SensorRulePropContext;
import com.facilio.alarms.sensor.util.NewSensorRuleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateSensorRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SensorRuleContext sensorRule = (SensorRuleContext) context.get(FacilioConstants.ContextNames.SENSOR_RULE_MODULE);

        List<SensorRulePropContext> sensorRuleTypes = (List<SensorRulePropContext>) context.get(FacilioConstants.ContextNames.SENSOR_RULE_TYPES);

        filterRuleTypesAndUpdate(sensorRuleTypes);
        filterRuleTypesAndAdd(sensorRuleTypes, sensorRule);

        return false;
    }

    private void filterRuleTypesAndUpdate(List<SensorRulePropContext> sensorRuleTypes) throws Exception {
        List<SensorRulePropContext> existingRuleType = sensorRuleTypes.stream().filter(ruleType -> (ruleType.getId() != null && ruleType.getId() > 0)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(existingRuleType)) {
            NewSensorRuleUtil.updateSensorRuleType(existingRuleType);
        }

    }

    private void filterRuleTypesAndAdd(List<SensorRulePropContext> sensorRuleTypes, SensorRuleContext sensorRule) throws Exception {
        List<SensorRulePropContext> newRuleType = sensorRuleTypes.stream().filter(ruleType -> ruleType.getId() == null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newRuleType)) {
            NewSensorRuleUtil.setParentIdForRuleTypes(sensorRuleTypes, sensorRule);
            NewSensorRuleUtil.addSensorRuleProp(newRuleType);
        }
    }
}
