package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.facilio.command.FacilioCommand;
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
            List<Long> readingCategoryId = new ArrayList<>();
            for (AlarmOccurrenceContext alarmOccurrenceContext : occurrences) {
                BaseAlarmContext alarm = alarmOccurrenceContext.getAlarm();
                alarmIds.add(alarm.getId());
                if (alarm instanceof ReadingAlarm) {
                    ReadingAlarmCategoryContext readingAlarmCategory = ((ReadingAlarm) alarm).getReadingAlarmCategory();
                    if (readingAlarmCategory != null) {
                        readingCategoryId.add(readingAlarmCategory.getId());
                    }
                }
            }

            Map<Long, ReadingAlarmCategoryContext> categoryMap = FieldUtil.getAsMap(NewAlarmAPI.getReadingAlarmCategory(readingCategoryId));
            Map<Long, BaseAlarmContext> alarmMap = FieldUtil.getAsMap(NewAlarmAPI.getAlarms(alarmIds));
            for (AlarmOccurrenceContext alarmOccurrenceContext : occurrences) {
                BaseAlarmContext alarm = alarmOccurrenceContext.getAlarm();
                alarmOccurrenceContext.setAlarm(alarmMap.get(alarm.getId()));
                if (alarm instanceof ReadingAlarm) {
                    ReadingAlarmCategoryContext readingAlarmCategory = ((ReadingAlarm) alarm).getReadingAlarmCategory();
                    if (readingAlarmCategory != null) {
                        ((ReadingAlarm) alarm).setReadingAlarmCategory(categoryMap.get(((ReadingAlarm) alarm).getReadingAlarmCategory().getId()));
                    }
                }
            }

            context.put(FacilioConstants.ContextNames.ALARM_LIST, convertToAlarmObject(occurrences));
        }
        return false;
    }

    private List<AlarmContext> convertToAlarmObject(List<AlarmOccurrenceContext> occurrences) throws Exception {
        List<AlarmContext> alarms = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(occurrences)) {
            for (AlarmOccurrenceContext alarmOccurrenceContext : occurrences) {
                AlarmContext alarmContext = new AlarmContext();
                alarmContext.setId(alarmOccurrenceContext.getId());
                alarmContext.setSerialNumber(alarmOccurrenceContext.getId());

                BaseAlarmContext baseAlarm = alarmOccurrenceContext.getAlarm();
                alarmContext.setSubject(baseAlarm.getSubject());

                alarmContext.setIsAcknowledged(alarmOccurrenceContext.isAcknowledged());
                alarmContext.setAcknowledgedBy(alarmOccurrenceContext.getAcknowledgedBy());
                alarmContext.setAcknowledgedTime(alarmOccurrenceContext.getAcknowledgedTime());
                alarmContext.setSeverity(alarmOccurrenceContext.getSeverity());
                alarmContext.setPreviousSeverity(alarmOccurrenceContext.getPreviousSeverity());
                alarmContext.setAutoClear(alarmOccurrenceContext.getAutoClear());
                alarmContext.setCreatedTime(alarmOccurrenceContext.getCreatedTime());
                alarmContext.setModifiedTime(baseAlarm.getLastOccurredTime());

                alarmContext.setDescription(baseAlarm.getDescription());
                alarmContext.setNoOfEvents(alarmOccurrenceContext.getNoOfEvents());
                AssetContext asset = AssetsAPI.getAssetInfo(baseAlarm.getResource().getId());
                if (asset != null) {
                    alarmContext.setResource(asset);
                    alarmContext.setSource(asset.getName());
                }

                if (baseAlarm instanceof ReadingAlarm) {
                    ReadingAlarmCategoryContext readingAlarmCategory = ((ReadingAlarm) baseAlarm).getReadingAlarmCategory();
                    if (readingAlarmCategory != null) {
                        TicketCategoryContext category = new TicketCategoryContext();
                        category.setName(readingAlarmCategory.getName());
                        category.setDisplayName(readingAlarmCategory.getDisplayName());
                        category.setId(readingAlarmCategory.getId());
                        alarmContext.setCategory(category);
                    }
                    ReadingRuleInterface rule = ((ReadingAlarm) baseAlarm).getRule();
                    if (rule != null) {
                        alarmContext.setCondition(rule.getName());
                    }
                }

                alarms.add(alarmContext);
            }
        }
        return alarms;
    }
}
