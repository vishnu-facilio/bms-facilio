package com.facilio.remotemonitoring.commands;

import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.command.FacilioCommand;
import com.facilio.remotemonitoring.context.AlarmTypeContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateAlarmTypeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmTypeContext> alarmTypes = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(alarmTypes)) {
            for(AlarmTypeContext alarmType : alarmTypes) {
                if(alarmType.isUncategorisedAlarm()) {
                    throw new IllegalArgumentException("Modifying undefined alarm type is not allowed");
                }
            }
        }
        return false;
    }
}
