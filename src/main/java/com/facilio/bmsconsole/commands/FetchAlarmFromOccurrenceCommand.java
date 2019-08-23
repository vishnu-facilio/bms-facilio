package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchAlarmFromOccurrenceCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<AlarmOccurrenceContext> occurrences = (List<AlarmOccurrenceContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
        if (CollectionUtils.isNotEmpty(occurrences)) {
            List<Long> alarmIds = new ArrayList<>();
            for (AlarmOccurrenceContext alarmOccurrenceContext : occurrences) {
                alarmIds.add(alarmOccurrenceContext.getAlarm().getId());
            }

            Map<Long, BaseAlarmContext> alarmMap = FieldUtil.getAsMap(NewAlarmAPI.getAlarms(alarmIds));
            for (AlarmOccurrenceContext alarmOccurrenceContext : occurrences) {
                alarmOccurrenceContext.setAlarm(alarmMap.get(alarmOccurrenceContext.getAlarm().getId()));
            }

            context.put(FacilioConstants.ContextNames.ALARM_LIST, convertToAlarmObject(occurrences));
        }
        return false;
    }

    private List<AlarmContext> convertToAlarmObject(List<AlarmOccurrenceContext> occurrences) {
        List<AlarmContext> alarms = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(occurrences)) {
            for (AlarmOccurrenceContext alarmOccurrenceContext : occurrences) {
                AlarmContext alarmContext = new AlarmContext();
                alarmContext.setId(alarmOccurrenceContext.getId());

                BaseAlarmContext baseAlarm = alarmOccurrenceContext.getAlarm();
                alarmContext.setSubject(baseAlarm.getSubject());

                alarmContext.setIsAcknowledged(alarmOccurrenceContext.isAcknowledged());
                alarmContext.setAcknowledgedBy(alarmOccurrenceContext.getAcknowledgedBy());
                alarmContext.setAcknowledgedTime(alarmOccurrenceContext.getAcknowledgedTime());
                alarmContext.setSeverity(alarmOccurrenceContext.getSeverity());
                alarmContext.setPreviousSeverity(alarmOccurrenceContext.getPreviousSeverity());
                alarmContext.setAutoClear(alarmOccurrenceContext.getAutoClear());
                alarmContext.setCreatedTime(alarmOccurrenceContext.getCreatedTime());
                alarmContext.setModifiedTime(alarmOccurrenceContext.getLastOccurredTime());

                alarmContext.setDescription(baseAlarm.getDescription());
                alarmContext.setNoOfEvents(alarmOccurrenceContext.getNoOfEvents());
                alarmContext.setResource(baseAlarm.getResource());

                alarms.add(alarmContext);
            }
        }
        return alarms;
    }
}
