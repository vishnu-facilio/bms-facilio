package com.facilio.alarms.sensor.commands;

import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetRecordModuleAndFieldIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SensorRuleContext sensorRule = (SensorRuleContext) context.get(FacilioConstants.ContextNames.SENSOR_RULE_MODULE);

        if (sensorRule != null) {
            sensorRule.setRecordModuleId((Long) context.get(FacilioConstants.ContextNames.MODULE_ID));

            List moduleFieldIds = (List) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS);
            if (CollectionUtils.isNotEmpty(moduleFieldIds)) {
                sensorRule.setRecordFieldId((Long) moduleFieldIds.get(0));
            }

        }
        return false;
    }
}
