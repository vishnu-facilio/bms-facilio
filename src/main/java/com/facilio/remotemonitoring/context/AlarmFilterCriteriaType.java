package com.facilio.remotemonitoring.context;

import com.facilio.modules.FacilioStringEnum;
import com.facilio.remotemonitoring.handlers.*;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public enum AlarmFilterCriteriaType implements FacilioStringEnum {
    ALARM_OPEN_FOR_SPECIFIED_DURATION("Alarm open for specified duration", ImmutableMap.of(AlarmApproach.RETURN_TO_NORMAL, new AlarmOpenForDurationOfTimeRTNHandler())),
    ALARM_COUNTS_IN_A_PERIOD_OF_TIME("Alarm counts in a period of time", ImmutableMap.of(AlarmApproach.RETURN_TO_NORMAL, new AlarmCountsInAPeriodOfTimeRTNHandler(), AlarmApproach.REPEAT_UNTIL_RESOLVED, new AlarmCountsInAPeriodOfTimeRURHandler())),
    ALARM_COUNTS_IN_A_PERIOD_OF_TIME_OR_OPEN_FOR_DURATION("Alarm open for specified duration or counts in a period of time", ImmutableMap.of(AlarmApproach.RETURN_TO_NORMAL, new AlarmCountsInAPeriodOfTimeOrOpenForDurationHandler())),
    NO_ALARM_RECEIVED_FOR_SPECIFIC_PERIOD("No alarm received for a specific duration", ImmutableMap.of(AlarmApproach.RETURN_TO_NORMAL, new NoAlarmReceivedForDurationOfTimeRTNHandler(), AlarmApproach.REPEAT_UNTIL_RESOLVED, new NoAlarmReceivedForDurationOfTimeRURHandler())),
    DO_NOT_FORWARD("Do no forward", ImmutableMap.of(AlarmApproach.RETURN_TO_NORMAL, new DoNotForwardHandler(), AlarmApproach.REPEAT_UNTIL_RESOLVED, new DoNotForwardHandler())),
    ALARM_ROLL_UP("Roll Up", ImmutableMap.of(AlarmApproach.RETURN_TO_NORMAL, new RollupAlarmHandler(), AlarmApproach.REPEAT_UNTIL_RESOLVED, new RollupAlarmHandler()));

    @Getter
    @Setter
    private Map<AlarmApproach, AlarmCriteriaHandler> handlerMap;

    @Getter
    @Setter
    private String displayName;

    @Override
    public String getValue() {
        return displayName;
    }

    AlarmFilterCriteriaType(String displayName, Map<AlarmApproach, AlarmCriteriaHandler> handlerMap) {
        this.displayName = displayName;
        this.handlerMap = handlerMap;
    }

    public AlarmCriteriaHandler getHandler(RawAlarmContext rawAlarm) {
        if (MapUtils.isNotEmpty(handlerMap)) {
            return handlerMap.get(rawAlarm.getAlarmApproachEnum());
        }
        return null;
    }
}