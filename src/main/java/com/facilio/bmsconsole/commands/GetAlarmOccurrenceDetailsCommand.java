package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAlarmOccurrenceDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordId = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

        List<AlarmOccurrenceContext> alarmOccurrences = NewAlarmAPI.getAlarmOccurrences(recordId);
        context.put(FacilioConstants.ContextNames.RECORD_LIST, alarmOccurrences);
        return false;
    }
}
