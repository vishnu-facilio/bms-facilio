package com.facilio.alarms.sensor.commands;

import com.facilio.alarms.sensor.context.SensorAlarmDetailsContext;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

public class AddSensorAlarmDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        SensorRuleContext sensorRule = (SensorRuleContext) context.get(FacilioConstants.ContextNames.SENSOR_RULE_MODULE);

        SensorAlarmDetailsContext sensorAlarmDetails = sensorRule.getSensorAlarmDetails();
        if (sensorAlarmDetails != null) {

            AlarmSeverityContext alarmSeverity = AlarmAPI.getAlarmSeverity(sensorAlarmDetails.getSeverity());
            V3Util.throwRestException(alarmSeverity == null, ErrorCode.ERROR, "Invalid severity value found!");

            sensorAlarmDetails.setSeverityId(alarmSeverity.getId());
            sensorAlarmDetails.setSensorRuleId(sensorRule.getId());

            GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                    .fields(FieldFactory.getSensorAlarmDetailFields())
                    .table(ModuleFactory.getSensorAlarmDetailsModule().getTableName());
            insertRecordBuilder.insert(FieldUtil.getAsProperties(sensorAlarmDetails));
            insertRecordBuilder.save();
        }

        return false;
    }
}
