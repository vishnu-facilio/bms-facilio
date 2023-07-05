package com.facilio.alarms.sensor.commands;

import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.SensorRulePropContext;
import com.facilio.alarms.sensor.util.NewSensorRuleUtil;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class PrepareSensorRuleForUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        SensorRuleContext sensorRule= (SensorRuleContext) recordMap.get(moduleName).get(0);

        if(CollectionUtils.isNotEmpty(sensorRule.getSensorRuleTypes())){
            List<SensorRulePropContext> sensorRuleTypes=sensorRule.getSensorRuleTypes();
            context.put(FacilioConstants.ContextNames.SENSOR_RULE_TYPES,sensorRuleTypes);
        }
        context.put(FacilioConstants.ContextNames.SENSOR_RULE_MODULE,sensorRule);
        return false;
    }
}
