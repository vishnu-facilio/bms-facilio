package com.facilio.faults;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class LoadOccurrenceForAlarmCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<BaseAlarmContext> alarmContexts = (List<BaseAlarmContext>) recordMap.get(moduleName);
        for (BaseAlarmContext alarmContext : alarmContexts) {
            long lastOccurrenceId = alarmContext.getLastOccurrenceId();
            AlarmOccurrenceContext alarmOccurrence = NewAlarmAPI.getAlarmOccurrence(lastOccurrenceId);
            alarmContext.setPrevOccurrence(alarmOccurrence);
        }
        return false;
    }
}