package com.facilio.alarms.sensor.commands;

import com.facilio.alarms.sensor.context.SensorAlarmDetailsContext;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.SensorRulePropContext;
import com.facilio.alarms.sensor.util.NewSensorRuleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchSensorRuleSummaryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<SensorRuleContext> sensorRules = recordMap.get(moduleName);

        for (SensorRuleContext sensorRule : sensorRules) {

            List<SensorRulePropContext> sensorRuleTypes = getSensorRuleTypes(sensorRule);
            if(sensorRuleTypes!=null) {
                sensorRule.setSensorRuleTypes(sensorRuleTypes);
            }
            SensorAlarmDetailsContext sensorAlarmDetails=NewSensorRuleUtil.getSensorAlarmDetails(sensorRule.getId());
            sensorRule.setSensorAlarmDetails(sensorAlarmDetails);
            NewSensorRuleUtil.setCategory(sensorRule);

            NameSpaceContext nameSpaceContext = NamespaceAPI.getNameSpaceByRuleId(sensorRule.getId(), NSType.SENSOR_RULE);
            sensorRule.setNs(nameSpaceContext);

        }
        return false;
    }

    private List<SensorRulePropContext> getSensorRuleTypes(SensorRuleContext sensorRule) throws Exception {

        List<SensorRulePropContext> sensorRuleTypes = NewSensorRuleUtil.getSensorRuleValidatorPropsByParentRuleId(sensorRule.getId(), true);

        if (CollectionUtils.isNotEmpty(sensorRuleTypes)) {
            return sensorRuleTypes;
        }
        return null;
    }
}
